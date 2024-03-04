package com.ironbrain.clients.backend.service.impl;

import com.ironbrain.clients.backend.exception.NotFoundException;
import com.ironbrain.clients.backend.model.Client;
import com.ironbrain.clients.backend.repository.ClientRepository;
import com.ironbrain.clients.backend.service.ClientService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;

    private static final String emptyListMsg = "The list of clients is empty";

    @Cacheable(value = "usernames")
    @Override
    public Client save(Client client) {
        log.info("The Client with Username = {} was saved successfully", client.getUsername());
        return clientRepository.save(client);
    }

    @CachePut(value = "usernames", key = "#id")
    @Override
    public Client updateById(Client client, Long id) {
        Optional<Client> clientResult = clientRepository.findById(id);
        if (clientResult.isPresent()) {
            clientResult.get().setEmail(client.getEmail());
            clientResult.get().setUsername(client.getUsername());
            clientResult.get().setPassword(client.getPassword());
            clientResult.get().setFirstName(client.getFirstName());
            clientResult.get().setMiddleName(client.getMiddleName());
            clientResult.get().setLastName(client.getLastName());
            clientResult.get().setUpdatedAt(new Date());
            clientResult.get().setIsActive(clientResult.get().getIsActive());

            log.info("The Client with Username = {} was updated successfully", client.getUsername());
            return clientRepository.save(clientResult.get());
        } else {
            String message = String.format("The Client with Username = %1s does not exist !!!", client.getUsername());
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Cacheable(value = "usernames", key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    @Override
    public Client findById(Long id) throws NotFoundException {
        Optional<Client> result = clientRepository.findById(id);
        if(result.isPresent()) {
            log.info("The Client with Id = {} was founded successfully", id);
            return result.get();
        }

        throw executeNotFoundException(id);
    }

    @Cacheable(value = "usernames", key = "#username", unless = "#result == null")
    @Transactional(readOnly = true)
    @Override
    public Client findByUsernameAndPassword(String username, String password) {
        Optional<Client> result = clientRepository.findClientByUsernameAndPassword(username, password);
        if (result.isPresent()) {
            log.info("The Client with Username = {} was founded successfully", username);
            return result.get();
        }
        String message = String.format("The Client with Username = %1s wasn't founded", username);
        log.info(message);
        throw new NotFoundException(message);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Client> findAllPaged(int page, int size) {
        Page<Client> clientList = clientRepository.findAll(PageRequest.of(page, size));
        if (!clientList.isEmpty()) {
            log.info("The Clients with Paged were consulted successfully");
            return clientList;
        }

        log.info(emptyListMsg);
        throw new NotFoundException(emptyListMsg);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Client> findAll() {
        List<Client> clientList = clientRepository.findAll();
        if (!clientList.isEmpty()) {
            log.info("The Clients were consulted successfully");
            return clientList;
        }

        log.info(emptyListMsg);
        throw new NotFoundException(emptyListMsg);
    }

    @CachePut(value = "usernames", key = "#id")
    @Override
    public Client activateAndDeactivateClientById(Long id) {
        Optional<Client> clientResult = clientRepository.findById(id);
        if(clientResult.isPresent()) {

            clientResult.get().setIsActive(!clientResult.get().getIsActive());

            log.info("The Client with Username = {} were activated/deactivated successfully", clientResult.get().getUsername());
            return clientRepository.save(clientResult.get());
        } else {
            throw executeNotFoundException(id);
        }
    }

    @CacheEvict(value = "usernames", key = "#id")
    @Override
    public void deleteById(Long id) {
        Optional<Client> clientResult = clientRepository.findById(id);
        if(clientResult.isPresent()) {
            log.info("The Client with the Username = {} was removed successfully", clientResult.get().getUsername());
            clientRepository.deleteById(id);
        } else {
            throw executeNotFoundException(id);
        }
    }

    private NotFoundException executeNotFoundException(Long id) throws NotFoundException {
        String message = String.format("The Client with the Id = %1d does not exist !!!", id);
        log.error(message);
        return new NotFoundException(message);
    }
}
