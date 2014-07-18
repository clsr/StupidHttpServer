import net.clsr.stupidhttp.StupidHttpCookie;
import net.clsr.stupidhttp.StupidHttpForm;
import net.clsr.stupidhttp.StupidHttpHandler;
import net.clsr.stupidhttp.StupidHttpHeader;
import net.clsr.stupidhttp.StupidHttpMuxer;
import net.clsr.stupidhttp.StupidHttpRequest;
import net.clsr.stupidhttp.StupidHttpResponse;
import net.clsr.stupidhttp.StupidHttpServer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple example server that tests a few features.
 */
public class Test {
	/**
	 * The main method.
	 * 
	 * @param args Ignored
	 */
	public static void main(String[] args) {
		Logger.getGlobal().setLevel(Level.ALL);
		StupidHttpServer server = new StupidHttpServer("localhost", 8080);
		server.setAccessLogger(Logger.getGlobal());
		server.setErrorLogger(Logger.getGlobal());
		StupidHttpMuxer mux = (StupidHttpMuxer) server.getHandler();

		mux.setHandler("/query", new StupidHttpHandler() {
			@Override
			public StupidHttpResponse handle(StupidHttpRequest req) {
				StupidHttpForm query = req.getQueryForm();
				StringBuilder sb = new StringBuilder();
				for (StupidHttpForm.Pair p : query) {
					sb.append(p.getKey()).append(": ").append(p.getValue()).append('\n');
				}
				return StupidHttpResponse.textResponse(sb.toString());
			}
		});

		mux.setHandler("/post", new StupidHttpHandler() {
			@Override
			public StupidHttpResponse handle(StupidHttpRequest req) {
				StupidHttpForm post = req.getPostForm();
				StringBuilder sb = new StringBuilder();
				for (StupidHttpForm.Pair p : post) {
					sb.append(p.getKey()).append(": ").append(p.getValue()).append('\n');
				}
				return StupidHttpResponse.textResponse(sb.toString());
			}
		});

		mux.setHandler("/cookies", new StupidHttpHandler() {
			@Override
			public StupidHttpResponse handle(StupidHttpRequest req) {
				StringBuilder sb = new StringBuilder();
				for (StupidHttpCookie c : req.getCookies()) {
					sb.append(c.getKey()).append(": ").append(c.getValue()).append('\n');
				}
				return StupidHttpResponse.textResponse(sb.toString());
			}
		});

		mux.setHandler("/headers", new StupidHttpHandler() {
			@Override
			public StupidHttpResponse handle(StupidHttpRequest req) {
				StringBuilder sb = new StringBuilder();
				for (StupidHttpHeader h : req.getHeaders()) {
					sb.append(h.getKey()).append(": ").append(h.getValue()).append('\n');
				}
				return StupidHttpResponse.textResponse(sb.toString());
			}
		});

		mux.setHandler("/ip", new StupidHttpHandler() {
			@Override
			public StupidHttpResponse handle(StupidHttpRequest req) {
				return StupidHttpResponse.textResponse(req.getRemoteAddress());
			}
		});

		mux.setHandler("/hello", new StupidHttpHandler() {
			@Override
			public StupidHttpResponse handle(StupidHttpRequest req) {
				return StupidHttpResponse.htmlResponse("<html><head><title>StupidHttpServer</title></head><body><h1>It works!</h1></body></html>");
			}
		});
		try {
			server.start();
			System.out.println("now listening on " + server.getAddress() + ":" + server.getPort());
			server.listenAndServe();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
