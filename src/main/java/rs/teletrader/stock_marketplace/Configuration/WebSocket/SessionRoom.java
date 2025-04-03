package rs.teletrader.stock_marketplace.Configuration.WebSocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import rs.teletrader.stock_marketplace.Models.Stocks.Company.CompanyModel;
import rs.teletrader.stock_marketplace.Services.Stock.OrderService.IOrderService;

public class SessionRoom {

    IOrderService iOrderService;
    CompanyModel company;

    SessionRoom() {
        this.clients = new HashMap<>();
    }

    private final Map<Integer, WebSocketSession> clients;

    public Integer getRoomSize() {
        return this.clients.size();
    }

    public void sendMessagetoUser(Integer idUser, String message) {

        try {
            if (clients.containsKey(idUser)) {
                clients.get(idUser).sendMessage(new TextMessage(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnectUser(Integer idUser) {
        try {
            if (clients.containsKey(idUser)) {
                clients.get(idUser).close();
                clients.remove(idUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void informUsers(Integer idUser1, Integer idUser2) {
        try {
            if (clients.containsKey(idUser1)) {
                clients.get(idUser1).sendMessage(new TextMessage("Success"));
                clients.get(idUser1).close();
                clients.remove(idUser1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (clients.containsKey(idUser2)) {
                clients.get(idUser2).sendMessage(new TextMessage("Success"));
                clients.get(idUser2).close();
                clients.remove(idUser2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addClient(Integer idUser, WebSocketSession session) {
        clients.put(idUser, session);
    }
}
