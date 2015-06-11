package twitter.stream;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(getPort());
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/"); // technically not required, as "/" is the default
        handler.addServlet(CustomWebSocketServlet.class, "/ws");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("src/main/webapp");
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {handler, resourceHandler});
        server.setHandler(handlers);

        server.start();

        new SparkApplication().run();
    }

    private static int getPort() {
        String port = getenv("PORT");

        if (port == null) {
            return 8080;
        } else {
            return parseInt(port);
        }
    }
}
