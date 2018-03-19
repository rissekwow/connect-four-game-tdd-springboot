package wt.connectfourgame.controller;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.assertj.core.api.WithAssertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import info.solidsoft.mockito.java8.api.WithBDDMockito;
import wt.connectfourgame.command.RegisterCommand;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketGameControllerTest implements WithAssertions, WithBDDMockito{

	static final String WEBSOCKET_URI = "ws://localhost:8080/websocket";
    static final String WEBSOCKET_TOPIC = "/game";

    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;

    @Before
    public void setup() {
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                asList(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @Test
    public void shouldReceiveAMessageFromTheServer() throws Exception {
        StompSession session = stompClient
                .connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {})
                .get(1, SECONDS);
        session.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler());

        RegisterCommand registerCommand = new RegisterCommand();
        registerCommand.setNickname("test");
        session.send(WEBSOCKET_TOPIC, registerCommand);

        Assert.assertEquals(registerCommand, blockingQueue.poll(1, SECONDS));
    }

    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer(new String((byte[]) o));
        }
    }

}
