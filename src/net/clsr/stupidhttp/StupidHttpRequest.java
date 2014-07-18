package net.clsr.stupidhttp;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * StupidHttpRequest represents a HTTP request.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpRequest {
	private static final int MAX_BODY_LENGTH = 1024 * 1024 * 16; // 16 MB

	private String localAddress;
	private String remoteAddress;
	private String method = "GET";
	private String path = null;
	private List<StupidHttpHeader> headers = new ArrayList<StupidHttpHeader>();
	private List<StupidHttpCookie> cookies = new ArrayList<StupidHttpCookie>();
	private byte[] body = null;

	/**
	 * Read a request from a socket.
	 * 
	 * @param s The socket to read from
	 * @throws IOException An exception with IO
	 * @throws StupidHttpException Invalid request
	 */
	public StupidHttpRequest(Socket s) throws IOException, StupidHttpException {
		this.remoteAddress = s.getRemoteSocketAddress().toString();
		this.localAddress = s.getLocalSocketAddress().toString();

		this.read(s.getInputStream());
	}

	/**
	 * Read a request from an input stream.
	 * 
	 * @param in The input stream to read from
	 * @param localAddress Local address that received the request
	 * @param remoteAddress Remote address that sent the request
	 * @throws IOException An exception with IO
	 * @throws StupidHttpException Invalid request
	 */
	public StupidHttpRequest(InputStream in, String localAddress, String remoteAddress) throws IOException, StupidHttpException {
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;

		this.read(in);
	}

	/**
	 * Construct a request from parameters.
	 * 
	 * @param localAddress Local address that received the request
	 * @param remoteAddress Remote address that sent the request
	 * @param method Request method
	 * @param path Request path
	 * @param headers Request headers
	 * @param body Request body data
	 */
	public StupidHttpRequest(String localAddress, String remoteAddress, String method, String path, StupidHttpHeader[] headers, byte[] body) {
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
		this.method = method.toUpperCase();
		this.path = path;
		for (StupidHttpHeader header : headers) {
			this.headers.add(header);
		}
		this.body = body;
	}

	/**
	 * @return The local address that received the request
	 */
	public String getLocalAddress() {
		return this.localAddress;
	}

	/**
	 * @return The remote address that sent the request
	 */
	public String getRemoteAddress() {
		return this.remoteAddress;
	}

	/**
	 * @return Whether the request's method is GET
	 */
	public boolean isGet() {
		return this.method.equals("GET");
	}

	/**
	 * @return Whether the request's method is POST
	 */
	public boolean isPost() {
		return this.method.equals("POST");
	}

	/**
	 * @return Whether the request's method is HEAD
	 */
	public boolean isHead() {
		return this.method.equals("HEAD");
	}

	/**
	 * @return The request method
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * @return The raw request path, including the query string
	 */
	public String getRawPath() {
		return this.path;
	}

	/**
	 * @return The request path, without the query string
	 */
	public String getPath() {
		int i = this.path.indexOf('?');
		if (i >= 0) {
			return this.path.substring(0, i);
		}
		return this.path;
	}

	/**
	 * @return The query string if any, otherwise null
	 */
	public String getQueryString() {
		int i = this.path.indexOf('?');
		if (i >= 0) {
			return this.path.substring(i + 1);
		}
		return null;
	}

	/**
	 * Try to reconstruct the URL to which this request might have been sent.
	 * 
	 * @return The URL constructed from the request path and the Host header
	 */
	public String getUrl() {
		String host = this.getHeader("host");
		return String.format("%s://%s%s", "http", host == null ? this.getLocalAddress() : host, this.getRawPath());
	}

	/**
	 * Get a header's value.
	 * If there are multiple headers with the same key, the first one's value is returned.
	 * 
	 * @param key The header
	 * @return The value, if the header was specified; null otherwise
	 */
	public String getHeader(String key) {
		key = new StupidHttpHeader(key, null).getNormalizedKey();
		for (StupidHttpHeader h : this.headers) {
			if (h.getNormalizedKey().equals(key)) {
				return h.getValue();
			}
		}
		return null;
	}

	/**
	 * @return Array of all headers on this request
	 */
	public StupidHttpHeader[] getHeaders() {
		return this.headers.toArray(new StupidHttpHeader[this.headers.size()]);
	}

	/**
	 * Get a cookie's value.
	 * 
	 * @param key The cookie key
	 * @return The cookie value
	 */
	public String getCookie(String key) {
		for (StupidHttpCookie c : this.cookies) {
			if (c.getKey().equals(key)) {
				return c.getValue();
			}
		}
		return null;
	}

	/**
	 * @return Array of all cookies in this request
	 */
	public StupidHttpCookie[] getCookies() {
		return this.cookies.toArray(new StupidHttpCookie[this.cookies.size()]);
	}

	/**
	 * @return A copy of the request body data; null, if not a POST request
	 */
	public byte[] getBody() {
		return this.body == null ? null : this.body.clone();
	}

	/**
	 * @return UTF-8 decoded request body data
	 */
	public String getBodyString() {
		if (this.body != null) {
			try {
				return new String(this.body, "UTF-8");
			} catch (UnsupportedEncodingException e) {}
		}
		return null;
	}

	/**
	 * @return The form parsed from the URL query string
	 */
	public StupidHttpForm getQueryForm() {
		String q = this.getQueryString();
		return new StupidHttpForm(q == null ? "" : q);
	}

	/**
	 * @return The form parsed from the request body data
	 */
	public StupidHttpForm getPostForm() {
		String p = this.getBodyString();
		return new StupidHttpForm(p == null ? "" : p);
	}

	private void read(InputStream in) throws IOException, StupidHttpException {
		BufferedInputStream bin = new BufferedInputStream(in);
		String method = this.readLine(bin);
		if (method == null) {
			throw new StupidHttpException(StupidHttpException.UNEXPECTED_END, "no method header");
		}
		this.parseMethod(method);
		String header;
		while ((header = this.readLine(bin)) != null && !"".equals(header)) {
			this.parseHeader(header);
		}
		if (header == null) {
			throw new StupidHttpException(StupidHttpException.UNEXPECTED_END, "unexpected end of headers");
		}
		if (this.isPost()) {
			String len = this.getHeader("content-length");
			if (len == null) {
				throw new StupidHttpException(StupidHttpException.INVALID_REQUEST, "missing content-length");
			}
			int length;
			try {
				length = Integer.parseInt(len);
			} catch (NumberFormatException e) {
				throw new StupidHttpException(StupidHttpException.INVALID_REQUEST, "content-length is not a number");
			}
			this.readBody(bin, length);
		}
	}

	private String readLine(InputStream in) throws IOException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int c;
		while ((c = in.read()) >= 0) {
			if (c == '\n') {
				break;
			}
			if (c == '\r') {
				in.mark(1);
				int n = in.read();
				if (n == '\n' || n < 0) {
					break;
				}
				in.reset();
			}
			buf.write(c);
		}
		String s = buf.toString("UTF-8");
		if (c < 0 && s.length() == 0) {
			return null;
		}
		return s;
	}

	private void parseMethod(String method) throws StupidHttpException {
		String[] fields = method.split("\\s+");
		if (fields.length != 3) {
			throw new StupidHttpException(StupidHttpException.INVALID_METHOD, method);
		}

		if ("POST".equalsIgnoreCase(fields[0])) {
			this.method = "POST";
		} else if ("GET".equalsIgnoreCase(fields[0])) {
			this.method = "GET";
		} else if ("HEAD".equalsIgnoreCase(fields[0])) {
			this.method = "HEAD";
		} else {
			throw new StupidHttpException(StupidHttpException.INVALID_METHOD, fields[0]);
		}

		this.path = fields[1];

		if (!fields[2].toUpperCase().matches("^HTTP/[0-9.]+$")) {
			throw new StupidHttpException(StupidHttpException.INVALID_METHOD, fields[2]);
		}
	}

	private void parseHeader(String header) throws StupidHttpException {
		String[] fields = header.split(": +", 2);
		if (fields.length != 2) {
			throw new StupidHttpException(StupidHttpException.INVALID_HEADER, header);
		}
		String key = fields[0].trim();
		String value = fields[1].trim();
		if (key.isEmpty() || value.isEmpty()) {
			throw new StupidHttpException(StupidHttpException.INVALID_HEADER, header);
		}
		StupidHttpHeader h = new StupidHttpHeader(key, value);
		this.headers.add(h);
		if (h.getNormalizedKey().equalsIgnoreCase("cookie")) {
			try {
				String[] cookies = h.getValue().split(";\\s*");
				for (String cookie : cookies) {
					this.cookies.add(new StupidHttpCookie(cookie));
				}
			} catch (StupidHttpException e) {
				// invalid cookie, ignore
			}
		}
	}

	private void readBody(InputStream in, int length) throws StupidHttpException, IOException {
		if (length > MAX_BODY_LENGTH) {
			throw new StupidHttpException(StupidHttpException.BODY_TOO_LONG, "body too long: " + length + " bytes");
		}

		ByteArrayOutputStream body = new ByteArrayOutputStream();
		byte[] buf = new byte[1024 * 4];
		int n = 0;
		for (;;) {
			int num = Math.min(buf.length, length);
			n = in.read(buf, 0, num);
			if (n < 0) {
				break;
			}
			body.write(buf, 0, n);
			length -= n;
			if (length <= 0) {
				break;
			}
		}

		this.body = body.toByteArray();
	}
}
