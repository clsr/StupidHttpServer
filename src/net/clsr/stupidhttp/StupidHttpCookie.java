package net.clsr.stupidhttp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * StupidHttpCookie represents a HTTP cookie key=value pair.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpCookie {
	private static final SimpleDateFormat COOKIE_EXPIRES_FORMAT = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");
	static {
		COOKIE_EXPIRES_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	private final String key;
	private final String value;
	private final Date expires;

	/**
	 * Constructs a cookie from a raw cookie string.
	 * 
	 * @param rawCookie The key=value cookie from the Cookie header
	 * @throws StupidHttpException Thrown if the cookie was invalid
	 */
	public StupidHttpCookie(String rawCookie) throws StupidHttpException {
		String[] fields = rawCookie.split("=", 2);
		if (fields.length != 2) {
			throw new StupidHttpException(StupidHttpException.INVALID_COOKIE, rawCookie);
		}
		String key = null;
		String value = null;
		try {
			key = URLDecoder.decode(fields[0].trim(), "UTF-8");
			value = URLDecoder.decode(fields[1].trim(), "UTF-8");
		} catch (UnsupportedEncodingException e) {}
		this.key = key;
		this.value = value;
		this.expires = null;
	}

	/**
	 * Constructs a session cookie from key and value strings.
	 * 
	 * @param key The cookie key
	 * @param value The cookie value
	 */
	public StupidHttpCookie(String key, String value) {
		this.key = key;
		this.value = value;
		this.expires = null;
	}

	/**
	 * Constructs an expiring cookie from key and value strings.
	 * 
	 * @param key The cookie key
	 * @param value The cookie value
	 * @param expires Expiration date; null for the Unix epoch (delete cookie on client)
	 */
	public StupidHttpCookie(String key, String value, Date expires) {
		this.key = key;
		this.value = value;
		if (expires == null) {
			this.expires = new Date(0);
		} else {
			this.expires = expires;
		}
	}

	/**
	 * Constructs an expiring cookie from key and value strings.
	 * This uses the Expires field just like {@link StupidHttpCookie#StupidHttpCookie(String, String, Date)}, not Max-Age.
	 * Local time is used to calculate the date for the Expires field in the cookie.
	 * 
	 * @param key The cookie key
	 * @param value The cookie value
	 * @param expiresAfter Number of seconds from now
	 */
	public StupidHttpCookie(String key, String value, int expiresAfter) {
		this.key = key;
		this.value = value;
		this.expires = new Date(new Date().getTime() + expiresAfter * 1000);
	}

	/**
	 * @return The cookie key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @return The cookie value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @return The expiration date; may be null for session cookies
	 */
	public Date getExpires() {
		return this.expires;
	}

	/**
	 * @return The key encoded for use in the Cookie header
	 */
	public String getEncodedKey() {
		return this.encode(this.key);
	}

	/**
	 * @return The value encoded for use in the Cookie header
	 */
	public String getEncodedValue() {
		return this.encode(this.value);
	}

	/**
	 * @return The expiration date encoded for use in the Cookie header
	 */
	public String getEncodedExpires() {
		return this.encode(COOKIE_EXPIRES_FORMAT.format(this.expires));
	}

	/**
	 * @return The cookie encoded for use in the Cookie HTTP header
	 */
	@Override
	public String toString() {
		if (this.expires != null) {
			return String.format("%s=%s; Expires=%s", this.getEncodedKey(), this.getEncodedValue(), this.getEncodedExpires());
		}
		return String.format("%s=%s", this.getEncodedKey(), this.getEncodedValue());
	}

	private String encode(String s) {
		try {
			return URLEncoder.encode(s.replace('\n', ' ').replace('\r', ' ').replace(';', ' '), "UTF-8");
		} catch (UnsupportedEncodingException e) {}
		return null;
	}
}