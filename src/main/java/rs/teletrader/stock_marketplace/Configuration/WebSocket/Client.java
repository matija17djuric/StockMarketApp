package rs.teletrader.stock_marketplace.Configuration.WebSocket;

import org.springframework.web.socket.WebSocketSession;

public class Client {
    Client(WebSocketSession session, Integer idUser) {
        this.idUser = idUser;
        this.session = session;
    }

    WebSocketSession session;
    Integer idUser;
}
