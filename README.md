stupidhttp
==========

StupidHttpServer is a simple embedded HTTP server library for Java.

Supported features:

- Embedding in applications without any external dependencies
- A small subset of HTTP/1.0 (no keep-alive, for example)
- GET, POST and HEAD requests
- Multithreading
- Extensible handlers
- Dispatching handlers based on request path
- Simple interface to headers, cookies and forms

The name comes from the lack of any advanced features. No chunked encoding support, no keep-alive, even no attempt to prevent DoS attacks.

It might also have security holes, as it hasn't been rigorously tested.

See the Javadoc comments (or generate the docs) for documentation and [Test.java](src/Test.java) for an example of usage.
