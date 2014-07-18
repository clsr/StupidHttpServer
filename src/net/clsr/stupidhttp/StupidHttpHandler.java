package net.clsr.stupidhttp;

/**
 * StupidHttpHandler handles an accepted connection.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public interface StupidHttpHandler {
	/**
	 * Handle a request.
	 * 
	 * @param req The request to handle
	 * @return The response to the request
	 */
	public StupidHttpResponse handle(StupidHttpRequest req);
}