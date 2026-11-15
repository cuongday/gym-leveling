// ignore: unused_import
import 'package:intl/intl.dart' as intl;

import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Vietnamese (`vi`).
class AppLocalizationsVi extends AppLocalizations {
  AppLocalizationsVi([String locale = 'vi']) : super(locale);

  @override
  String get appTitle => 'Gym Leveling';

  @override
  String get homeGreeting => 'Chào mừng trở lại';

  @override
  String get errorGeneric => 'Không kết nối được máy chủ';

  @override
  String get retry => 'Thử lại';
}
