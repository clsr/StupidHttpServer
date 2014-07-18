package net.clsr.stupidhttp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * StupidHttpMuxer is a handler that dispatches other handlers based on the request path.
 * 
 * The associates paths with handlers. When it is called to handle a request, the request path is examined and a handler is chosen based on that.
 * If the path is a directory (ends with "/"), it will match based on prefix (all subdirectories and files will also match).
 * Otherwise, only the exact string will match.
 * If there are multiple matches, the handler with the longest match path that matches will be chosen.
 * For example, if the request path is "/foo/bar/baz" and we have "/foo/", "/foo/bar/" and "/foo/bar/quux" handlers, the "/foo/bar/" one will be chosen.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpMuxer implements StupidHttpHandler {
	private Map<String, StupidHttpHandler> handlers = Collections.synchronizedMap(new HashMap<String, StupidHttpHandler>());
	private StupidHttpHandler handle404 = StupidHttpStatus.handle404;

	/**
	 * Create a new muxer.
	 */
	public StupidHttpMuxer() {}

	/**
	 * Add a handler for a path.
	 * 
	 * @param path The path to match; if it ends with /, it also matches all sub-folders and files
	 * @param handler The handler to associate with the path
	 */
	public void setHandler(String path, StupidHttpHandler handler) {
		this.handlers.put(path, handler);
	}

	/**
	 * Sets the handler that handles all requests that match no other handlers.
	 * The default is {@link StupidHttpStatus#handle404}.
	 * 
	 * @param handle404 The handler
	 */
	public void setNotFoundHandler(StupidHttpHandler handle404) {
		this.handle404 = handle404;
	}

	/**
	 * Dispatches a handler to handle the request based on the request's path.
	 */
	@Override
	public StupidHttpResponse handle(StupidHttpRequest req) {
		StupidHttpHandler handler = this.match(req.getPath());
		if (handler == null) {
			return this.handle404.handle(req);
		}
		return handler.handle(req);
	}

	/**
	 * Resolve the handler for a path.
	 * 
	 * @param path The request path
	 * @return The handler that matches that path
	 */
	public StupidHttpHandler match(String path) {
		StupidHttpHandler handler = null;
		int maxPatternLength = 0;
		for (Map.Entry<String, StupidHttpHandler> e : this.handlers.entrySet()) {
			if (!this.matchPath(e.getKey(), path)) {
				continue;
			}
			if (handler == null || e.getKey().length() > maxPatternLength) {
				maxPatternLength = e.getKey().length();
				handler = e.getValue();
			}
		}
		return handler;
	}

	private boolean matchPath(String pattern, String path) {
		if (pattern.length() == 0) {
			return path.equals("/");
		}
		if (pattern.charAt(pattern.length() - 1) != '/') { // match filepath, as in /foo/bar.baz
			return pattern.equals(path);
		}
		return path.startsWith(pattern); // match a directory path, as in /foo/bar/
	}
}