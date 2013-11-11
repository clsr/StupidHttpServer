package in.cefer.stupidhttp;

/**
 * StupidHttpHeader represents a HTTP header entry.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpHeader {
	private static final String[] OverrideNormalizedKeys = new String[] { "Content-MD5", "DNT", "ETag", "TE", "WWW-Authenticate", "X-XSS-Protection" };
	private final String key;
	private final String value;

	/**
	 * Construct a header entry from key and value strings.
	 * 
	 * @param key The header key
	 * @param value The value
	 */
	public StupidHttpHeader(String key, String value) {
		this.key = key == null ? null : key.trim();
		this.value = value == null ? null : value.trim();
	}

	/**
	 * @return The header key
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @return Normalized header key
	 */
	public String getNormalizedKey() {
		for (String k : OverrideNormalizedKeys) {
			if (this.key.equalsIgnoreCase(k)) {
				return k;
			}
		}

		String[] fields = this.key.split("-");
		StringBuilder key = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (i != 0) {
				key.append('-');
			}
			if (fields[i].length() > 0) {
				key.append(Character.toUpperCase(fields[i].charAt(0)));
				if (fields[i].length() > 1) {
					key.append(fields[i].substring(1));
				}
			}
		}
		return key.toString();
	}

	/**
	 * @return The header value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Construct a normalized "Header: value" entry.
	 * 
	 * @return The header entry
	 */
	@Override
	public String toString() {
		return this.getNormalizedKey() + ": " + this.getValue();
	}
}