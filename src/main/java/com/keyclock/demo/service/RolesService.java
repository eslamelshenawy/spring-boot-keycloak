package com.keyclock.demo.service;

import com.keyclock.demo.DTO.UserDTO;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class RolesService {

    @Autowired
    KeycloakUtils keycloakUtils;

    public List<RoleRepresentation> getRoles(String token, String realm) {
        String baseUrl = keycloakUtils.urlKeycloak + "/admin/realms/" + realm + "/roles";

        HttpHeaders headers = keycloakUtils.createHeaders(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<RoleRepresentation>> response = keycloakUtils.restTemplate.exchange(baseUrl, HttpMethod.GET, request, new ParameterizedTypeReference<List<RoleRepresentation>>() {
            });
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            System.out.println("KeyCloak getRolesResponse: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve roles", e);
        }
    }

    public RoleRepresentation getRolesByUser(String token, String realm, String username) {
        String baseUrl = keycloakUtils.urlKeycloak + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = keycloakUtils.createHeaders(token);
        UserRepresentation userRepresentation = keycloakUtils.getUserKeycloak(token, username, realm);
        String userId = userRepresentation.getId();
        List<String> realmRoles = userRepresentation.getRealmRoles();
        userRepresentation.getRealmRoles();

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<RoleRepresentation> response = keycloakUtils.restTemplate.exchange(baseUrl, HttpMethod.GET, request, new ParameterizedTypeReference<RoleRepresentation>() {});
            if (response.getStatusCode().is2xxSuccessful()) {
                RoleRepresentation roleRepresentation = response.getBody();
//                roleRepresentation.setRealmRoles(realmRoles);
                return roleRepresentation;
            } else {
                return null;
            }
        } catch (HttpClientErrorException e) {
            System.out.println("KeyCloak getRolesResponse: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve roles", e);
        }
    }

}
