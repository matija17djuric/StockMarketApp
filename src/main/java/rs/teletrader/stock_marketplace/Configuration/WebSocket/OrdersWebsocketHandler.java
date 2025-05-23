package rs.teletrader.stock_marketplace.Configuration.WebSocket;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrdersWebsocketHandler extends AbstractWebSocketHandler {

    private final Map<Integer, SessionRoom> rooms;

    public void informUsers(Integer idCompany, Integer idUser1, Integer idUser2) {
        if (this.rooms.containsKey(idCompany)) {
            this.rooms.get(idCompany).informUsers(idUser1, idUser2);
        }
    }

    public SessionRoom getRoom(Integer idCompany) {
        if (rooms.containsKey(idCompany)) {
            return rooms.get(idCompany);
        } else {
            return null;
        }
    }

    public void sendMessagetoUser(Integer idCompany, Integer idUser, String message) {
        if (this.rooms.containsKey(idCompany)) {
            this.rooms.get(idCompany).sendMessagetoUser(idUser, message);
        }
    }

    public void disconnectUser(Integer idCompany, Integer idUser) {
        if (this.rooms.containsKey(idCompany)) {
            this.rooms.get(idCompany).disconnectUser(idUser);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String path = session.getUri().getPath();
        String parameters = path.replaceAll("/ws/orders/", "");
        String[] parametersList = parameters.split("/");
        Integer idCompany = Integer.valueOf(parametersList[0]);
        Integer idUser = Integer.valueOf(parametersList[1]);
        if (rooms.containsKey(idCompany)) {
            rooms.get(idCompany).disconnectUser(idUser);
            if (rooms.get(idCompany).getRoomSize() == 0)
                rooms.remove(idCompany);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String path = session.getUri().getPath();
        String parameters = path.replaceAll("/ws/orders/", "");
        String[] parametersList = parameters.split("/");
        Integer idCompany = Integer.valueOf(parametersList[0]);
        Integer idUser = Integer.valueOf(parametersList[1]);
        if (!rooms.containsKey(idCompany)) {
            rooms.put(idCompany, new SessionRoom());
        }
        rooms.get(idCompany).addClient(idUser, session);
    }

}
