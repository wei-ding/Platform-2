package org.clinical3PO.common.security.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import org.clinical3PO.common.security.model.AccumuloRole;
import org.clinical3PO.common.security.model.Role;
import org.clinical3PO.common.security.model.User;

@Repository
public class UserDAO {

	private JdbcTemplate jdbcTemplate;

	public User loadUserByUsername(final String userName) {

		assert (jdbcTemplate != null);

		String sql = "select * from users where userName=?";
		User user = null;

		try {
			user = jdbcTemplate.queryForObject(sql, new Object[] { userName },
					new BeanPropertyRowMapper<User>(User.class));
		} catch (EmptyResultDataAccessException e) {
			throw new UsernameNotFoundException("User not found");
		}

		// Get the authorities

		String roleQuery = "select * from roles where userId=?";
		List<Role> roles = jdbcTemplate.query(roleQuery, new Object[] { user
				.getId() }, new BeanPropertyRowMapper<Role>(Role.class));

		user.setAuthorities(roles);

		// Get the accumulo roles

		String accumuloRoleQuery = "select * from accumuloroles where userId=?";
		List<AccumuloRole> accumuloRoles = jdbcTemplate.query(
				accumuloRoleQuery, new Object[] { user.getId() },
				new BeanPropertyRowMapper<AccumuloRole>(AccumuloRole.class));

		user.setAccumoloRoles(accumuloRoles);

		return user;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}
