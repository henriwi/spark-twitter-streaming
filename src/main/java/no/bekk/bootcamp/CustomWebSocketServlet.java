package no.bekk.bootcamp;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomWebSocketServlet extends WebSocketServlet {

    static WebSocket.Connection connection;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getNamedDispatcher("default").forward(request,
                response);
    }

    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
        return new CustomWebSocket();
    }

    public static WebSocket.Connection getConnection() {
        return connection;
    }

    class CustomWebSocket implements WebSocket.OnTextMessage {
        private Connection connection;

        @Override
        public void onClose(int closeCode, String message) {
            connection.close();
        }

        @Override
        public void onMessage(String data) {
            System.out.println("Received: " + data);
        }

        @Override
        public void onOpen(Connection connection) {
            this.connection = connection;
            CustomWebSocketServlet.connection = connection;
        }
    }
}
