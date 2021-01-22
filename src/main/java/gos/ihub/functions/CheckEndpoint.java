package gos.ihub.functions;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import gos.ihub.util.RestClient;

/**
 * Azure Functions with HTTP Trigger.
 */
public class CheckEndpoint {
    /**
     * This function listens at endpoint "/api/checkEndpoint". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/checkEndpoint
     * 2. curl {your host}/api/checkEndpoint?name=HTTP%20Query
     */
    @FunctionName("checkEndpoint")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, route = "check-endpoint", authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context){
        try{
            context.getLogger().info("fx checkEndpoint");
            String endpoint = request.getQueryParameters().getOrDefault("url", "https://ifconfig.me/ip");
            RestClient restClient = new RestClient(endpoint);
            Map<String, Object> response = restClient.sendGETRequest();
            return request.createResponseBuilder(HttpStatus.OK).body(response).build();
        }catch (Exception e){
            context.getLogger().severe(e.toString());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(e).build();
        }
    }
}
