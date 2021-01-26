package com.intergence.hgsrest.collection.databasedriven;

import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class ClasspathDatabaseDrivenQueryDefinitionTest {


	@Test
	public void readsSqlFileContents() throws IOException {
		ClasspathDatabaseDrivenQueryDefinition classpathDatabaseDrivenQueryDefinition = new ClasspathDatabaseDrivenQueryDefinition();
		classpathDatabaseDrivenQueryDefinition.setSqlFileName("/queries/test.sql");

		String sql = classpathDatabaseDrivenQueryDefinition.getSql();
		assertEquals("select * from STEPHEN", sql);
	}
}