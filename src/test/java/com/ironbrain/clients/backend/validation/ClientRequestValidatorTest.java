package com.ironbrain.clients.backend.validation;

import com.ironbrain.clients.backend.dto.ClientRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClientRequestValidatorTest {

    private ClientRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ClientRequestValidator();
    }

    @Test
    @DisplayName(value = "Validate Post and Put ClientRequest")
    void validatePostAndPutRequest() {
        ClientRequest clientRequest = ClientRequest.builder()
                .firstName("Yoni")
                .middleName("Herrera")
                .lastName("Lica")
                .email("ingyoniherrera@gmail.com")
                .username("Psycho")
                .password("P@ssw0rd")
                .build();

        validator.validatePostAndPutRequest(clientRequest);
    }

    @Test
    @DisplayName(value = "Validate Post and Put ClientRequest, Password fails")
    void validatePostAndPutRequestPasswordFails() {
        ClientRequest clientRequest = ClientRequest.builder()
                .firstName("Yoni")
                .middleName("Herrera")
                .lastName("Lica")
                .email("ingyoniherrera@gmail.com")
                .username("Psycho")
                .password("P@ss")
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validatePostAndPutRequest(clientRequest));
    }

    @Test
    @DisplayName(value = "Validate Username and Password ClientRequest")
    void validateUsernameAndPasswordRequest() {
        ClientRequest clientRequest = ClientRequest.builder()
                .username("Psycho")
                .password("P@ssw0rd")
                .build();

        validator.validateUsernameAndPasswordRequest(clientRequest);
    }

    @Test
    @DisplayName(value = "Validate Username and Password ClientRequest, Password fails")
    void validateUsernameAndPasswordRequestFail() {
        ClientRequest clientRequest = ClientRequest.builder()
                .username("Psycho")
                .password("P@ss")
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validateUsernameAndPasswordRequest(clientRequest));
    }
}