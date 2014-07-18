package net.clsr.stupidhttp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * StupidHttpResponse represents a HTTP response.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpResponse {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	static {
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	private List<StupidHttpHeader> headers = new ArrayList<StupidHttpHeader>();
	private int code = StupidHttpStatus.OK;
	private byte[] body = null;

	/**
	 * Create a HTTP response.
	 * 
	 * The Content-Type, Date and Server headers are automatically populated.
	 */
	public StupidHttpResponse() {
		this.headers.add(new StupidHttpHeader("content-type", "text/plain; charset=utf-8"));
		this.headers.add(new StupidHttpHeader("date", DATE_FORMAT.format(new Date())));
		this.headers.add(new StupidHttpHeader("server", this.getClass().getPackage().getName()));
	};

	/**
	 * Add a header to the response.
	 * The header will be appended even if headers with the same name already exist.
	 * 
	 * @param header The header
	 */
	public void addHeader(StupidHttpHeader header) {
		this.headers.add(header);
	}

	/**
	 * Add a header to the response.
	 * If a header with the same name already exists, it will be overwritten with this one.
	 * 
	 * @param header The header
	 */
	public void setHeader(StupidHttpHeader header) {
		for (int i = 0; i < this.headers.size(); i++) {
			if (this.headers.get(i).getNormalizedKey().equals(header.getNormalizedKey())) {
				this.headers.set(i, header);
				return;
			}
		}
		this.addHeader(header);
	}

	/**
	 * Adds a Set-Cookie header to the response.
	 * 
	 * @param cookie The cookie
	 */
	public void addCookie(StupidHttpCookie cookie) {
		this.addHeader(new StupidHttpHeader("set-cookie", cookie.toString()));
	}

	/**
	 * Sets the Location header and the {@link StupidHttpStatus#Found} response code to send a redirect response.
	 * 
	 * @param path The path to redirect to
	 */
	public void redirect(String path) {
		this.redirect(path, StupidHttpStatus.Found);
	}

	/**
	 * Sets the Location header and the response code to send a redirect response.
	 * 
	 * @param path The path to redirect to
	 * @param code The response code
	 */
	public void redirect(String path, int code) {
		this.setHeader(new StupidHttpHeader("location", path));
		this.setCode(code);
	}

	/**
	 * Get all headers with the specified name.
	 * 
	 * @param key The header name
	 * @return Array of headers with that name
	 */
	public StupidHttpHeader[] getHeaders(String key) {
		key = new StupidHttpHeader(key, null).getNormalizedKey();
		List<StupidHttpHeader> headers = new ArrayList<StupidHttpHeader>();
		for (StupidHttpHeader header : this.headers) {
			if (header.getNormalizedKey().equals(key)) {
				headers.add(header);
			}
		}
		return headers.toArray(new StupidHttpHeader[headers.size()]);
	}

	/**
	 * Get all headers in the response.
	 * 
	 * @return Array of all headers
	 */
	public StupidHttpHeader[] getAllHeaders() {
		return this.headers.toArray(new StupidHttpHeader[this.headers.size()]);
	}

	/**
	 * Remove the first header with the specified name.
	 * 
	 * @param key The name of the header to remove
	 */
	public void removeHeader(String key) {
		key = new StupidHttpHeader(key, null).getNormalizedKey();
		for (int i = 0; i < this.headers.size(); i++) {
			if (this.headers.get(i).getNormalizedKey().equals(key)) {
				this.headers.remove(i);
				return;
			}
		}
	}

	/**
	 * Remove all headers with the specified name.
	 * 
	 * @param key The name of the headers to remove
	 */
	public void removeHeaders(String key) {
		key = new StupidHttpHeader(key, null).getNormalizedKey();
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < this.headers.size(); i++) {
			if (this.headers.get(i).getNormalizedKey().equals(key)) {
				indices.add(i);
			}
		}
		for (int i : indices) {
			this.headers.remove(i);
		}
	}

	/**
	 * Remove all headers.
	 */
	public void removeAllHeaders() {
		this.headers.clear();
	}

	/**
	 * @return The response code; should be a constant in {@link StupidHttpStatus}
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Set the response code.
	 * Should be a constant from {@link StupidHttpStatus}.
	 * 
	 * @param code The response code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Construct a HTTP status line.
	 * Looks like "HTTP/1.0 200 OK", if the code is {@link StupidHttpStatus#OK}.
	 * "\r\n" is not included.
	 * 
	 * @return The HTTP status line
	 */
	public String getStatus() {
		return String.format("HTTP/1.0 %d %s", this.code, StupidHttpStatus.statusText(this.code));
	}

	/**
	 * @return A copy of the request body data
	 */
	public byte[] getBody() {
		return this.body.clone();
	}

	/**
	 * Sets the body data to a copy of the parameter.
	 * Also sets the Content-Length header.
	 * 
	 * @param body The body data
	 */
	public void setBody(byte[] body) {
		this.body = body.clone();
		this.setHeader(new StupidHttpHeader("content-length", Integer.toString(this.body.length)));
	}

	/**
	 * Sets the body data to UTF-8 encoded parameter.
	 * Also sets the Content-Length header.
	 * 
	 * @param body The body data
	 */
	public void setBody(String body) {
		try {
			this.body = body.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {}
		this.setHeader(new StupidHttpHeader("content-length", Integer.toString(this.body.length)));
	}

	/**
	 * Compose a HTTP request into a string.
	 * 
	 * @return The raw HTTP request
	 */
	@Override
	public String toString() {
		StringWriter buf = new StringWriter();
		buf.write(this.getStatus());
		buf.write("\r\n");
		for (StupidHttpHeader h : this.headers) {
			buf.write(h.toString());
			buf.write("\r\n");
		}
		if (this.body != null) {
			try {
				buf.write("\r\n");
				buf.write(new String(this.body, "UTF-8"));
			} catch (UnsupportedEncodingException e) {}
		}
		return buf.toString();
	}

	/**
	 * Writes the HTTP request to a stream.
	 * 
	 * @param out The stream to write the request to
	 * @throws IOException IO error during writing
	 */
	public void writeTo(OutputStream out) throws IOException {
		this.writeTo(out, true);
	}

	/**
	 * Writes the HTTP request to a stream.
	 * 
	 * @param out The stream to write the request to
	 * @param writeBody Whether to write the body data too; should be false for HEAD requests
	 * @throws IOException IO error during writing
	 */
	public void writeTo(OutputStream out, boolean writeBody) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(out);
		byte[] rn = "\r\n".getBytes("UTF-8");
		try {
			bos.write(this.getStatus().getBytes("UTF-8"));
			bos.write(rn);
			for (StupidHttpHeader h : this.headers) {
				bos.write(h.toString().getBytes("UTF-8"));
				bos.write(rn);
			}
			bos.write(rn);
			if (this.body != null && writeBody) {
				bos.write(this.body);
			}
			bos.flush();
		} catch (UnsupportedEncodingException e) {}
	}

	/**
	 * Construct a simple HTTP response.
	 * 
	 * @param code Response code
	 * @param contentType Content-Type header; if null, "text/plain; charset=utf-8" will be used
	 * @param body The response body data
	 * @return The response
	 */
	public static StupidHttpResponse simpleResponse(int code, String contentType, String body) {
		StupidHttpResponse resp = new StupidHttpResponse();
		resp.setHeader(new StupidHttpHeader("content-type", contentType == null ? "text/plain; charset=utf-8" : contentType));
		resp.setCode(code <= 0 ? StupidHttpStatus.OK : code);
		resp.setBody(body);
		return resp;
	}

	/**
	 * Construct a simple plaintext response.
	 * The "text/plain; charset=utf-8" Content-Type will be used.
	 * 
	 * @param body The body text
	 * @return The response
	 */
	public static StupidHttpResponse textResponse(String body) {
		return StupidHttpResponse.simpleResponse(0, null, body);
	}

	/**
	 * Construct a simple HTML response.
	 * The "text/html; charset=utf-8" Content-Type will be used.
	 * 
	 * @param body The body HTML
	 * @return The response
	 */
	public static StupidHttpResponse htmlResponse(String body) {
		return StupidHttpResponse.simpleResponse(0, "text/html; charset=utf-8", body);
	}

	/**
	 * Construct a simple {@link StupidHttpStatus#Found} redirect response.
	 * No body data will be sent.
	 * 
	 * @param path The path to redirect to
	 * @return The response
	 */
	public static StupidHttpResponse redirectResponse(String path) {
		StupidHttpResponse resp = new StupidHttpResponse();
		resp.redirect(path);
		return resp;
	}

	/**
	 * Construct a simple {@link StupidHttpStatus#InternalServerError} response.
	 * It produces a text response in the "500 Internal Server Error: ExceptionMessage" format (ExceptionMessage being the exception's .getMessage()).
	 * 
	 * @param e The exception to show
	 * @return The response
	 */
	public static StupidHttpResponse errorResponse(Exception e) {
		return StupidHttpResponse.simpleResponse(StupidHttpStatus.InternalServerError, null, String.format("%d %s: %s", StupidHttpStatus.InternalServerError, StupidHttpStatus.statusText(StupidHttpStatus.InternalServerError), e.getMessage()));
	}

	/**
	 * Construct a simple {@link StupidHttpStatus#NotFound} response.
	 * It produces a text response in the "404 Not Found: /path" format (/path being the argument).
	 * 
	 * @param path The path that wasn't found
	 * @return The response
	 */
	public static StupidHttpResponse notFoundResponse(String path) {
		return StupidHttpResponse.simpleResponse(StupidHttpStatus.NotFound, null, String.format("%d %s%s", StupidHttpStatus.NotFound, StupidHttpStatus.statusText(StupidHttpStatus.NotFound), path == null ? "" : ": " + path));
	}

	/**
	 * Construct a simple response that serves a file.
	 * It tries to guess the filetype from the filename.
	 * 
	 * @param filename The file to serve
	 * @return The response
	 */
	public static StupidHttpResponse fileResponse(String filename) {
		try {
			return StupidHttpResponse.fileResponse(new File(filename).toURI().toURL());
		} catch (MalformedURLException e) {
			return StupidHttpResponse.errorResponse(e);
		}
	}

	/**
	 * Construct a simple response that serves a file.
	 * It tries to guess the filetype from the filename.
	 * 
	 * @param file The file to serve
	 * @return The response
	 */
	public static StupidHttpResponse fileResponse(File file) {
		try {
			return StupidHttpResponse.fileResponse(file.toURI().toURL());
		} catch (MalformedURLException e) {
			return StupidHttpResponse.errorResponse(e);
		}
	}

	/**
	 * Construct a simple response that serves a file.
	 * It tries to guess the filetype from the filename.
	 * 
	 * @param url The file to serve
	 * @return The response
	 */
	public static StupidHttpResponse fileResponse(URL url) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in;
		try {
			in = url.openStream();
		} catch (IOException e) {
			return StupidHttpResponse.notFoundResponse(url.getPath());
		}

		byte[] buf = new byte[1024 * 8];
		int n = 0;
		try {
			while ((n = in.read(buf)) != -1) {
				out.write(buf, 0, n);
			}
		} catch (IOException e) {
			return StupidHttpResponse.errorResponse(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				return StupidHttpResponse.errorResponse(e);
			}
		}

		StupidHttpResponse resp = new StupidHttpResponse();
		resp.setBody(out.toByteArray());
		resp.setHeader(new StupidHttpHeader("content-type", URLConnection.guessContentTypeFromName(url.getPath())));
		return resp;
	}
}
