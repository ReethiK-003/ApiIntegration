package com.apiintegration.core.jwt;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.apiintegration.core.exception.DuplicateEntryException;
import com.apiintegration.core.model.User;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 8862482307163207789L;

	private static final String ROLE_PREFIX = "ROLE_";

	private final List<GrantedAuthority> authorityList;
	private User user;

	public UserDetailsImpl(User user) {
		this.user = user;

		String role = user.getUserRole();

		authorityList = new LinkedList<GrantedAuthority>();
//		authorityList.add(new SimpleGrantedAuthority(UserRole.USER));
		if (role != null) {
			authorityList.add(new SimpleGrantedAuthority(ROLE_PREFIX + role));
		} else {
			throw new DuplicateEntryException("Unauthorized Request detected without Role ." + user.getUserEmail());
		}
	}

	public boolean isEmailVerified() {
		return user.getVerifiedEmail();
	}

	public boolean isSessionValid(String tokenSession) {
		return tokenSession.equals(user.getSession());
	}

	public boolean isAccountPresent() {
		return user.getAccount() != null;
	}

	public User getUser() {
		return user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorityList;
	}

	@Override
	public String getPassword() {
		return user.getUserPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
