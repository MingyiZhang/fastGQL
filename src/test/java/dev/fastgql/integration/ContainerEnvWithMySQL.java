package dev.fastgql.integration;

import dev.fastgql.db.DatasourceConfig;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

public class ContainerEnvWithMySQL extends AbstractContainerEnvImpl {
  @Override
  protected JdbcDatabaseContainer<?> createJdbcContainer() {
    return new MySQLContainer<>("fastgql/mysql-testcontainers:latest")
        .withNetworkAliases("mysql")
        .withUsername("debezium")
        .withPassword("dbz");
  }

  @Override
  protected DatasourceConfig createDatasourceConfig() {
    return DatasourceConfig.createDatasourceConfig(
        jdbcDatabaseContainer.getJdbcUrl(),
        jdbcDatabaseContainer.getUsername(),
        jdbcDatabaseContainer.getPassword(),
        jdbcDatabaseContainer.getDatabaseName());
  }

  @Override
  public String getJdbcUrlForMultipleQueries() {
    return String.format("%s?allowMultiQueries=true", jdbcDatabaseContainer.getJdbcUrl());
  }
}
