package dev.fastgql;

import dev.fastgql.security.TokenGenerator;
import io.vertx.core.Launcher;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.graphql.ApolloWSMessageType;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.WebSocket;

public class SubscriptionClient extends AbstractVerticle {

  public static void main(String[] args) {
    Launcher.executeCommand(
        "run",
        SubscriptionClient.class.getName(),
        "--conf",
        "src/main/resources/conf-postgres.json");
  }

  @Override
  public void start() {
    TokenGenerator tokenGenerator =
        TokenGenerator.createTokenGenerator(vertx, config().getJsonObject("auth"));
    String jwtToken = tokenGenerator.generateTokenWithRole("admin");

    WebSocketConnectOptions wsOptions =
        new WebSocketConnectOptions()
            .addHeader(HttpHeaders.AUTHORIZATION.toString(), "Bearer " + jwtToken)
            .setHost("localhost")
            .setPort(8080)
            .setURI("/v1/graphql");
    vertx
        .createHttpClient()
        .webSocket(
            wsOptions,
            websocketRes -> {
              if (websocketRes.succeeded()) {
                WebSocket webSocket = websocketRes.result();

                webSocket.handler(
                    message -> {
                      System.out.println(message.toJsonObject().encodePrettily());
                    });

                JsonObject request =
                    new JsonObject()
                        .put("id", "1")
                        .put("type", ApolloWSMessageType.START.getText())
                        .put(
                            "payload",
                            new JsonObject()
                                .put(
                                    "query",
                                    "subscription { addresses { id customers_on_address { id } } }"));
                webSocket.write(new Buffer(request.toBuffer()));
              } else {
                websocketRes.cause().printStackTrace();
              }
            });
  }
}
