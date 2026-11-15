class ApiException implements Exception {
  const ApiException({this.statusCode, this.detail});

  final int? statusCode;
  final String? detail;

  @override
  String toString() => 'ApiException($statusCode, $detail)';
}
