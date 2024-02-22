package com.ironbrain.clients.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClientResponse {

    private ClientResponseBody body;

    private String message;

}

