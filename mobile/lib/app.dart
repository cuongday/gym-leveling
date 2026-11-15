import 'package:material_ui/material_ui.dart';

import 'core/l10n/app_localizations.dart';
import 'core/router.dart';
import 'core/theme.dart';

class GymLevelingApp extends StatelessWidget {
  const GymLevelingApp({super.key, this.locale});

  final Locale? locale;

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      onGenerateTitle: (context) => AppLocalizations.of(context)!.appTitle,
      theme: lightTheme,
      darkTheme: darkTheme,
      themeMode: ThemeMode.system,
      routerConfig: appRouter,
      locale: locale,
      supportedLocales: const [Locale('en'), Locale('vi')],
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      localeResolutionCallback: (deviceLocale, supported) =>
          deviceLocale?.languageCode == 'vi'
          ? const Locale('vi')
          : const Locale('en'),
    );
  }
}
