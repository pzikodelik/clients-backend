package com.ironbrain.clients.backend.controller;

import com.ironbrain.clients.backend.dto.ClientRequest;
import com.ironbrain.clients.backend.dto.ClientResponse;
import com.ironbrain.clients.backend.dto.ClientResponseBody;
import com.ironbrain.clients.backend.dto.ListClientResponse;
import com.ironbrain.clients.backend.exception.NotFoundException;
import com.ironbrain.clients.backend.model.Client;
import com.ironbrain.clients.backend.service.ClientService;
import com.ironbrain.clients.backend.validation.ClientRequestValidator;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import javax.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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

    @Timed(value = "client.save")
    @ApiOperation(value = "Save Client")
    @ApiResponses(value = {
            @ApiResponse(message = "The Client was saved successfully", code = 200),
            @ApiResponse(message = "The Client already exists on the DB", code = 404),
            @ApiResponse(message = "The Client information was validated and has errors", code = 406),
            @ApiResponse(message = "Something happened on the server, try again", code = 500)
    })
    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> save(@RequestBody ClientRequest clientRequest) {
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
                            .body(getClientResponseBody(clientResult))
                            .message("Client saved successfully !!!")
                            .build(),
                    HttpStatus.OK);

        } catch (RuntimeException ex) {
            return returnException(ex);
        }
    }

    @Timed(value = "client.updateById")
    @ApiOperation(value = "Updated Client by Id")
    @ApiResponses(value = {
            @ApiResponse(message = "The Client was updated successfully", code = 200),
            @ApiResponse(message = "The Client already exists on the DB", code = 404),
            @ApiResponse(message = "The Client information was validated and has errors", code = 406),
            @ApiResponse(message = "Something happened on the server, try again", code = 500)
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> updateById(@RequestBody ClientRequest clientRequest, @PathVariable Long id) {
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
                            .body(getClientResponseBody(clientResult))
                            .message("Client updated successfully !!!")
                            .build(),
                    HttpStatus.OK);

        } catch (RuntimeException ex) {
            return returnException(ex);
        }
    }

    @Timed(value = "client.findById")
    @ApiOperation(value = "Find Client by Id")
    @ApiResponses(value = {
            @ApiResponse(message = "The Client was founded successfully", code = 200),
            @ApiResponse(message = "The Client doesn't exists on the DB", code = 404),
            @ApiResponse(message = "Something happened on the server, try again", code = 500)
    })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {

        try {
            Client clientResult = clientService.findById(id);
            return  new ResponseEntity<>(
                    ClientResponse.builder()
                            .body(getClientResponseBody(clientResult))
                            .message("Client founded successfully !!!")
                            .build(),
                    HttpStatus.OK);
        } catch (NotFoundException ex) {
            return returnException(ex);
        }
    }

    @Timed(value = "client.findByUsernameAndPassword")
    @ApiOperation(value = "Find Client by Username and Password")
    @ApiResponses(value = {
            @ApiResponse(message = "The Client was founded successfully", code = 200),
            @ApiResponse(message = "The Client wasn't founded on the DB", code = 404),
            @ApiResponse(message = "The Client information was validated and has errors", code = 406),
            @ApiResponse(message = "Something happened on the server, try again", code = 500)
    })
    @PostMapping(path = "/findByUsernameAndPassword", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> findByUsernameAndPassword(@RequestBody ClientRequest clientRequest) {
        try {

            clientRequestValidator.validateUsernameAndPasswordRequest(clientRequest);

            Client clientResult = clientService.findByUsernameAndPassword(clientRequest.getUsername(), clientRequest.getPassword());

            return new ResponseEntity<>(
                    ClientResponse.builder()
                            .body(getClientResponseBody(clientResult))
                            .message("Client founded successfully !!!")
                            .build(),
                    HttpStatus.OK);
            } catch (RuntimeException ex) {
            return returnException(ex);
        }
    }

    @Timed(value = "client.activateAndDeactivateById")
    @ApiOperation(value = "Activate/Deactivate Client")
    @ApiResponses(value = {
            @ApiResponse(message = "The Client was activate/deactivate successfully", code = 200),
            @ApiResponse(message = "The Client wasn't found on the DB", code = 404),
            @ApiResponse(message = "Something happened on the server, try again", code = 500)
    })
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> activateAndDeactivateById(@PathVariable Long id) {
        try {

            Client clientResult = clientService.activateAndDeactivateClientById(id);

            String status = clientResult.getIsActive() ? "activated" : "deactivated";

            return new ResponseEntity<>(
                    ClientResponse.builder()
                            .body(getClientResponseBody(clientResult))
                            .message("Client " + status + " successfully !!!")
                            .build(),
                    HttpStatus.OK);

        } catch (RuntimeException ex) {
            return returnException(ex);
        }
    }

    @Timed(value = "client.findAll")
    @ApiOperation(value = "Find All Clients")
    @ApiResponses(value = {
            @ApiResponse(message = "The Clients were founded successfully", code = 200),
            @ApiResponse(message = "The Clients were empty on the DB", code = 404)
    })
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ListClientResponse> findAll() {

        try {
            List<Client> clientsResult = clientService.findAll();

            return new ResponseEntity<>(
                    ListClientResponse.builder()
                            .body( clientsResult.stream().map(
                                    this::getClientResponseBody
                            ).collect(Collectors.toList()))
                            .message("Clients founded successfully !!!")
                            .build(),
                    HttpStatus.OK);
        } catch (RuntimeException ex) {
            return returnListException(ex);
        }
    }

    @Timed(value = "client.findAll.paged")
    @ApiOperation(value = "Find All Client with Paged")
    @ApiResponses(value = {
            @ApiResponse(message = "The Clients were founded successfully", code = 200),
            @ApiResponse(message = "The Clients were empty on the DB", code = 404)
    })
    @GetMapping(value = "/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ListClientResponse> findAllPaged(@PathParam(value = "page") int page, @PathParam(value = "size") int size) {

        try {
            Page<Client> clientsResult = clientService.findAllPaged(page, size);

            return new ResponseEntity<>(
                    ListClientResponse.builder()
                            .body( clientsResult.stream().map(
                                    this::getClientResponseBody
                            ).collect(Collectors.toList()))
                            .message("Clients founded successfully !!!")
                            .build(),
                    HttpStatus.OK);
        } catch (RuntimeException ex) {
            return returnListException(ex);
        }
    }

    @Timed(value = "client.deleteById")
    @ApiOperation(value = "Delete Client by Id")
    @ApiResponses(value = {
            @ApiResponse(message = "The Client was deleted successfully", code = 200),
            @ApiResponse(message = "The Client doesn't exists on the DB", code = 404),
            @ApiResponse(message = "Something happened on the server, try again", code = 500)
    })
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientResponse> deleteById(@PathVariable Long id) {
        try {

            clientService.deleteById(id);

            return new ResponseEntity<>(
                    ClientResponse.builder()
                            .message("Client deleted successfully !!!")
                            .build(),
                    HttpStatus.OK);

        } catch (RuntimeException ex) {
            return returnException(ex);
        }
    }

    private ResponseEntity<ClientResponse> returnException(RuntimeException ex) {
        String messageError = ex.getLocalizedMessage();

        if (ex instanceof DataIntegrityViolationException) {

            String value;

            if (ex.getLocalizedMessage().contains("EMAIL")) {
                value = "Email";
            } else {
                value = "Username";
            }
            messageError = String.format("Already exists this %1s in the System !!!", value);

            return getResponseEntity(messageError, HttpStatus.BAD_REQUEST);
        } else if (ex instanceof IllegalArgumentException) {
            return getResponseEntity(messageError, HttpStatus.NOT_ACCEPTABLE);
        } else if (ex instanceof NotFoundException) {
            return getResponseEntity(messageError, HttpStatus.NOT_FOUND);
        }

        return getResponseEntity(messageError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ListClientResponse> returnListException(RuntimeException ex) {
        String messageError = ex.getLocalizedMessage();

        if (ex instanceof NotFoundException) {
            return getListResponseEntity(messageError, HttpStatus.NOT_FOUND);
        }

        return getListResponseEntity(messageError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ClientResponse> getResponseEntity(String messageError, HttpStatus httpStatus) {
        return new ResponseEntity<>(
                ClientResponse.builder()
                        .message(messageError)
                        .build(),
                httpStatus);
    }

    private ResponseEntity<ListClientResponse> getListResponseEntity(String messageError, HttpStatus httpStatus) {
        return new ResponseEntity<>(
                ListClientResponse.builder()
                        .message(messageError)
                        .build(),
                httpStatus);
    }

    private ClientResponseBody getClientResponseBody(Client  client) {
        return ClientResponseBody.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .middleName(client.getMiddleName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .username(client.getUsername())
                .password(client.getPassword())
                .isActive(client.getIsActive())
                .build();
    }
}
