package com.keyclock.demo.service;

import jdk.dynalink.beans.StaticClass;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.Map;

public class KeycloakUtils {


    private static String urlKeycloakToken;

    static String urlKeycloak;

    static final RestTemplate restTemplate = new RestTemplate();

    @Value("${custom.urlKeycloakToken}")
    public void setUrlKeycloakToken(String urlKeycloakToken) {
        KeycloakUtils.urlKeycloakToken = urlKeycloakToken;
    }

    @Value("${custom.urlKeycloak}")
    public void setUrlKeycloak(String urlKeycloak) {
        KeycloakUtils.urlKeycloak = urlKeycloak;
    }

    public static String getUrlKeycloakToken() {
        return urlKeycloakToken;
    }

    public static String getUrlKeycloak() {
        return urlKeycloak;
    }

    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }


    public String getToken() throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("client_id", "admin-cli");
        requestParams.add("username", "admin");
        requestParams.add("password", "admin");
        requestParams.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(urlKeycloakToken, HttpMethod.POST, request, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            Map<String, Object> result = response.getBody();
            return result.get("access_token").toString();
        } catch (Exception e) {
            System.out.println("KeyCloak getAccessTokenResponse: " + e.getMessage());
            throw new RuntimeException("Failed to obtain access token", e);
        }
    }


    HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }


    public UserRepresentation getUserKeycloak(String token, String userName, String realm) {
        final String baseUrl = urlKeycloak + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<UserRepresentation> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<UserRepresentation[]> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.GET, requestEntity, UserRepresentation[].class);
        UserRepresentation[] result = responseEntity.getBody();
        for (UserRepresentation user : result) {
            if (userName.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }
}
