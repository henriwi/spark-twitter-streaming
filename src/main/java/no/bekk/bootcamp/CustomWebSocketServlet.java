package no.bekk.bootcamp;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CustomWebSocketServlet extends WebSocketServlet {

    static List<CustomWebSocket> connections = new LinkedList<>();

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


    public static void broadcastMessage(String text) {
        connections.stream().forEach(w -> w.sendMessage(text));
    }

    class CustomWebSocket implements WebSocket.OnTextMessage {

        WebSocket.Connection connection;
        
        @Override
        public void onClose(int closeCode, String message) {
            connections.remove(this);
            connection.close();
        }

        @Override
        public void onMessage(String data) {
            System.out.println("Received: " + data);
        }

        @Override
        public void onOpen(Connection connection) {
            this.connection = connection;
            connections.add(this);
        }

        public void sendMessage(String text) {
            try {
                connection.sendMessage(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
