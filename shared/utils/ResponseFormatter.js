// Common response formatter
class ResponseFormatter {
  static success(data, message = 'Success') {
    return {
      success: true,
      message,
      data
    };
  }

  static error(message, statusCode = 500, details = null) {
    return {
      success: false,
      message,
      statusCode,
      details
    };
  }

  static paginated(data, page, limit, total) {
    return {
      success: true,
      data,
      pagination: {
        page,
        limit,
        total,
        pages: Math.ceil(total / limit)
      }
    };
  }
}

module.exports = ResponseFormatter;
