package dev.fastgql;

import dev.fastgql.integration.*;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class IntegrationTests {

  @Nested
  @DisplayName("PostgreSQL Tests")
  class Postgres {

    @Nested
    @DisplayName("PostgreSQL Query Tests")
    class PostgresQuery extends ContainerEnvWithDatabase implements WithPostgres, QueryTests {}

    @Nested
    @DisplayName("PostgreSQL Query Tests with JWT Security")
    class PostgresQueryWithSecurity extends ContainerEnvWithDatabase
        implements WithPostgres, WithSecurity, QueryTestsWithSecurity {}

    @Nested
    @DisplayName("PostgreSQL Subscription Tests with external Debezium")
    class PostgresSubscriptionExternalDebezium extends ContainerEnvWithDatabaseAndDebezium
        implements WithPostgres, WithPostgresConnector, SubscriptionTests {}

    @Nested
    @DisplayName("PostgreSQL Subscription Tests with embedded Debezium")
    class PostgresSubscriptionEmbeddedDebezium extends ContainerEnvWithDatabase
        implements WithPostgres, WithEmbeddedDebezium, SubscriptionTests {}

    @Nested
    @DisplayName("PostgreSQL Subscription Tests with JWT Security")
    class PostgresSubscriptionWithSecurity extends ContainerEnvWithDatabaseAndDebezium
        implements WithPostgres,
            WithPostgresConnector,
            WithSecurity,
            SubscriptionTestsWithSecurity {}
  }

  @Nested
  @DisplayName("MySQL Tests")
  class MySQL {

    @Nested
    @DisplayName("MySQL Query Tests")
    class MySQLQuery extends ContainerEnvWithDatabase implements WithMySQL, QueryTests {}

    @Nested
    @DisplayName("MySQL Query Tests with JWT Security")
    class MySQLQueryWithSecurity extends ContainerEnvWithDatabase
        implements WithMySQL, WithSecurity, QueryTestsWithSecurity {}

    @Nested
    @DisplayName("MySQL Subscription Tests with external Debezium")
    class MySQLSubscriptionExternalDebezium extends ContainerEnvWithDatabaseAndDebezium
        implements WithMySQL, WithMySQLConnector, SubscriptionTests {}

    @Nested
    @DisplayName("MySQL Subscription Tests with embedded Debezium")
    class MySQLSubscriptionEmbeddedDebezium extends ContainerEnvWithDatabase
        implements WithMySQL, WithEmbeddedDebezium, SubscriptionTests {}

    @Nested
    @DisplayName("MySQL Subscription Tests with JWT Security")
    class MySQLSubscriptionWithSecurity extends ContainerEnvWithDatabaseAndDebezium
        implements WithMySQL, WithMySQLConnector, WithSecurity, SubscriptionTestsWithSecurity {}
  }
}
