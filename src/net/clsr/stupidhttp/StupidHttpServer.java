package net.clsr.stupidhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StupidHttpServer is the core class of a HTTP server.
 * This class provides socket listeners, dispatches handlers, handles threading and logs access.
 * 
 * @author Marko Ceferin <marko@cefer.in>
 * @version 1.0
 */
public class StupidHttpServer {
	private Logger accessLogger;
	private Logger errorLogger;
	private ServerSocket sock;
	private String address;
	private int port;
	private ExecutorService executor = Executors.newCachedThreadPool();
	private StupidHttpHandler handler = new StupidHttpMuxer();

	/**
	 * Create a new HTTP server listening on port 80
	 */
	public StupidHttpServer() {
		this.port = 80;
	}

	/**
	 * Create a new HTTP server listening on the specified address and port.
	 * 
	 * @param address The address to listen on
	 * @param port The port to listen on
	 */
	public StupidHttpServer(String address, int port) {
		this.address = address;
		this.port = port;
	}

	/**
	 * Starts a new server socket.
	 * 
	 * @throws IOException Exception from {@link ServerSocket#ServerSocket(int)}
	 */
	public void start() throws IOException {
		this.sock = new ServerSocket(this.port, 0, InetAddress.getByName(this.address));
	}

	/**
	 * Closes the server socket.
	 * 
	 * @throws IOException Exception from {@link ServerSocket#close()}
	 */
	public void stop() throws IOException {
		this.sock.close();
		this.sock = null;
	}

	/**
	 * Accept and handle one connection.
	 * 
	 * @throws IOException Exception from {@link ServerSocket#accept()} or the handler
	 * @throws StupidHttpException Exception from the handler
	 */
	public void accept() throws IOException, StupidHttpException {
		Socket s = this.sock.accept();
		this.handle(s);
	}

	/**
	 * Accept and handle connections indefinitely on several threads.
	 */
	public void listenAndServe() {
		for (;;) {
			try {
				final Socket s = this.sock.accept();
				this.executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							StupidHttpServer.this.handle(s);
						} catch (StupidHttpException e) {
							handleError(e, s, StupidHttpStatus.BadRequest);
						} catch (IOException e) {
							handleError(e, s, StupidHttpStatus.InternalServerError);
						}
					}
				});
			} catch (SocketException e) {
				this.logError(e, Level.SEVERE);
				break;
			} catch (IOException e) {
				this.logError(e);
			}
		}
	}

	/**
	 * @return The address the server is listening on
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * @return The port the server is listening on
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * @return The handler for accepted connections; default is a {@link StupidHttpMuxer}
	 */
	public StupidHttpHandler getHandler() {
		return this.handler;
	}

	/**
	 * Sets a new handler that handles all accepted connections.
	 * 
	 * @param handler The new handler
	 */
	public void setHandler(StupidHttpHandler handler) {
		this.handler = handler;
	}

	/**
	 * Sets a logger for handled requests.
	 * 
	 * @param l The new logger
	 */
	public void setAccessLogger(Logger l) {
		this.accessLogger = l;
	}

	/**
	 * Sets a logger for errors.
	 * 
	 * @param l The new logger
	 */
	public void setErrorLogger(Logger l) {
		this.errorLogger = l;
	}

	private void handle(Socket s) throws IOException, StupidHttpException {
		StupidHttpRequest req = new StupidHttpRequest(s);
		StupidHttpResponse resp = this.handler.handle(req);
		this.logAccess(req, resp);
		OutputStream out = s.getOutputStream();
		resp.writeTo(out, !req.isHead());
		s.close();
	}

	private void handleError(Exception e, Socket s, int code) {
		this.logError(e);
		StupidHttpResponse resp = new StupidHttpResponse();
		resp.setCode(code);
		resp.setBody(String.format("%d %s: %s", code, StupidHttpStatus.statusText(code), e.getMessage()));
		try {
			resp.writeTo(s.getOutputStream());
			s.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void logAccess(StupidHttpRequest req, StupidHttpResponse resp) {
		if (this.accessLogger != null) {
			this.accessLogger.log(Level.INFO, String.format("%s - %s %s - %d", req.getRemoteAddress(), req.getMethod(), req.getPath(), resp.getCode()));
		}
	}

	private void logError(Exception e) {
		this.logError(e, Level.WARNING);
	}

	private void logError(Exception e, Level l) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		PrintStream bufps = new PrintStream(buf);
		e.printStackTrace(bufps);
		this.logError(buf.toString(), l);
	}

	private void logError(String s) {
		this.logError(s, Level.WARNING);
	}

	private void logError(String s, Level l) {
		if (this.errorLogger != null) {
			this.errorLogger.log(l, s);
		}
	}
}
