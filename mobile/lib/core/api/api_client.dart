import 'dart:ui';

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../config.dart';
import 'api_exception.dart';

class AppLocaleNotifier extends Notifier<Locale> {
  @override
  Locale build() => const Locale('en');

  void set(Locale locale) => state = locale;
}

/// Effective language the app is showing; synced from the resolved
/// MaterialApp locale so the Accept-Language header matches the UI.
final appLocaleProvider = NotifierProvider<AppLocaleNotifier, Locale>(
  AppLocaleNotifier.new,
);

class PingResult {
  const PingResult({required this.status, required this.message});

  factory PingResult.fromJson(Map<String, dynamic> json) => PingResult(
    status: json['status'] as String,
    message: json['message'] as String,
  );

  final String status;
  final String message;
}

Dio buildDio({
  required String baseUrl,
  required Locale Function() localeGetter,
}) {
  final dio = Dio(BaseOptions(baseUrl: baseUrl));
  dio.interceptors.add(
    InterceptorsWrapper(
      onRequest: (options, handler) {
        options.headers['Accept-Language'] = localeGetter().languageCode;
        handler.next(options);
      },
    ),
  );
  return dio;
}

class ApiClient {
  ApiClient(this._dio);

  final Dio _dio;

  Future<PingResult> ping() async {
    try {
      final response = await _dio.get<Map<String, dynamic>>('/api/v1/ping');
      return PingResult.fromJson(response.data!);
    } on DioException catch (e) {
      final data = e.response?.data;
      throw ApiException(
        statusCode: e.response?.statusCode,
        detail: data is Map ? data['detail'] as String? : null,
      );
    }
  }
}

final dioProvider = Provider<Dio>(
  (ref) => buildDio(
    baseUrl: AppConfig.apiBaseUrl,
    localeGetter: () => ref.read(appLocaleProvider),
  ),
);

final apiClientProvider = Provider<ApiClient>(
  (ref) => ApiClient(ref.watch(dioProvider)),
);
