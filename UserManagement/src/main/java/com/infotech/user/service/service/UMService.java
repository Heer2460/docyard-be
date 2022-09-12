package com.infotech.user.service.service;

import com.infotech.user.service.dto.UserDTO;
import com.infotech.user.service.entity.Tenant;
import com.infotech.user.service.entity.User;
import com.infotech.user.service.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class UMService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;

    public List<User> getAllUsers() {
        log.info("getAllUsers method called...");

        return userRepository.findAll();
    }

    public UserDTO getUserByID(Long id) {
        log.info("getUserByID method called...");

        UserDTO userDTO = new UserDTO();

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userDTO.setUser(optionalUser.get());
            Tenant tenant = restTemplate.getForObject("http://TENANT-SERVICE/tenants/" + optionalUser.get().getTenantId(), Tenant.class);
            userDTO.setTenant(tenant);
        }
        return userDTO;
    }

    public User createAndUpdateUser(User user) {
        log.info("createAndUpdateUser method called...");

        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        log.info("deleteUserById method called...");

        userRepository.deleteById(id);
    }


}
