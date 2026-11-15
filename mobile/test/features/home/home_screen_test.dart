import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:gym_leveling/app.dart';
import 'package:gym_leveling/core/api/api_client.dart';
import 'package:gym_leveling/core/api/api_exception.dart';
import 'package:material_ui/material_ui.dart';

class FakeApiClient implements ApiClient {
  FakeApiClient(this.handler);
  final Future<PingResult> Function() handler;
  int calls = 0;

  @override
  Future<PingResult> ping() {
    calls++;
    return handler();
  }
}

Future<void> pumpApp(WidgetTester tester, FakeApiClient fake) async {
  await tester.pumpWidget(
    ProviderScope(
      overrides: [apiClientProvider.overrideWithValue(fake)],
      child: const GymLevelingApp(),
    ),
  );
  await tester.pumpAndSettle();
}

void main() {
  setUp(() => GoogleFonts.config.allowRuntimeFetching = false);
  tearDown(() {
    TestWidgetsFlutterBinding.ensureInitialized()
        .platformDispatcher
        .clearLocalesTestValue();
  });

  testWidgets('homeShowsServerMessage', (tester) async {
    final fake = FakeApiClient(
      () async => const PingResult(status: 'ok', message: 'Xin chào từ Gym Leveling'),
    );
    await pumpApp(tester, fake);
    expect(find.text('Xin chào từ Gym Leveling'), findsOneWidget);
  });

  testWidgets('homeShowsErrorWhenApiFails', (tester) async {
    final fake = FakeApiClient(() async => throw const ApiException());
    await pumpApp(tester, fake);
    expect(find.text("Can't connect to server"), findsOneWidget);
    expect(fake.calls, 1);
    await tester.tap(find.text('Retry'));
    await tester.pumpAndSettle();
    expect(fake.calls, 2);
  });

  testWidgets('unsupportedDeviceLocaleFallsBackToEnglish', (tester) async {
    tester.platformDispatcher.localesTestValue = [const Locale('ja')];
    final fake = FakeApiClient(() async => throw const ApiException());
    await pumpApp(tester, fake);
    expect(find.text("Can't connect to server"), findsOneWidget);
  });

  testWidgets('viDeviceLocaleShowsVietnamese', (tester) async {
    tester.platformDispatcher.localesTestValue = [const Locale('vi', 'VN')];
    final fake = FakeApiClient(() async => throw const ApiException());
    await pumpApp(tester, fake);
    expect(find.text('Không kết nối được máy chủ'), findsOneWidget);
    expect(find.text('Thử lại'), findsOneWidget);
  });
}
