package com.keyclock.demo.controller;

import com.keyclock.demo.DTO.UserDTO;
import com.keyclock.demo.service.KeyCloakService;
import com.keyclock.demo.service.KeycloakUtils;
import com.keyclock.demo.service.RolesService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController

@RequestMapping(path = "/user")
public class KeyCloakUserController {

    @Autowired
    KeyCloakService keyCloakService;
    @Autowired
    KeycloakUtils service;


    @GetMapping("/")
    public List<UserRepresentation> getUsers(@RequestParam String realm) throws URISyntaxException {
        String token = service.getToken();
        List<UserRepresentation> users = keyCloakService.getUsers(token, realm);
        return users;
    }

    @PostMapping("/")
    public String addUser(@RequestParam String realm, @RequestBody UserDTO userDTO) throws Exception {
        String token = service.getToken();
        keyCloakService.createUser(token, userDTO, realm);
        return "User Added Successfully.";
    }

    @PutMapping(path = "/update/{userName}")
    public String updateUser(@PathVariable("userName") String userName, @RequestBody UserDTO userDTO, @RequestParam String realm) throws URISyntaxException {
        String token = service.getToken();
        keyCloakService.updateUser(userName, token, userDTO, realm);
        return "User Details Updated Successfully.";
    }

//    @GetMapping(path = "/{userName}")
//    public List<UserRepresentation> getUser(@PathVariable("userName") String userName){
//        List<UserRepresentation> user = service.getUser(userName);
//        return user;
//    }

    @DeleteMapping(path = "/delete/{userName}")
    public String deleteUser(@PathVariable("userName") String userName, @RequestParam String realm) throws URISyntaxException {
        String token = service.getToken();
        keyCloakService.deleteUser(userName, token, realm);
        return "User Deleted Successfully.";
    }

//    @GetMapping(path = "/verification-link/{userId}")
//    public String sendVerificationLink(@PathVariable("userId") String userId){
//        service.sendVerificationLink(userId);
//        return "Verification Link Send to Registered E-mail Id.";
//    }
//
//    @GetMapping(path = "/reset-password/{userId}")
//    public String sendResetPassword(@PathVariable("userId") String userId){
//        service.sendResetPassword(userId);
//        return "Reset Password Link Send Successfully to Registered E-mail Id.";
//    }
}
