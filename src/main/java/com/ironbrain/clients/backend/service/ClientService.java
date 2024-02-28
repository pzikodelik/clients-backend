package com.ironbrain.clients.backend.service;

import com.ironbrain.clients.backend.model.Client;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ClientService {

    Client save(Client client);
    Client updateById(Client client, Long id);

    Optional<Client> findById(Long id);
    Optional<Client> findByUsernameAndPassword(String username, String password);

    Client activateAndDeactivateClientById(Long id);

    List<Client> findAll();

    Page<Client> findAllPaged(int page, int size);

    void deleteById(Long id);
}
