/**
 * Copyright 2010 Real-Status Ltd. All rights
 * reserved. This file contains intellectual property   
 * belonging to Real-Status Ltd and its licensors.
 */
package com.intergence.hgsrest.vmware.credentials;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


public class VmWareCredentialRepository {
	
	private static final String FIND_ALL_SQL = "SELECT * FROM VmWareCredential";
	private static final String FIND_BY_HOST_NAME_OR_IP_SQL = "SELECT * FROM     VmWareCredential WHERE hostNameOrIp = ?";
	private static final String INSERT_SQL = "INSERT INTO VmWareCredential (password, username, hostNameOrIp) VALUES (?,?,?)";
	private static final String CHANGE_PASSWORD_SQL = "UPDATE VmWareCredential SET password = ? WHERE username = ? AND hostNameOrIp = ?";
	private static final String DELETE_SQL = "DELETE FROM VmWareCredential WHERE key = ?";
	private static final String HAS_CREDENTIALS_SQL = "SELECT count(key) FROM VmWareCredential WHERE username = ? AND hostNameOrIp = ?";
	private static final String BUILD_DATABASE_SQL = "create table VmWareCredential (\n" +
			"  key SERIAL PRIMARY KEY,\n" +
			"  username VARCHAR(64),\n" +
			"  password VARCHAR(64),\n" +
			"  hostNameOrIp VARCHAR(128)\n" +
			");";

	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	public Collection<Credential> findAll() {
		return jdbcTemplate.query(FIND_ALL_SQL, getCredentialMapper());
	}
	
	public Collection<Credential> findByHostNameOrIpAddress(final String hostNameOrIp) {
		return jdbcTemplate.query(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						PreparedStatement statement = connection.prepareStatement(FIND_BY_HOST_NAME_OR_IP_SQL);
						statement.setString(1, hostNameOrIp);
						return statement;
					}
				},
				getCredentialMapper());
	}
	
	public boolean putCredential(final String username, final String password, final String hostNameOrIp) {
		final boolean firstTime = !hasCredential(username, hostNameOrIp);
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement statement = connection.prepareStatement(firstTime ? INSERT_SQL : CHANGE_PASSWORD_SQL);
				statement.setString(1, password);
				statement.setString(2, username);
				statement.setString(3, hostNameOrIp);
				return statement;
			}
		});

		return firstTime;
	}

	private void buildDatabase() {

		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement statement = connection.prepareStatement(BUILD_DATABASE_SQL);
				return statement;
			}
		});

	}

	public void deleteCredential(final int key) {
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement statement = connection.prepareStatement(DELETE_SQL);
				statement.setInt(1, key);
				return statement;
			}
		});
	}
	
	private boolean hasCredential(final String username, final String hostNameOrIp) {
		return jdbcTemplate.queryForInt(HAS_CREDENTIALS_SQL, username, hostNameOrIp) > 0;
	}
	
	private RowMapper<Credential> getCredentialMapper() {
		return new RowMapper<Credential>() {
			@Override
			public Credential mapRow(ResultSet resultSet, int rowNum) throws SQLException {
				return new Credential(
					resultSet.getInt("key"),
					resultSet.getString("username"),
					resultSet.getString("password"),
					resultSet.getString("hostNameOrIp"));
			}
		};
	}
}