package org.clinical3PO.common.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.clinical3PO.common.security.dao.UserDAO;
import org.clinical3PO.common.security.model.User;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserDAO userDao;

	@Override
	public User loadUserByUsername(final String username) throws UsernameNotFoundException, DataAccessException {
		return userDao.loadUserByUsername(username);
	}

}
