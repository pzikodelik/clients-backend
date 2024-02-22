package com.ironbrain.clients.backend.controller;

import com.ironbrain.clients.backend.dto.ClientRequest;
import com.ironbrain.clients.backend.dto.ClientResponse;
import com.ironbrain.clients.backend.dto.ClientResponseBody;
import com.ironbrain.clients.backend.dto.ListClientResponse;
import com.ironbrain.clients.backend.exception.NotFoundException;
import com.ironbrain.clients.backend.model.Client;
import com.ironbrain.clients.backend.service.ClientService;
import com.ironbrain.clients.backend.validation.ClientRequestValidator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600)
@RestController
@RequestMapping(path = "/client")
@AllArgsConstructor
public class ClientController {

    private ClientService clientService;
    private ClientRequestValidator clientRequestValidator;

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody ClientRequest clientRequest) {
        try {

            clientRequestValidator.validatePostAndPutRequest(clientRequest);

            Client clientResult = clientService.save(
                    Client.builder()
                            .firstName(clientRequest.getFirstName())
                            .middleName(clientRequest.getMiddleName())
                            .lastName(clientRequest.getLastName())
                            .email(clientRequest.getEmail())
                            .username(clientRequest.getUsername())
                            .password(clientRequest.getPassword())
                            .build()
            );

            return new ResponseEntity<>(
                    ClientResponse.builder()
                            .body(
                                    ClientResponseBody.builder()
                                            .id(clientResult.getId())
                                            .firstName(clientResult.getFirstName())
                                            .middleName(clientResult.getMiddleName())
                                            .lastName(clientResult.getLastName())
                                            .email(clientResult.getEmail())
                                            .username(clientResult.getUsername())
                                            .password(clientRequest.getPassword())
                                            .isActive(clientResult.getIsActive())
                                            .build())
                            .message("Client saved successfully !!!")
                            .build(),
                    HttpStatus.OK);

        } catch (IllegalArgumentException | DataIntegrityViolationException ex) {
            return returnException(ex);
        }
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateById(@RequestBody ClientRequest clientRequest, @PathVariable Long id) {
        try {

            clientRequestValidator.validatePostAndPutRequest(clientRequest);

            Client clientResult = clientService.updateById(
                    Client.builder()
                            .firstName(clientRequest.getFirstName())
                            .middleName(clientRequest.getMiddleName())
                            .lastName(clientRequest.getLastName())
                            .email(clientRequest.getEmail())
                            .username(clientRequest.getUsername())
                            .password(clientRequest.getPassword())
                            .isActive(clientRequest.getIsActive())
                            .build(), id
            );

            return new ResponseEntity<>(
                    ClientResponse.builder()
                            .body(
                                    ClientResponseBody.builder()
                                            .id(clientResult.getId())
                                            .firstName(clientResult.getFirstName())
                                            .middleName(clientResult.getMiddleName())
                                            .lastName(clientResult.getLastName())
                                            .email(clientResult.getEmail())
                                            .username(clientResult.getUsername())
                                            .password(clientRequest.getPassword())
                                            .isActive(clientResult.getIsActive())
                                            .build())
                            .message("Client updated successfully !!!")
                            .build(),
                    HttpStatus.OK);

        } catch (NotFoundException | IllegalArgumentException | DataIntegrityViolationException ex) {
            return returnException(ex);
        }
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findById(@PathVariable Long id) {

        Optional<Client> clientResult = clientService.findById(id);

        return clientResult.map(
                        client -> new ResponseEntity<>(
                                ClientResponse.builder()
                                        .body(
                                                ClientResponseBody.builder()
                                                        .id(client.getId())
                                                        .firstName(client.getFirstName())
                                                        .middleName(client.getMiddleName())
                                                        .lastName(client.getLastName())
                                                        .email(client.getEmail())
                                                        .username(client.getUsername())
                                                        .password(client.getPassword())
                                                        .isActive(client.getIsActive())
                                                        .build())
                                        .message("Client founded successfully !!!")
                                        .build(),
                                HttpStatus.OK))
                .orElseGet(
                        () -> new ResponseEntity<>(
                                ClientResponse.builder()
                                        .message("The client with the Id " + id + " doesn't exist !!!")
                                        .build(),
                                HttpStatus.NOT_FOUND
                        )
                );
    }

    @PostMapping(path = "/findByUsernameAndPassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUsernameAndPassword(@RequestBody ClientRequest clientRequest) {
        try {
            clientRequestValidator.validateUsernameAndPasswordRequest(clientRequest);

            Optional<Client> clientResult = clientService.findByUsernameAndPassword(clientRequest.getUsername(), clientRequest.getPassword());

            return clientResult.map(
                            client -> new ResponseEntity<>(
                                    ClientResponse.builder()
                                            .body(
                                                    ClientResponseBody.builder()
                                                            .id(client.getId())
                                                            .firstName(client.getFirstName())
                                                            .middleName(client.getMiddleName())
                                                            .lastName(client.getLastName())
                                                            .email(client.getEmail())
                                                            .username(client.getUsername())
                                                            .password(client.getPassword())
                                                            .isActive(client.getIsActive())
                                                            .build())
                                            .message("Client founded successfully !!!")
                                            .build(),
                                    HttpStatus.OK))
                    .orElseGet(
                            () -> new ResponseEntity<>(
                                    ClientResponse.builder()
                                            .message("Username or Password is incorrect, try again !!!")
                                            .build(),
                                    HttpStatus.NOT_FOUND
                            )
                    );

        } catch (IllegalArgumentException ex) {
            return returnException(ex);
        }
    }

    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> activateAndDeactivateById(@PathVariable Long id) {
        try {

            Client clientResult = clientService.activateAndDeactivateClientById(id);

            String status = clientResult.getIsActive() ? "activated" : "deactivated";

            return new ResponseEntity<>(
                    ClientResponse.builder()
                            .body(
                                    ClientResponseBody.builder()
                                            .id(clientResult.getId())
                                            .firstName(clientResult.getFirstName())
                                            .middleName(clientResult.getMiddleName())
                                            .lastName(clientResult.getLastName())
                                            .email(clientResult.getEmail())
                                            .username(clientResult.getUsername())
                                            .password(clientResult.getPassword())
                                            .isActive(clientResult.getIsActive())
                                            .build())
                            .message("Client " + status + " successfully !!!")
                            .build(),
                    HttpStatus.OK);

        } catch (NotFoundException ex) {
            return returnException(ex);
        }
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {

        List<Client> clientsResult = clientService.findAll();

        if (clientsResult.isEmpty()) {
            return new ResponseEntity<>(
                    ClientResponse.builder()
                            .message("There aren't clients !!!")
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        } else {
            return new ResponseEntity<>(
                    ListClientResponse.builder()
                            .body( clientsResult.stream().map(
                                    client -> ClientResponseBody.builder()
                                            .id(client.getId())
                                            .firstName(client.getFirstName())
                                            .middleName(client.getMiddleName())
                                            .lastName(client.getLastName())
                                            .email(client.getEmail())
                                            .username(client.getUsername())
                                            .password(client.getPassword())
                                            .isActive(client.getIsActive())
                                            .build()
                            ).collect(Collectors.toList()))
                            .message("Clients founded successfully !!!")
                            .build(),
                    HttpStatus.OK);
        }
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        try {

            clientService.deleteById(id);

            return new ResponseEntity<>(
                    ClientResponse.builder()
                            .message("Client deleted successfully !!!")
                            .build(),
                    HttpStatus.OK);

        } catch (NotFoundException ex) {
            return returnException(ex);
        }
    }

    private ResponseEntity<?> returnException(RuntimeException ex) {
        String messageError = ex.getLocalizedMessage();

        if (ex instanceof DataIntegrityViolationException) {

            String value;

            if (ex.getLocalizedMessage().contains("EMAIL")) {
                value = "Email";
            } else {
                value = "Username";
            }
            messageError = String.format("Already exists this %1$s in the System !!!", value);
        } else if (ex instanceof NotFoundException) {
            return new ResponseEntity<>(
                    ClientResponse.builder()
                            .message(messageError)
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                ClientResponse.builder()
                        .message(messageError)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
