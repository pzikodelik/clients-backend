package com.ironbrain.clients.backend.validation;

import com.ironbrain.clients.backend.dto.ClientRequest;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ClientRequestValidator {

    public void validatePostAndPutRequest(ClientRequest clientRequest) {
        Assert.hasText(clientRequest.getFirstName(), "First Name can't be null or empty");
        Assert.hasText(clientRequest.getMiddleName(), "Middle Name can't be null or empty");
        Assert.hasText(clientRequest.getLastName(), "Last Name can't be null or empty");
        Assert.hasText(clientRequest.getEmail(), "Email can't be null or empty");
        Assert.isTrue(EmailValidator.getInstance().isValid(clientRequest.getEmail()), "Email doesn't have the correct structure");
        Assert.hasText(clientRequest.getUsername(), "Username can't be null or empty");
        Assert.hasText(clientRequest.getPassword(), "Password can't be null or empty");
        Assert.isTrue(clientRequest.getPassword().length() > 5, "Password must have at least 6 characters");
    }

    public void validateUsernameAndPasswordRequest(ClientRequest clientRequest) {
        Assert.hasText(clientRequest.getUsername(), "Username can't be null or empty");
        Assert.hasText(clientRequest.getPassword(), "Password can't be null or empty");
        Assert.isTrue(clientRequest.getPassword().length() > 5, "Password must have at least 6 characters");
    }
}
