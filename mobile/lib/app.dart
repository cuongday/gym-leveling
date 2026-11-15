import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:material_ui/material_ui.dart';

import 'core/api/api_client.dart';
import 'core/l10n/app_localizations.dart';
import 'core/router.dart';
import 'core/theme.dart';

class GymLevelingApp extends ConsumerWidget {
  const GymLevelingApp({super.key, this.locale});

  final Locale? locale;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return MaterialApp.router(
      onGenerateTitle: (context) => AppLocalizations.of(context)!.appTitle,
      theme: lightTheme,
      darkTheme: darkTheme,
      themeMode: ThemeMode.system,
      routerConfig: appRouter,
      locale: locale,
      builder: (context, child) {
        // Keep Accept-Language in sync with the language actually shown.
        final resolved = Localizations.localeOf(context);
        if (ref.read(appLocaleProvider) != resolved) {
          Future.microtask(
            () => ref.read(appLocaleProvider.notifier).set(resolved),
          );
        }
        return child ?? const SizedBox.shrink();
      },
      supportedLocales: const [Locale('en'), Locale('vi')],
      localizationsDelegates: [
        AppLocalizations.delegate,
        ...GlobalMaterialLocalizations.delegates,
      ],
      localeResolutionCallback: (deviceLocale, supported) =>
          deviceLocale?.languageCode == 'vi'
          ? const Locale('vi')
          : const Locale('en'),
    );
  }
}
