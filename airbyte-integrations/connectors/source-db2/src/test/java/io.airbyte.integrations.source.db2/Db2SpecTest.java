/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.source.db2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.airbyte.commons.io.IOs;
import io.airbyte.commons.json.Jsons;
import io.airbyte.commons.resources.MoreResources;
import io.airbyte.protocol.models.v0.ConnectorSpecification;
import io.airbyte.validation.json.JsonSchemaValidator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class Db2SpecTest {

  private static JsonNode schema;
  private static JsonNode config;
  private static String configText;
  private static JsonSchemaValidator validator;

  @BeforeAll
  static void init() throws IOException {
    configText = """
                 {
                   "host": "localhost",
                   "port": 1521,
                   "db": "db",
                   "username": "test",
                   "password": "password",
                   "jdbc_url_params": "property1=pValue1&property2=pValue2"
                 }
                 """;
    final String spec = MoreResources.readResource("spec.json");
    final File schemaFile = IOs.writeFile(Files.createTempDirectory(Path.of("/tmp"), "pg-spec-test"), "schema.json", spec).toFile();
    schema = JsonSchemaValidator.getSchema(schemaFile).get("connectionSpecification");
    validator = new JsonSchemaValidator();
  }

  @BeforeEach
  void beforeEach() {
    config = Jsons.deserialize(configText);
  }

  @Test
  void testHostMissing() {
    ((ObjectNode) config).remove("host");
    assertFalse(validator.test(schema, config));
  }

  @Test
  void testPortMissing() {
    ((ObjectNode) config).remove("port");
    assertFalse(validator.test(schema, config));
  }

  @Test
  void testDatabaseMissing() {
    ((ObjectNode) config).remove("db");
    assertFalse(validator.test(schema, config));
  }

  @Test
  void testUsernameMissing() {
    ((ObjectNode) config).remove("username");
    assertFalse(validator.test(schema, config));
  }

  @Test
  void testPasswordMissing() {
    ((ObjectNode) config).remove("password");
    assertFalse(validator.test(schema, config));
  }

  @Test
  void testJdbcAdditionalProperty() throws Exception {
    final ConnectorSpecification spec = new Db2Source().spec();
    assertNotNull(spec.getConnectionSpecification().get("properties").get("jdbc_url_params"));
  }

}
