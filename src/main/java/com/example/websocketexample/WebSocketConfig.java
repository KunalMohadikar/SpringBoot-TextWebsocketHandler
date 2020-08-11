package com.example.websocketexample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	
	ArrayList<WebSocketSession> sessions = new ArrayList<WebSocketSession>();
	int x = 0;

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(auctionHandler(), "/user");
	}
	
	@Bean
    public HandshakeInterceptor auctionInterceptor() {
        return new HandshakeInterceptor() {
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                  WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

                // Get the URI segment corresponding to the auction id during handshake
                String path = request.getURI().getPath();
//                System.out.println(path);
                String auctionId = path.substring(path.lastIndexOf('/') + 1);

                // This will be added to the websocket session
                attributes.put("auctionId", path);
                return true;
            }

            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                    WebSocketHandler wsHandler, Exception exception) {
                // Nothing to do after handshake
            }
        };
    }
	
	@Bean
    public WebSocketHandler auctionHandler() {
        return new TextWebSocketHandler() {
            public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
                // Retrieve the auction id from the websocket session (copied during the handshake)
                String auctionId = (String) session.getAttributes().get("auctionId");

               
        		for(WebSocketSession webSocketSession : sessions) {
        			String payload = message.getPayload();
            		JSONObject jsonObject = new JSONObject(payload);
//            		webSocketSession.sendMessage(new TextMessage("Hi " + jsonObject.get("user") + " "+ auctionId+" "+" how may we help you?"));
            		System.out.println(sessions.size());
            		String[] ar = webSocketSession.getUri().toString().split("/");
            		
            		System.out.println(ar[ar.length-1]);
//            		!(webSocketSession.getId().toString().equals(session.getId().toString())) &&
            		if( !(webSocketSession.getId().toString().equals(session.getId().toString())) &&(webSocketSession.getUri().toString().equals(session.getUri().toString())) ) {
            			webSocketSession.sendMessage(new TextMessage("Hi " + jsonObject.get("user") + " "+ auctionId+" "+" how may we help you?"));
                		x++;
                		System.out.println("x: "+ x);
            		}
            		
        		}
            }
            
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        		//the messages will be broadcasted to all users.
            	sessions.add(session);
            	session.sendMessage(new TextMessage("Hello new Session"));
            	System.out.println("Session Stated");
        	}
            
            public void afterConnectionClosed(WebSocketSession session,CloseStatus closeStatus) {
            	sessions.remove(session);
            	System.out.println("Seesion Closed");
            	
            }
        };
    }

}
