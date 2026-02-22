package com.sk.workitem.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterRoles;
import com.sk.workitem.app.repository.MasterLoginRepository;
import com.sk.workitem.app.repository.MasterRolesRepository;

@Component
public class UserDetailsImpl implements UserDetailsService {

	private MasterLoginRepository mstrLoginRepo;
	private MasterRolesRepository roleRepo;
	
	@Autowired
	public UserDetailsImpl(MasterLoginRepository mstrLoginRepo, MasterRolesRepository roleRepo) {
		this.mstrLoginRepo = mstrLoginRepo;
		this.roleRepo = roleRepo;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MasterLogin masterLogin = mstrLoginRepo.findById(username).orElseThrow(
				() -> new UsernameNotFoundException("User does not exist")
				);
		MasterRoles roles = roleRepo.findById(masterLogin.getRoleId()).get();
		return new User(username, masterLogin.getPasswordHash(), getGrantedAuthority(roles.getRoleName()));
	}
	
	private List<GrantedAuthority> getGrantedAuthority(String role) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+role));
		return grantedAuthorities;
	}
}
