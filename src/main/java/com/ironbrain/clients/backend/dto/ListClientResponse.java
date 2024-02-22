package com.ironbrain.clients.backend.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ListClientResponse {

    private List<ClientResponseBody> body;

    private String message;

}
