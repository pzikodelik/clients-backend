package com.ironbrain.clients.backend.controller;

import com.ironbrain.clients.backend.dto.ClientRequest;
import com.ironbrain.clients.backend.exception.NotFoundException;
import com.ironbrain.clients.backend.model.Client;
import com.ironbrain.clients.backend.service.ClientService;
import com.ironbrain.clients.backend.validation.ClientRequestValidator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService service;

    @Mock
    private ClientRequestValidator validator;

    @InjectMocks
    private ClientController controller;

    private ClientRequest clientRequest;

    @BeforeEach
    void setUp() {
        clientRequest = ClientRequest.builder()
                .firstName("Yoni")
                .middleName("Herrera")
                .lastName("Lica")
                .email("ingyoniherrera@gmail.com")
                .username("User")
                .password("P@ssw0rd")
                .isActive(true)
                .build();
    }

    @Test
    void save() {

        Mockito.when(service.save(Mockito.any())).thenReturn(new Client());
        Mockito.doNothing().when(validator).validatePostAndPutRequest(Mockito.any());

        ResponseEntity<?> result = controller.save(clientRequest);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void saveValidationFail() {

        Mockito.doThrow(IllegalArgumentException.class).when(validator).validatePostAndPutRequest(Mockito.any());

        ResponseEntity<?> result = controller.save(clientRequest);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void updateById() {

        Mockito.when(service.updateById(Mockito.any(), Mockito.anyLong())).thenReturn(new Client());

        Mockito.doNothing().when(validator).validatePostAndPutRequest(Mockito.any());

        ResponseEntity<?> result = controller.updateById(clientRequest, 1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void updateByIdValidationFail() {

        Mockito.doThrow(IllegalArgumentException.class).when(validator).validatePostAndPutRequest(Mockito.any());

        ResponseEntity<?> result = controller.updateById(clientRequest, 1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void updateByIdNotFoundException() {

        Mockito.when(service.updateById(Mockito.any(), Mockito.anyLong())).thenThrow(NotFoundException.class);

        Mockito.doNothing().when(validator).validatePostAndPutRequest(Mockito.any());

        ResponseEntity<?> result = controller.updateById(clientRequest, 1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void updateByIdDataIntegrityViolationExceptionByEmail() {

        Mockito.when(service.updateById(Mockito.any(), Mockito.anyLong())).thenThrow(new DataIntegrityViolationException("EMAIL"));

        Mockito.doNothing().when(validator).validatePostAndPutRequest(Mockito.any());

        ResponseEntity<?> result = controller.updateById(clientRequest, 1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void updateByIdDataIntegrityViolationExceptionByUsername() {

        Mockito.when(service.updateById(Mockito.any(), Mockito.anyLong())).thenThrow(new DataIntegrityViolationException("USERNAME"));

        Mockito.doNothing().when(validator).validatePostAndPutRequest(Mockito.any());

        ResponseEntity<?> result = controller.updateById(clientRequest, 1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void findById() {

        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(Optional.of(new Client()));

        ResponseEntity<?> result = controller.findById(1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void findByIdNotFound() {

        Mockito.when(service.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        ResponseEntity<?> result = controller.findById(1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void findByUsernameAndPassword() {

        Mockito.when(service.findByUsernameAndPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.of(new Client()));

        Mockito.doNothing().when(validator).validateUsernameAndPasswordRequest(Mockito.any());

        ResponseEntity<?> result = controller.findByUsernameAndPassword(clientRequest);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void findByUsernameAndPasswordNotFound() {

        Mockito.when(service.findByUsernameAndPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.empty());

        Mockito.doNothing().when(validator).validateUsernameAndPasswordRequest(Mockito.any());

        ResponseEntity<?> result = controller.findByUsernameAndPassword(clientRequest);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void findByUsernameAndPasswordValidationFail() {

        Mockito.doThrow(IllegalArgumentException.class).when(validator).validateUsernameAndPasswordRequest(Mockito.any());

        ResponseEntity<?> result = controller.findByUsernameAndPassword(clientRequest);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void activateAndDeactivateByIdActiveTrue() {

        Client client = new Client();
        client.setIsActive(true);

        Mockito.when(service.activateAndDeactivateClientById(Mockito.anyLong())).thenReturn(client);

        ResponseEntity<?> result = controller.activateAndDeactivateById(1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void activateAndDeactivateByIdActiveFalse() {

        Client client = new Client();
        client.setIsActive(false);

        Mockito.when(service.activateAndDeactivateClientById(Mockito.anyLong())).thenReturn(client);

        ResponseEntity<?> result = controller.activateAndDeactivateById(1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void activateAndDeactivateByIdNotFound() {

        Mockito.when(service.activateAndDeactivateClientById(Mockito.anyLong())).thenThrow(NotFoundException.class);

        ResponseEntity<?> result = controller.activateAndDeactivateById(1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void findAll() {

        Mockito.when(service.findAll()).thenReturn(List.of(new Client()));

        ResponseEntity<?> result = controller.findAll();

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void findAllEmpty() {

        Mockito.when(service.findAll()).thenReturn(List.of());

        ResponseEntity<?> result = controller.findAll();

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }

    @Test
    void deleteById() {

        Mockito.doNothing().when(service).deleteById(Mockito.anyLong());

        ResponseEntity<?> result = controller.deleteById(1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void deleteByIdNotFound() {

        Mockito.doThrow(NotFoundException.class).when(service).deleteById(Mockito.anyLong());

        ResponseEntity<?> result = controller.deleteById(1L);

        Assertions.assertTrue(result.hasBody());
        Assertions.assertTrue(result.getStatusCode().is4xxClientError());
    }
}