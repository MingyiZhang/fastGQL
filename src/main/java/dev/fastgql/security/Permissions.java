package dev.fastgql.security;

import java.util.HashMap;
import java.util.Map;

public class Permissions {

  private final Map<String, TablePermissions> permissions;

  private Permissions() {
    permissions = new HashMap<>();
  }

  public static Permissions createEmptyPermission() {
    return new Permissions();
  }

  public void updateTablePermissions(String tableName, TablePermissions tablePermissions) {
    permissions.put(tableName, tablePermissions);
  }

  public void createNewTablePermissions(String tableName) {
    permissions.putIfAbsent(tableName, TablePermissions.createEmptyPermission());
  }
}
