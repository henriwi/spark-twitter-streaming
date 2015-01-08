package no.bekk.bootcamp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/"); // technically not required, as "/" is the default
        handler.addServlet(CustomWebSocketServlet.class, "/ws");
        server.setHandler(handler);
        server.start();

        new SparkApplication().run();
    }
}
