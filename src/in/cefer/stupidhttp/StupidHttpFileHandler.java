package in.cefer.stupidhttp;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * StupidHttpFileHandler implements a {@link StupidHttpHandler} that serves a file.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpFileHandler implements StupidHttpHandler {
	private URL url;

	/**
	 * Creates a new handler.
	 * 
	 * @param filename The file to serve
	 * @throws MalformedURLException The filename is malformed
	 */
	public StupidHttpFileHandler(String filename) throws MalformedURLException {
		this.url = new File(filename).toURI().toURL();
	}

	/**
	 * Creates a new handler.
	 * 
	 * @param file The file to serve
	 * @throws MalformedURLException The filename is malformed
	 */
	public StupidHttpFileHandler(File file) throws MalformedURLException {
		this.url = file.toURI().toURL();
	}

	/**
	 * Creates a new handler.
	 * 
	 * @param url The URL to serve
	 */
	public StupidHttpFileHandler(URL url) {
		this.url = url;
	}

	@Override
	public StupidHttpResponse handle(StupidHttpRequest req) {
		return StupidHttpResponse.fileResponse(this.url);
	}
}