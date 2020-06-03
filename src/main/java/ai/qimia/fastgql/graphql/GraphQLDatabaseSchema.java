package ai.qimia.fastgql.graphql;

import ai.qimia.fastgql.db.DatabaseSchema;
import ai.qimia.fastgql.common.QualifiedName;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GraphQLDatabaseSchema {
  private Map<String, Map<String, GraphQLNodeDefinition>> graph;

  private String getNameForReferencingField(QualifiedName qualifiedName) {
    Objects.requireNonNull(qualifiedName);
    return String.format("%s_ref", qualifiedName.getName());
  }

  private String getNameForReferencedByField(QualifiedName qualifiedName) {
    Objects.requireNonNull(qualifiedName);
    return String.format("%s_on_%s", qualifiedName.getParent(), qualifiedName.getName());
  }

  public GraphQLNodeDefinition nodeAt(String table, String field) {
    return graph.get(table).get(field);
  }

  public GraphQLDatabaseSchema(DatabaseSchema databaseSchema) {
    Objects.requireNonNull(databaseSchema);
    graph = new HashMap<>();
    databaseSchema.getGraph().forEach((parent, subgraph) -> {
      graph.put(parent, new HashMap<>());
      Map<String, GraphQLNodeDefinition> graphQLSubgraph = graph.get(parent);
      subgraph.forEach((name, node) -> {
        QualifiedName qualifiedName = node.getQualifiedName();
        QualifiedName referencing = node.getReferencing();
        Set<QualifiedName> referencedBySet = node.getReferencedBy();
        graphQLSubgraph.put(name, GraphQLNodeDefinition.createLeaf(qualifiedName, node.getFieldType()));
        if (referencing != null) {
          String referencingName = getNameForReferencingField(qualifiedName);
          graphQLSubgraph.put(referencingName, GraphQLNodeDefinition.createReferencing(qualifiedName, referencing));
        }
        referencedBySet.forEach(referencedBy -> {
          String referencedByName = getNameForReferencedByField(referencedBy);
          graphQLSubgraph.put(referencedByName, GraphQLNodeDefinition.createReferencedBy(qualifiedName, referencedBy));
        });
      });
    });
  }

  public void applyToGraphQLObjectType(GraphQLObjectType.Builder builder) {
    Objects.requireNonNull(builder);
    graph.forEach((parent, subgraph) -> {
      GraphQLObjectType.Builder object = GraphQLObjectType.newObject()
        .name(parent);
      subgraph.forEach((name, node) -> {
        object.field(GraphQLFieldDefinition.newFieldDefinition()
          .name(name)
          .type(node.getGraphQLType())
          .build());
      });
      builder.field(GraphQLFieldDefinition.newFieldDefinition()
        .name(parent)
        .type(GraphQLList.list(object.build()))
        .build());
    });
  }

  @Override
  public String toString() {
    return "GraphQLSchema{" +
      "graph=" + graph +
      '}';
  }
}
