import 'package:material_ui/material_ui.dart';
import 'package:google_fonts/google_fonts.dart';

@immutable
class GymColors extends ThemeExtension<GymColors> {
  const GymColors({
    required this.surface2,
    required this.border,
    required this.textPrimary,
    required this.textSecondary,
    required this.textMuted,
    required this.primaryText,
    required this.primarySoft,
    required this.accent,
    required this.accentSoft,
    required this.success,
    required this.warning,
    required this.info,
    required this.rankIron,
    required this.rankBronze,
    required this.rankSilver,
    required this.rankGold,
    required this.rankPlatinum,
    required this.rankDiamond,
    required this.rankMaster,
  });

  final Color surface2;
  final Color border;
  final Color textPrimary;
  final Color textSecondary;
  final Color textMuted;
  final Color primaryText;
  final Color primarySoft;
  final Color accent;
  final Color accentSoft;
  final Color success;
  final Color warning;
  final Color info;
  final Color rankIron;
  final Color rankBronze;
  final Color rankSilver;
  final Color rankGold;
  final Color rankPlatinum;
  final Color rankDiamond;
  final Color rankMaster;

  static const light = GymColors(
    surface2: Color(0xFFF3EDE3),
    border: Color(0xFFE7DFD2),
    textPrimary: Color(0xFF1E1B16),
    textSecondary: Color(0xFF6B6358),
    textMuted: Color(0xFFA69D8F),
    primaryText: Color(0xFFB05A00),
    primarySoft: Color(0x24FFA21F),
    accent: Color(0xFFE2451B),
    accentSoft: Color(0x1AE2451B),
    success: Color(0xFF2E8F4A),
    warning: Color(0xFFB7791F),
    info: Color(0xFF2F6FDB),
    rankIron: Color(0xFF5F6670),
    rankBronze: Color(0xFFA35A26),
    rankSilver: Color(0xFF6F7A88),
    rankGold: Color(0xFFA87A00),
    rankPlatinum: Color(0xFF0E8F80),
    rankDiamond: Color(0xFF3563D6),
    rankMaster: Color(0xFF8A35D0),
  );

  static const dark = GymColors(
    surface2: Color(0xFF221F1B),
    border: Color(0xFF2F2B25),
    textPrimary: Color(0xFFF5F2EC),
    textSecondary: Color(0xFFA39C90),
    textMuted: Color(0xFF625C52),
    primaryText: Color(0xFFFFB547),
    primarySoft: Color(0x1FFFAA2C),
    accent: Color(0xFFFF6A3D),
    accentSoft: Color(0x1FFF6A3D),
    success: Color(0xFF5ED47F),
    warning: Color(0xFFF5C542),
    info: Color(0xFF7AA8FF),
    rankIron: Color(0xFF8A929C),
    rankBronze: Color(0xFFD08A52),
    rankSilver: Color(0xFFC9D1DC),
    rankGold: Color(0xFFF5C542),
    rankPlatinum: Color(0xFF4FE3D0),
    rankDiamond: Color(0xFF7AA8FF),
    rankMaster: Color(0xFFC56BFF),
  );

  @override
  GymColors copyWith({
    Color? surface2,
    Color? border,
    Color? textPrimary,
    Color? textSecondary,
    Color? textMuted,
    Color? primaryText,
    Color? primarySoft,
    Color? accent,
    Color? accentSoft,
    Color? success,
    Color? warning,
    Color? info,
    Color? rankIron,
    Color? rankBronze,
    Color? rankSilver,
    Color? rankGold,
    Color? rankPlatinum,
    Color? rankDiamond,
    Color? rankMaster,
  }) {
    return GymColors(
      surface2: surface2 ?? this.surface2,
      border: border ?? this.border,
      textPrimary: textPrimary ?? this.textPrimary,
      textSecondary: textSecondary ?? this.textSecondary,
      textMuted: textMuted ?? this.textMuted,
      primaryText: primaryText ?? this.primaryText,
      primarySoft: primarySoft ?? this.primarySoft,
      accent: accent ?? this.accent,
      accentSoft: accentSoft ?? this.accentSoft,
      success: success ?? this.success,
      warning: warning ?? this.warning,
      info: info ?? this.info,
      rankIron: rankIron ?? this.rankIron,
      rankBronze: rankBronze ?? this.rankBronze,
      rankSilver: rankSilver ?? this.rankSilver,
      rankGold: rankGold ?? this.rankGold,
      rankPlatinum: rankPlatinum ?? this.rankPlatinum,
      rankDiamond: rankDiamond ?? this.rankDiamond,
      rankMaster: rankMaster ?? this.rankMaster,
    );
  }

