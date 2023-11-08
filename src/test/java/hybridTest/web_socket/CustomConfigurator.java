package hybridTest.web_socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;

public class CustomConfigurator  extends ClientEndpointConfig.Configurator {
	@Override
    public void beforeRequest(Map<String, List<String>> headers) {
        super.beforeRequest(headers);

        headers.put("Sec-Websocket-Accept", List.of("d/HlLB4m69ccSXvXD5AH2plulKU="));
    }
	
	

}
