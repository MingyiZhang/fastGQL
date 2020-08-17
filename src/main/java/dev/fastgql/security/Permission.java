package dev.fastgql.security;

import io.vertx.core.json.JsonObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Permission {

  private final Set<String> columns;
  private JsonObject condition;

  private Permission() {
    this.columns = new HashSet<>();
    this.condition = new JsonObject();
  }

  public static Permission createEmptyPermission() {
    return new Permission();
  }

  public void addColumn(String column) {
    this.columns.add(column);
  }

  public void addAllColumns(Collection<String> columns) {
    this.columns.addAll(columns);
  }

  public boolean isColumnPermitted(String column) {
    return this.columns.contains(column);
  }

  public void setCondition(JsonObject condition) {
    this.condition = condition;
  }
}
