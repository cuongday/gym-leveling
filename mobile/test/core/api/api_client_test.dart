import 'dart:ui';

import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gym_leveling/core/api/api_client.dart';
import 'package:gym_leveling/core/api/api_exception.dart';
import 'package:http_mock_adapter/http_mock_adapter.dart';

void main() {
  late Dio dio;
  late DioAdapter adapter;
  var locale = const Locale('en');

  setUp(() {
    locale = const Locale('en');
    dio = buildDio(baseUrl: 'http://test', localeGetter: () => locale);
    adapter = DioAdapter(dio: dio);
  });

  test('pingParsesResponse', () async {
    adapter.onGet(
      '/api/v1/ping',
      (s) => s.reply(200, {'status': 'ok', 'message': 'Hello from Gym Leveling'}),
    );
    final result = await ApiClient(dio).ping();
    expect(result.message, 'Hello from Gym Leveling');
    expect(result.status, 'ok');
  });

  test('pingSendsAcceptLanguage', () async {
    locale = const Locale('vi');
    adapter.onGet(
      '/api/v1/ping',
      (s) => s.reply(200, {'status': 'ok', 'message': 'x'}),
      headers: {'Accept-Language': 'vi'},
    );
    final result = await ApiClient(dio).ping();
    expect(result.message, 'x');
  });

  test('problemDetailBecomesApiException', () async {
    adapter.onGet(
      '/api/v1/ping',
      (s) => s.reply(500, {'status': 500, 'detail': 'boom'}),
    );
    expect(
      ApiClient(dio).ping(),
      throwsA(isA<ApiException>().having((e) => e.detail, 'detail', 'boom')),
    );
  });

  test('connectionErrorBecomesApiException', () async {
    adapter.onGet(
      '/api/v1/ping',
      (s) => s.throws(
        0,
        DioException.connectionError(
          requestOptions: RequestOptions(path: '/api/v1/ping'),
          reason: 'down',
        ),
      ),
    );
    expect(
      ApiClient(dio).ping(),
      throwsA(isA<ApiException>().having((e) => e.statusCode, 'statusCode', isNull)),
    );
  });
}
