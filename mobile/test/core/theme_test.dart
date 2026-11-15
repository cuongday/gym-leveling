import 'dart:ui';

import 'package:flutter_test/flutter_test.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:gym_leveling/core/theme.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  GoogleFonts.config.allowRuntimeFetching = false;

  test('light primary matches design token', () {
    expect(lightTheme.colorScheme.primary, const Color(0xFFFFA21F));
  });

  test('dark scaffold background matches design token', () {
    expect(darkTheme.scaffoldBackgroundColor, const Color(0xFF0E0D0B));
  });

  test('GymColors extension exposes rankMaster', () {
    expect(
      lightTheme.extension<GymColors>()!.rankMaster,
      const Color(0xFF8A35D0),
    );
  });
}
