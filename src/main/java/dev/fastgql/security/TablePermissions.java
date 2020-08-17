package dev.fastgql.security;

import java.util.HashMap;
import java.util.Map;

public class TablePermissions {

  private final Map<String, Permission> selectPermissions;

  private TablePermissions() {
    this.selectPermissions = new HashMap<>();
  }

  public static TablePermissions createEmptyPermission() {
    return new TablePermissions();
  }

  public void updateSelectPermission(String role, Permission permission) {
    selectPermissions.put(role, permission);
  }
}
