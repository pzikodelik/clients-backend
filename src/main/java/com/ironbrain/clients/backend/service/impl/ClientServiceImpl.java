package com.ironbrain.clients.backend.service.impl;

import com.ironbrain.clients.backend.exception.NotFoundException;
import com.ironbrain.clients.backend.model.Client;
import com.ironbrain.clients.backend.repository.ClientRepository;
import com.ironbrain.clients.backend.service.ClientService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private ClientRepository clientRepository;

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

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

            return clientRepository.save(clientResult.get());
        } else {
            throw new NotFoundException("The client with the Id " + id + " does not exist !!!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Client> findByUsernameAndPassword(String username, String password) {
        return clientRepository.findClientByUsernameAndPassword(username, password);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client activateAndDeactivateClientById(Long id) {
        Optional<Client> clientResult = clientRepository.findById(id);
        if(clientResult.isPresent()) {

            clientResult.get().setIsActive(!clientResult.get().getIsActive());

            return clientRepository.save(clientResult.get());
        } else {
            throw new NotFoundException("The client with the Id " + id + " does not exist !!!");
        }
    }

    @Override
    public void deleteById(Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
        } else {
            throw new NotFoundException("The client with the Id " + id + " does not exist !!!");
        }
    }
}
