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
import java.util.*;

@Service
public class KeyCloakService {

    @Autowired
    KeycloakUtils keycloakUtils;


    public List<UserRepresentation> getUsers(String token, String realm) throws URISyntaxException {
        String baseUrl = keycloakUtils.urlKeycloak + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = keycloakUtils.createHeaders(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<UserRepresentation>> response = keycloakUtils.restTemplate.exchange(baseUrl, HttpMethod.GET, request, new ParameterizedTypeReference<List<UserRepresentation>>() {
            });
            return response.getStatusCode().is2xxSuccessful() ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            System.out.println("KeyCloak getUsersResponse: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }

    public String createUser(String token, UserDTO userDTO, String realm) throws Exception {
        String result = "";
        final String baseUrl = keycloakUtils.urlKeycloak + "/admin/realms/" + realm + "/users";

        try {
            HttpHeaders headers = keycloakUtils.createHeaders(token);

            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setEmail(userDTO.getEmail());
            userRepresentation.setUsername(userDTO.getUsername());
            userRepresentation.setLastName(userDTO.getLastName());
            userRepresentation.setFirstName(userDTO.getFirstName());
            userRepresentation.setEnabled(true);

            HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(userRepresentation, headers);

            try {
                ResponseEntity<UserRepresentation> responseEntity = keycloakUtils.restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, UserRepresentation.class);
                result = responseEntity.getBody().toString();
            } catch (HttpClientErrorException e) {
                System.out.println(e);
                result = e.getMessage();
            }

            return result;
        } catch (Exception e) {
            result = e.getMessage();
        }
        return "";
    }

    public String updateUser(String userName, String token, UserDTO userDTO, String realm) throws URISyntaxException {

        String result = "";
        final String userUrl = keycloakUtils.urlKeycloak + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        UserRepresentation userRepresentation = getUserKeycloak(token, userName, realm);
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setEnabled(true);

        final String baseUrl = userUrl + "/" + userRepresentation.getId();

        HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(userRepresentation, headers);
        try {
            ResponseEntity<UserRepresentation> responseEntity = keycloakUtils.restTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, UserRepresentation.class);
            if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
                result = "User updated successfully";
            } else {
                result = "Failed to update user";
            }
        } catch (Exception e) {
            System.out.println("KeyCloak updateUserResponse: " + e.getMessage());
            throw new RuntimeException("Failed to update user", e);
        }

        return result;

    }

    public UserRepresentation getUserKeycloak(String token, String userName, String realm) {
        final String baseUrl = keycloakUtils.urlKeycloak + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<UserRepresentation[]> responseEntity = keycloakUtils.restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, UserRepresentation[].class);
        UserRepresentation[] result = responseEntity.getBody();
        for (UserRepresentation user : result) {
            if (userName.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public void deleteUser(String userName, String token, String realm) {
        final String userUrl = keycloakUtils.urlKeycloak + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        UserRepresentation userRepresentation = getUserKeycloak(token, userName, realm);
        final String baseUrl = userUrl + "/" + userRepresentation.getId();
        HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(userRepresentation, headers);

        try {
            ResponseEntity<Void> responseEntity = keycloakUtils.restTemplate.exchange(baseUrl, HttpMethod.DELETE, requestEntity, Void.class);
            if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
                System.out.println("User deleted successfully");
            } else {
                System.out.println("Failed to delete user");
            }
        } catch (Exception e) {
            System.out.println("KeyCloak deleteUserResponse: " + e.getMessage());
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}
