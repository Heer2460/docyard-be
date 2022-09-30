package com.infotech.dms.autentication.service.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.infotech.dms.autentication.service.entity.User;
import com.infotech.dms.autentication.service.entity.repository.UserRepository;
import com.infotech.dms.autentication.service.util.AppUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

//    @Autowired
//    private RestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {

        List<String> perm = getPrivileges(user);
        return getGrantedAuthorities(perm);
    }

    private List<String> getPrivileges(User user) {
        List<String> privileges = new ArrayList<>();
       /* List<Menu> roleMenus = menuRepository.findUserMenuByUserId(user.getId());

        for (Menu menu : roleMenus) {
            privileges.add(menu.getTitle());
        }*/
        return new ArrayList<>(privileges);
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User  user =
//                restTemplate.getForObject("http://UM-SERVICE/user/username/" + username
//                        ,User.class);

        User user = userRepository.findByUsername(username);
        if (!AppUtility.isEmpty(user)) {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    getAuthorities(user));
        } else {
            throw new UsernameNotFoundException("User name " + username + " not found");
        }
    }
}
