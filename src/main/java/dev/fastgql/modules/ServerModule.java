package dev.fastgql.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import dev.fastgql.modules.Annotations.AuthorizationHandler;
import dev.fastgql.modules.Annotations.ServerPort;
import dev.fastgql.modules.Annotations.UpdateHandler;
import dev.fastgql.router.ApolloWSHandlerUpdatable;
import dev.fastgql.router.GraphQLHandlerUpdatable;
import dev.fastgql.security.JWTConfig;
import graphql.GraphQL;
import io.reactivex.Single;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.graphql.GraphiQLHandler;
import javax.annotation.Nullable;
import javax.inject.Singleton;

public class ServerModule extends AbstractModule {

  @Provides
  @Singleton
  HttpServerOptions provideHttpServerOptions() {
    return new HttpServerOptions().setWebsocketSubProtocols("graphql-ws");
  }

  @Provides
  @Singleton
  GraphQLHandlerUpdatable provideGraphQLHandlerUpdatable() {
    return GraphQLHandlerUpdatable.create();
  }

  @Provides
  @Singleton
  ApolloWSHandlerUpdatable provideApolloWSHandlerUpdatable() {
    return ApolloWSHandlerUpdatable.create();
  }

  @Provides
  @Singleton
  GraphiQLHandler provideGraphiQLHandler() {
    return GraphiQLHandler.create(new GraphiQLHandlerOptions().setEnabled(true));
  }

  @Provides
  @Singleton
  @AuthorizationHandler
  Handler<RoutingContext> provideAuthorizationHandler() {
    return context -> {};
  }

  @Provides
  @Singleton
  @UpdateHandler
  Handler<RoutingContext> provideUpdateHandler(
      GraphQLHandlerUpdatable graphQLHandlerUpdatable,
      ApolloWSHandlerUpdatable apolloWSHandlerUpdatable,
      Single<GraphQL> graphQLSingle) {
    return context ->
        graphQLSingle.subscribe(
            graphQL -> {
              if (graphQLHandlerUpdatable != null) {
                graphQLHandlerUpdatable.updateGraphQL(graphQL);
              }
              if (apolloWSHandlerUpdatable != null) {
                apolloWSHandlerUpdatable.updateGraphQL(graphQL);
              }
              HttpServerResponse response = context.response();
              response.putHeader("content-type", "text/html").end("updated");
            },
            context::fail);
  }

  @Provides
  @Singleton
  JWTAuthHandler provideJWTAuthHandler(Vertx vertx, JWTConfig jwtConfig) {
    return jwtConfig.getJWTAuthHandler(vertx);
  }

  @Provides
  Single<Router> provideRouterSingle(
      Vertx vertx,
      @Nullable JWTAuthHandler jwtAuthHandler,
      GraphQLHandlerUpdatable graphQLHandlerUpdatable,
      ApolloWSHandlerUpdatable apolloWSHandlerUpdatable,
      GraphiQLHandler graphiQLHandler,
      @UpdateHandler Handler<RoutingContext> updateHandler,
      Single<GraphQL> graphQLSingle) {
    return graphQLSingle.map(
        graphQL -> {
          Router router = Router.router(vertx);
          if (jwtAuthHandler != null) {
            router.route("/v1/*").handler(jwtAuthHandler);
          }
          if (apolloWSHandlerUpdatable != null) {
            apolloWSHandlerUpdatable.updateGraphQL(graphQL);
            router.route("/v1/graphql").handler(apolloWSHandlerUpdatable);
          }
          if (graphQLHandlerUpdatable != null) {
            graphQLHandlerUpdatable.updateGraphQL(graphQL);
            router.route("/v1/graphql").handler(graphQLHandlerUpdatable);
          }
          if (graphiQLHandler != null) {
            router.route("/graphiql/*").handler(graphiQLHandler);
          }
          if (updateHandler != null) {
            router.route("/v1/update").handler(updateHandler);
          }
          return router;
        });
  }

  @Provides
  Single<HttpServer> provideHttpServer(
      Vertx vertx,
      HttpServerOptions httpServerOptions,
      Single<Router> routerSingle,
      @ServerPort int port) {
    return routerSingle.flatMap(
        router -> vertx.createHttpServer(httpServerOptions).requestHandler(router).rxListen(port));
  }
}
