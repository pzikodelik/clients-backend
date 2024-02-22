package com.ironbrain.clients.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClientResponseBody {

    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Boolean isActive;
    
}
