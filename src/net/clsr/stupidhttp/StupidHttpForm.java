package net.clsr.stupidhttp;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * StupidHttpForm represents HTTP GET or POST form data.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpForm implements Iterable<StupidHttpForm.Pair> {
	private final List<Pair> query = new ArrayList<Pair>();

	/**
	 * Constructs a form from a raw string.
	 * 
	 * @param query The query string (URL-encoded key=value pairs separated by &)
	 */
	public StupidHttpForm(String query) {
		for (String pair : query.split("&")) {
			if (!pair.isEmpty()) {
				this.query.add(new Pair(pair));
			}
		}
	}

	/**
	 * Constructs a form from a map.
	 * 
	 * @param query The map form keys to form values
	 */
	public StupidHttpForm(Map<String, String> query) {
		for (Map.Entry<String, String> e : query.entrySet()) {
			this.query.add(new Pair(e.getKey(), e.getValue()));
		}
	}

	/**
	 * Constructs a form from an ordered array of pairs.
	 * 
	 * @param query Pairs of form keys and values
	 */
	public StupidHttpForm(Pair[] query) {
		for (Pair p : query) {
			this.query.add(p);
		}
	}

	/**
	 * Resolve a form key to a value.
	 * If there are multiple values associated with this key, the first one is returned.
	 * 
	 * @param key The form key
	 * @return The form value associated with the key
	 */
	public String get(String key) {
		for (Pair p : this.query) {
			if (p.getKey().equals(key)) {
				return p.getValue();
			}
		}
		return null;
	}

	/**
	 * Get all values associated with a key.
	 * 
	 * @param key The form key
	 * @return Array of form values associated with the key
	 */
	public String[] getAll(String key) {
		List<String> l = new ArrayList<String>();
		for (Pair p : this.query) {
			if (p.getKey().equals(key)) {
				l.add(p.getValue());
			}
		}
		return l.toArray(new String[l.size()]);
	}

	/**
	 * Get a map from keys to values.
	 * If there are keys with more than one value, only the first value is returned.
	 * 
	 * @return The map of form keys to form values
	 */
	public Map<String, String> toMap() {
		Map<String, String> m = new HashMap<String, String>();
		for (Pair p : this.query) {
			if (!m.containsKey(p.getKey())) {
				m.put(p.getKey(), p.getValue());
			}
		}
		return m;
	}

	/**
	 * Get an array of form entries.
	 * 
	 * @return An array of form entries
	 */
	public Pair[] toArray() {
		return this.query.toArray(new Pair[this.query.size()]);
	}

	/**
	 * URL-encode the form.
	 * 
	 * @return The URL-encoded form
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Pair p : this.query) {
			if (first) {
				first = false;
			} else {
				sb.append('&');
			}
			sb.append(p.toString());
		}
		return sb.toString();
	}

	@Override
	public Iterator<Pair> iterator() {
		return this.query.iterator();
	}

	/**
	 * Pair represents a key=value form entry.
	 */
	public class Pair {
		private final String key;
		private final String value;

		/**
		 * Construct a pair from a raw URL-encoded "key=value" string.
		 * 
		 * @param urlEncodedPair URL-encoded "key=value" pair
		 */
		public Pair(String urlEncodedPair) {
			String[] fields = urlEncodedPair.split("=", 2);
			String key = null;
			String value = null;
			try {
				key = URLDecoder.decode(fields[0], "UTF-8");
				if (fields.length == 2) {
					value = URLDecoder.decode(fields[1], "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {}
			this.key = key;
			this.value = value;
		}

		/**
		 * Construct a pair from key and value strings.
		 * 
		 * @param key The form key
		 * @param value The value associated with the key
		 */
		public Pair(String key, String value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * @return The key
		 */
		public String getKey() {
			return this.key;
		}

		/**
		 * @return URL-encoded key
		 */
		public String getEncodedKey() {
			try {
				return URLEncoder.encode(this.key, "UTF-8");
			} catch (UnsupportedEncodingException e) {}
			return null;
		}

		/**
		 * @return The value
		 */
		public String getValue() {
			return this.value;
		}

		/**
		 * @return URL-encoded value
		 */
		public String getEncodedValue() {
			try {
				return URLEncoder.encode(this.value, "UTF-8");
			} catch (UnsupportedEncodingException e) {}
			return null;
		}

		/**
		 * URL-encode the pair to "key=value" string.
		 * 
		 * @return URL-encoded pair
		 */
		@Override
		public String toString() {
			if (this.value == null) {
				return this.getEncodedKey();
			}
			return this.getEncodedKey() + "=" + this.getEncodedValue();
		}
	}
}