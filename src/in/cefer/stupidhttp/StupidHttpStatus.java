package in.cefer.stupidhttp;

import java.util.HashMap;
import java.util.Map;

/**
 * StupidHttpStatus contains HTTP response code constants and methods related to them.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpStatus {
	/** 100 Continue */
	public static final int Continue = 100;
	/** 101 Switching Protocols */
	public static final int SwitchingProtocols = 101;

	/** 200 OK */
	public static final int OK = 200;
	/** 201 Created */
	public static final int Created = 201;
	/** 202 Accepted */
	public static final int Accepted = 202;
	/** 203 Non-Authoritative Info */
	public static final int NonAuthoritativeInfo = 203;
	/** 204 No Content */
	public static final int NoContent = 204;
	/** 205 Reset Content */
	public static final int ResetContent = 205;
	/** 206 Partial Content */
	public static final int PartialContent = 206;

	/** 300 Multiple Choices */
	public static final int MultipleChoices = 300;
	/** 301 Moved Permanently */
	public static final int MovedPermanently = 301;
	/** 302 Found */
	public static final int Found = 302;
	/** 303 See Other */
	public static final int SeeOther = 303;
	/** 304 Not Modified */
	public static final int NotModified = 304;
	/** 305 Use Proxy */
	public static final int UseProxy = 305;
	/** 307 Temporary Redirect */
	public static final int TemporaryRedirect = 307;

	/** 400 Bad Request */
	public static final int BadRequest = 400;
	/** 401 Unauthorized */
	public static final int Unauthorized = 401;
	/** 402 Payment Required */
	public static final int PaymentRequired = 402;
	/** 403 Forbidden */
	public static final int Forbidden = 403;
	/** 404 Not Found */
	public static final int NotFound = 404;
	/** 405 Method Not Allowed */
	public static final int MethodNotAllowed = 405;
	/** 406 Not Acceptable */
	public static final int NotAcceptable = 406;
	/** 407 Proxy Authentication Required */
	public static final int ProxyAuthRequired = 407;
	/** 408 Request Timeout */
	public static final int RequestTimeout = 408;
	/** 409 Conflict */
	public static final int Conflict = 409;
	/** 410 Gone */
	public static final int Gone = 410;
	/** 411 Length Required */
	public static final int LengthRequired = 411;
	/** 412 Precondition Failed */
	public static final int PreconditionFailed = 412;
	/** 413 Request Entity Too Large */
	public static final int RequestEntityTooLarge = 413;
	/** 414 Request URI Too Long */
	public static final int RequestUriTooLong = 414;
	/** 415 Unsupported Media Type */
	public static final int UnsupportedMediaType = 415;
	/** 416 Request Range Not Satisfiable */
	public static final int RequestedRangeNotSatisfiable = 416;
	/** 417 Expectation Failed */
	public static final int ExpectationFailed = 417;
	/** 418 I'm a teapot */
	public static final int Teapot = 418;

	/** 500 Internal Server Error */
	public static final int InternalServerError = 500;
	/** 501 Not Implemented */
	public static final int NotImplemented = 501;
	/** 502 Bad Gateway */
	public static final int BadGateway = 502;
	/** 503 Service Unavailable */
	public static final int ServiceUnavailable = 503;
	/** 504 Gateway Timeout */
	public static final int GatewayTimeout = 504;
	/** 505 HTTP Version Not Supported */
	public static final int HttpVersionNotSupported = 505;

	private final static Map<Integer, String> statusTexts = new HashMap<Integer, String>();
	static {
		statusTexts.put(Continue, "Continue");
		statusTexts.put(SwitchingProtocols, "Switching Protocols");

		statusTexts.put(OK, "OK");
		statusTexts.put(Created, "Created");
		statusTexts.put(Accepted, "Accepted");
		statusTexts.put(NonAuthoritativeInfo, "Non-Authoritative Information");
		statusTexts.put(NoContent, "No Content");
		statusTexts.put(ResetContent, "Reset Content");
		statusTexts.put(PartialContent, "Partial Content");

		statusTexts.put(MultipleChoices, "Multiple Choices");
		statusTexts.put(MovedPermanently, "Moved Permanently");
		statusTexts.put(Found, "Found");
		statusTexts.put(SeeOther, "See Other");
		statusTexts.put(NotModified, "Not Modified");
		statusTexts.put(UseProxy, "Use Proxy");
		statusTexts.put(TemporaryRedirect, "Temporary Redirect");

		statusTexts.put(BadRequest, "Bad Request");
		statusTexts.put(Unauthorized, "Unauthorized");
		statusTexts.put(PaymentRequired, "Payment Required");
		statusTexts.put(Forbidden, "Forbidden");
		statusTexts.put(NotFound, "Not Found");
		statusTexts.put(MethodNotAllowed, "Method Not Allowed");
		statusTexts.put(NotAcceptable, "Not Acceptable");
		statusTexts.put(ProxyAuthRequired, "Proxy Authentication Required");
		statusTexts.put(RequestTimeout, "Request Timeout");
		statusTexts.put(Conflict, "Conflict");
		statusTexts.put(Gone, "Gone");
		statusTexts.put(LengthRequired, "Length Required");
		statusTexts.put(PreconditionFailed, "Precondition Failed");
		statusTexts.put(RequestEntityTooLarge, "Request Entity Too Large");
		statusTexts.put(RequestUriTooLong, "Request URI Too Long");
		statusTexts.put(UnsupportedMediaType, "Unsupported Media Type");
		statusTexts.put(RequestedRangeNotSatisfiable, "Requested Range Not Satisfiable");
		statusTexts.put(ExpectationFailed, "Expectation Failed");
		statusTexts.put(Teapot, "I'm a teapot");

		statusTexts.put(InternalServerError, "Internal Server Error");
		statusTexts.put(NotImplemented, "Not Implemented");
		statusTexts.put(BadGateway, "Bad Gateway");
		statusTexts.put(ServiceUnavailable, "Service Unavailable");
		statusTexts.put(GatewayTimeout, "Gateway Timeout");
		statusTexts.put(HttpVersionNotSupported, "HTTP Version Not Supported");
	}

	/**
	 * Get the description text of a status code.
	 * 
	 * @param code The status code
	 * @return The description
	 */
	public static String statusText(int code) {
		return statusTexts.get(code);
	}

	/**
	 * Construct a simple handler that replies with a HTTP status.
	 * It uses text responses in the "123 Description" format (if the code was 123 and {@link StupidHttpStatus#statusText(int)} returned "Description" for it).
	 * The response status code is set to the status code provided in the argument.
	 * 
	 * @param code The status code
	 * @return The handler
	 */
	public static StupidHttpHandler statusHandler(final int code) {
		return new StupidHttpHandler() {
			@Override
			public StupidHttpResponse handle(StupidHttpRequest req) {
				StupidHttpResponse resp = new StupidHttpResponse();
				resp.setCode(code);
				resp.setBody(String.format("%d %s", code, StupidHttpStatus.statusText(code)));
				return resp;
			}
		};
	}

	/**
	 * A simple handler that replies "404 Not Found: /file/path" for the request path ("/file/path" in this example).
	 */
	public static final StupidHttpHandler handle404 = new StupidHttpHandler() {
		@Override
		public StupidHttpResponse handle(StupidHttpRequest req) {
			StupidHttpResponse resp = new StupidHttpResponse();
			resp.setCode(StupidHttpStatus.NotFound);
			resp.setBody(String.format("%d %s: %s", StupidHttpStatus.NotFound, StupidHttpStatus.statusText(StupidHttpStatus.NotFound), req.getPath()));
			return resp;
		}
	};
}