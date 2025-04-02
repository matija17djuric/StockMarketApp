package rs.teletrader.stock_marketplace.Configuration.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final Map<Integer, SessionRoom> rooms = new HashMap();

    @Bean("rooms")
    public Map<Integer, SessionRoom> getRooms() {
        return rooms;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new OrdersWebsocketHandler(rooms), "/orders/{idCompany}/{idUser}");
    }

}
