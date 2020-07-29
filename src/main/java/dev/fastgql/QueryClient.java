package dev.fastgql;

import io.vertx.core.Launcher;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

public class QueryClient extends AbstractVerticle {

  private static final String jwtToken =
      "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9."
          + "eyJpYXQiOjE1OTQ3MjA4MTF9."
          + "tCUr0CM_j6ZOiJakW2ODxvxxEJtNnmMWquSTGhJmK1aMu4aeAtHyGJlwpkmLo-"
          + "FBMWsU8elGLiTZ5xeGISS8tMWd4rfg03yyjSOjaDeNTZiMNYb0JZ06b8Sd6rGV"
          + "2FXapcgDqLlZvxfYCwL5mRIKSCZs_gmSAZ47y6RvKALA96bToB6LFJNA_vXQKW"
          + "xmFuAjuEBMs0RCGDY_VeJ9VIDUvtuW7h3sUR2Vs3XeJVtNtfwmR932UFV5ANhR"
          + "U0n_18G8i_VEtPxmGuv8Z2C-UnOaE5ryiMltXwRt15NDNy77hhzSW2xOGwnttq"
          + "xoHIixWiJuIi1Z0XPurvtf7oymIKRtBg";

  public static void main(String[] args) {
    Launcher.executeCommand("run", QueryClient.class.getName());
  }

  @Override
  public void start(Promise<Void> future) {
    WebClient client = WebClient.create(vertx);
    client
        .get(8080, "localhost", "/v1/update")
        .bearerTokenAuthentication(jwtToken)
        .expect(ResponsePredicate.SC_OK)
        .rxSend()
        .doOnSuccess(
            bufferHttpResponse -> {
              client
                  .post(8080, "localhost", "/v1/graphql")
                  .bearerTokenAuthentication(jwtToken)
                  .expect(ResponsePredicate.SC_OK)
                  .expect(ResponsePredicate.JSON)
                  .as(BodyCodec.jsonObject())
                  .rxSendJsonObject(
                      new JsonObject()
                          .put("query", "query { addresses { id customers_on_address { id } } }"))
                  .doOnSuccess(
                      jsonObjectHttpResponse -> {
                        System.out.println(jsonObjectHttpResponse.body());
                        future.complete();
                      })
                  .doOnError(future::fail)
                  .subscribe();
            })
        .doOnError(future::fail)
        .subscribe();
  }
}
