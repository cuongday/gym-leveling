import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/api/api_client.dart';

final pingProvider = FutureProvider<PingResult>(
  (ref) => ref.watch(apiClientProvider).ping(),
  // Riverpod 3 retries failed providers by default; surface errors immediately
  // so the UI shows the error state and the user retries manually.
  retry: (_, _) => null,
);
