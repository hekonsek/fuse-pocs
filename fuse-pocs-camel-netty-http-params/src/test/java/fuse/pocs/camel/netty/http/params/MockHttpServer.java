package fuse.pocs.camel.netty.http.params;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MockHttpServer {

    private final int port;

    private HttpServer server;

    private final List<URI> requestsUris = new LinkedList<URI>();

    public MockHttpServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/uri", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    requestsUris.add(httpExchange.getRequestURI());
                    String response = "This is the response";
                    httpExchange.sendResponseHeaders(200, response.length());
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            });
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        server.stop(0);
    }

    public List<URI> requestsUris() {
        return new ArrayList<URI>(requestsUris);
    }

    public URI lastRequestUri() {
        return requestsUris.get(requestsUris.size() - 1);
    }

    public void reset() {
        requestsUris.clear();
    }

}