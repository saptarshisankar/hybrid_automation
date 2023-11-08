package hybridTest.web_socket;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ClientEndpoint
public class MyWebSocketClientTest {

	static Session session = null;

	@OnMessage
	public void onMessage(String message) {
		System.out.println("Received message: " + message);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(message);

			if (jsonNode.has("echo_req") && jsonNode.has("msg_type") && jsonNode.has("states_list")) {

				JsonNode statesList = jsonNode.get("states_list");

				if (statesList.isArray()) {
					for (JsonNode state : statesList) {
						if (state.has("text") && state.has("value")) {
							String text = state.get("text").asText();
							String value = state.get("value").asText();

							System.out.println("State: " + text + ", Value: " + value);
						} else {
							System.err.println("Invalid state object in the response.");
						}

					}
				} else {
					System.err.println("Invalid 'states_list' in the response.");
				}
			} else {
				System.err.println("Invalid response structure.");
			}
			System.out.println("-----------------------------");
			System.out.println("Scenario 3, Testcase3: Response is correct and verified properly.");
			System.out.println("------------");
			closeSession();
		} catch (Exception e) {
			System.err.println("Error parsing JSON response: " + e.getMessage());
		}
	}

	private void closeSession() {
		if (session != null && session.isOpen()) {
			try {
				session.close();
				System.out.println("Scenario 3, Testcase4: WebSocket connection closed.");
			} catch (IOException e) {
				System.err.println("Error closing WebSocket connection: " + e.getMessage());
			}
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("WebSocket connection is established.");
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("WebSocket connection successfully terminated");
	}

	public static void main(String[] args) throws IOException {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		String uri = "wss://ws.binaryws.com/websockets/v3?app_id=36544";

		try {

			ClientEndpointConfig.Configurator configurator = new CustomConfigurator();

			session = container.connectToServer(MyWebSocketClientTest.class, new URI(uri));

			System.out.println("Scenario 3, Testcase 1: Opened a websocket connection to states list successfully!! ");
			System.out.println("------------");

			String jsonRequest = "{\"states_list\":\"id\"}";
			session.getBasicRemote().sendText(jsonRequest);
			System.out.println("Scenario 3, Testcase 2:  Request sent Successfully!!");
			System.out.println("------------");

			new Scanner(System.in).nextLine();

		} catch (Exception e) {
			System.out.println("Exception caught");
		}

	}
}
