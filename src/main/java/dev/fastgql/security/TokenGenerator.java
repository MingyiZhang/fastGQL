package dev.fastgql.security;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;

public class TokenGenerator {

  private final Vertx vertx;
  private final JsonObject config;

  TokenGenerator(Vertx vertx, JsonObject authConfig) {
    this.vertx = vertx;
    this.config = authConfig;
  }

  public static TokenGenerator createTokenGenerator(Vertx vertx, JsonObject authConfig) {
    return new TokenGenerator(vertx, authConfig);
  }

  public String generateTokenWithClaims(JsonObject claims) {
    JWTAuthOptions jwtAuthOptions = new JWTAuthOptions().addPubSecKey(new PubSecKeyOptions(config));
    JWTAuth jwtAuth = JWTAuth.create(vertx, jwtAuthOptions);
    return jwtAuth.generateToken(
        claims, new JWTOptions().setAlgorithm(config.getString("algorithm")));
  }

  public String generateTokenWithRole(String role) {
    JsonObject claims = new JsonObject().put("role", role);
    return generateTokenWithClaims(claims);
  }
}
