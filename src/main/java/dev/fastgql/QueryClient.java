package dev.fastgql;

import dev.fastgql.security.TokenGenerator;
import io.vertx.core.Launcher;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class QueryClient extends AbstractVerticle {

  public static void main(String[] args) {
    Launcher.executeCommand(
        "run", QueryClient.class.getName(), "--conf", "src/main/resources/conf-postgres.json");
  }

  @Override
  public void start(Promise<Void> future) {
    TokenGenerator tokenGenerator =
        TokenGenerator.createTokenGenerator(vertx, config().getJsonObject("auth"));

    WebClient.create(vertx)
        .post(8080, "localhost", "/v1/graphql")
        .bearerTokenAuthentication(tokenGenerator.generateTokenWithRole("admin"))
        .expect(ResponsePredicate.SC_OK)
        .expect(ResponsePredicate.JSON)
        .as(BodyCodec.jsonObject())
        .rxSendJsonObject(
            new JsonObject().put("query", "query { addresses { id customers_on_address { id } } }"))
        .subscribe(
            response -> {
              System.out.println(response.body());
              future.complete();
            },
            future::fail);
  }
}
