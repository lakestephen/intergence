package com.intergence.hgsrest.collection.databasedriven;

import com.intergence.hgsrest.util.StreamReader;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO Comments
 *
 * @author Lake
 */
public class ClasspathDatabaseDrivenQueryDefinition implements DatabaseDrivenQueryDefinition {

	private Logger log = Logger.getLogger(this.getClass());

	public String sqlFileName;

	@Override
	public String getSql() {
		checkNotNull(sqlFileName);

		URL resource = this.getClass().getResource(sqlFileName);
		log.info("Opening [" + resource + "]");

		InputStream sqlAsStream = this.getClass().getResourceAsStream(sqlFileName);
		checkNotNull(sqlAsStream, "Can't find sql [%s]", sqlFileName);

		String sql = new StreamReader().readStream(sqlAsStream);

		return sql;
	}


	public void setSqlFileName(String sqlFileName) {
		this.sqlFileName = sqlFileName;
	}

    @Override
    public String toString() {
        return "ClasspathDatabaseDrivenQueryDefinition{" +
                "sqlFileName='" + sqlFileName + '\'' +
                '}';
    }
}
