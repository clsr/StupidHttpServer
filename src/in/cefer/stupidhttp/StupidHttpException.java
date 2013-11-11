package in.cefer.stupidhttp;

/**
 * StupidHttpException represents a HTTP-related exception.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpException extends Exception {
	private static final long serialVersionUID = -2762970369794627080L;
	/** An arbitrary exception; this shouldn't happen */
	public static final int UNKNOWN = 0;
	/** Invalid contents of the syntactically correct request */
	public static final int INVALID_REQUEST = 1;
	/** Unsupported request method */
	public static final int INVALID_METHOD = 2;
	/** Invalid header syntax */
	public static final int INVALID_HEADER = 3;
	/** Invalid cookie syntax */
	public static final int INVALID_COOKIE = 4;
	/** Unexpected end of request (before end of headers) */
	public static final int UNEXPECTED_END = 5;
	/** Missing request body */
	public static final int MISSING_BODY = 6;
	/** Request body is too long */
	public static final int BODY_TOO_LONG = 7;

	private final int type;
	private final String msg;

	/**
	 * Construct a new exception/
	 * 
	 * @param type One of the constants specified in this class
	 * @param msg Exception message
	 */
	public StupidHttpException(int type, String msg) {
		this.type = type;
		this.msg = msg;
	}

	/**
	 * @return The exception type; matches one of the constants specified in this class
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * @return The exception reason; stringified version of the constants' identifiers
	 */
	public String getReason() {
		switch (this.type) {
		case UNKNOWN:
			return "UNKNOWN";
		case INVALID_REQUEST:
			return "INVALID_REQUEST";
		case INVALID_METHOD:
			return "INVALID_METHOD";
		case INVALID_HEADER:
			return "INVALID_HEADER";
		case INVALID_COOKIE:
			return "INVALID_COOKIE";
		case UNEXPECTED_END:
			return "UNEXPECTED_END";
		case MISSING_BODY:
			return "MISSING_BODY";
		case BODY_TOO_LONG:
			return "BODY_TOO_LONG";
		default:
			return "INVALID_ERROR_REASON";
		}
	}

	@Override
	public String getMessage() {
		return this.getReason() + ": " + this.msg;
	}
}