  @override
  GymColors lerp(ThemeExtension<GymColors>? other, double t) {
    if (other is! GymColors) return this;
    return GymColors(
      surface2: Color.lerp(surface2, other.surface2, t)!,
      border: Color.lerp(border, other.border, t)!,
      textPrimary: Color.lerp(textPrimary, other.textPrimary, t)!,
      textSecondary: Color.lerp(textSecondary, other.textSecondary, t)!,
      textMuted: Color.lerp(textMuted, other.textMuted, t)!,
      primaryText: Color.lerp(primaryText, other.primaryText, t)!,
      primarySoft: Color.lerp(primarySoft, other.primarySoft, t)!,
      accent: Color.lerp(accent, other.accent, t)!,
      accentSoft: Color.lerp(accentSoft, other.accentSoft, t)!,
      success: Color.lerp(success, other.success, t)!,
      warning: Color.lerp(warning, other.warning, t)!,
      info: Color.lerp(info, other.info, t)!,
      rankIron: Color.lerp(rankIron, other.rankIron, t)!,
      rankBronze: Color.lerp(rankBronze, other.rankBronze, t)!,
      rankSilver: Color.lerp(rankSilver, other.rankSilver, t)!,
      rankGold: Color.lerp(rankGold, other.rankGold, t)!,
      rankPlatinum: Color.lerp(rankPlatinum, other.rankPlatinum, t)!,
      rankDiamond: Color.lerp(rankDiamond, other.rankDiamond, t)!,
      rankMaster: Color.lerp(rankMaster, other.rankMaster, t)!,
    );
  }
}

class Gap {
  static const double xs = 4;
  static const double sm = 8;
  static const double md = 12;
  static const double lg = 16;
  static const double xl = 24;
  static const double xxl = 32;
}

class Radii {
  static const double sm = 8;
  static const double md = 12;
  static const double lg = 20;
  static const double pill = 999;
}

TextTheme _textTheme(TextTheme base, Color color) {
  final bodyFont = GoogleFonts.beVietnamPro().fontFamily;
  final headFont = GoogleFonts.chakraPetch().fontFamily;
  final body = base.apply(
    fontFamily: bodyFont,
    bodyColor: color,
    displayColor: color,
  );
  TextStyle? head(TextStyle? s) =>
      s?.copyWith(fontFamily: headFont, fontWeight: FontWeight.w700);
  return body.copyWith(
    displayLarge: head(body.displayLarge),
    displayMedium: head(body.displayMedium),
    displaySmall: head(body.displaySmall),
    headlineLarge: head(body.headlineLarge),
    headlineMedium: head(body.headlineMedium),
    headlineSmall: head(body.headlineSmall),
  );
}

ThemeData _build({
  required ColorScheme scheme,
  required Color background,
  required GymColors gym,
}) {
  final base = ThemeData(brightness: scheme.brightness);
  return ThemeData(
    useMaterial3: true,
    brightness: scheme.brightness,
    colorScheme: scheme,
    scaffoldBackgroundColor: background,
    textTheme: _textTheme(base.textTheme, gym.textPrimary),
    appBarTheme: AppBarTheme(
      backgroundColor: background,
      foregroundColor: gym.textPrimary,
      elevation: 0,
      scrolledUnderElevation: 0,
    ),
    dividerColor: gym.border,
    extensions: [gym],
  );
}

final ThemeData lightTheme = _build(
  scheme: ColorScheme.fromSeed(seedColor: const Color(0xFFFFA21F)).copyWith(
    brightness: Brightness.light,
    primary: const Color(0xFFFFA21F),
    onPrimary: const Color(0xFF1E1406),
    surface: const Color(0xFFFFFFFF),
    onSurface: GymColors.light.textPrimary,
    error: const Color(0xFFD1344A),
  ),
  background: const Color(0xFFFAF6EF),
  gym: GymColors.light,
);

final ThemeData darkTheme = _build(
  scheme:
      ColorScheme.fromSeed(
        seedColor: const Color(0xFFFFAA2C),
        brightness: Brightness.dark,
      ).copyWith(
        primary: const Color(0xFFFFAA2C),
        onPrimary: const Color(0xFF1E1406),
        surface: const Color(0xFF181613),
        onSurface: GymColors.dark.textPrimary,
        error: const Color(0xFFFF5468),
      ),
  background: const Color(0xFF0E0D0B),
  gym: GymColors.dark,
);
