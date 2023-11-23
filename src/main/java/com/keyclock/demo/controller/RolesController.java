package com.keyclock.demo.controller;

import com.keyclock.demo.DTO.UserDTO;
import com.keyclock.demo.service.KeyCloakService;
import com.keyclock.demo.service.KeycloakUtils;
import com.keyclock.demo.service.RolesService;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@RestController

@RequestMapping(path = "/roles")
public class RolesController {

    @Autowired
    RolesService rolesService;
    @Autowired
    KeycloakUtils service;

    @GetMapping("/")
    public List<RoleRepresentation> getRoles(@RequestParam String realm) throws URISyntaxException {
        String token = service.getToken();
        List<RoleRepresentation> roles = rolesService.getRoles(token, realm);
        return roles;
    }

    @GetMapping("/username")
    public RoleRepresentation getRolesByUser(@RequestParam String username,@RequestParam String realm) throws URISyntaxException {
        String token = service.getToken();
        RoleRepresentation roles = rolesService.getRolesByUser(token, realm,username);
        return roles;
    }

//    @PostMapping("/")
//    public String createRole(@RequestParam String realm, @RequestBody UserDTO userDTO) throws Exception {
//        String token = service.getToken();
//        keyCloakService.createUser(token, userDTO, realm);
//        return "User Added Successfully.";
//    }

}
