package com.ironbrain.clients.backend.service.impl;

import com.ironbrain.clients.backend.exception.NotFoundException;
import com.ironbrain.clients.backend.model.Client;
import com.ironbrain.clients.backend.repository.ClientRepository;
import java.util.Date;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository repository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;

    @BeforeEach
    void setUp() {

        client = new Client(1L, "Yoni", "Herrera", "Lica", "ingyoniherrera@gmail.com", "Psycho", "P@ssw0rd", true, new Date(), new Date());
    }

    @Test
    void save() {

        Mockito.when(repository.save(client)).thenReturn(client);

        Assertions.assertNotNull(clientService.save(client));
    }

    @Test
    void updateById() {

        Mockito.lenient().when(repository.findById(1L)).thenReturn(Optional.of(client));

        client.setUsername("Metatron");
        Mockito.when(repository.save(client)).thenReturn(client);

        Client result = clientService.updateById(client, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Metatron",result.getUsername());
    }

    @Test
    void updateByIdFails() {

        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> clientService.updateById(client, 1L));
    }

    @Test
    void findById() {

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(client));

        Assertions.assertNotNull(clientService.findById(1L));
    }

    @Test
    void findByIdNotFound() {

        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,() -> clientService.findById(1L));
    }

    @Test
    void findByUsernameAndPassword() {
        Mockito.when(repository.findClientByUsernameAndPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.of(client));

        Assertions.assertNotNull(clientService.findByUsernameAndPassword("", ""));
    }

    @Test
    void findByUsernameAndPasswordNotFound() {
        Mockito.when(repository.findClientByUsernameAndPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> clientService.findByUsernameAndPassword("", ""));
    }

    @Test
    void findAll() {

        Mockito.when(repository.findAll()).thenReturn(List.of(client));

        List<Client> clients = clientService.findAll();

        Assertions.assertFalse(clients.isEmpty());
        Assertions.assertEquals(1, clients.size());
    }

    @Test
    void findAllNotFound() {

        Mockito.when(repository.findAll()).thenReturn(List.of());

        Assertions.assertThrows(NotFoundException.class, () -> clientService.findAll());
    }

    @Test
    void findAllPage() {

        Mockito.when(repository.findAll(PageRequest.of(1, 1))).thenReturn(new PageImpl<>(List.of(new Client())));

        Page<Client> clients = clientService.findAllPaged(1, 1);

        Assertions.assertFalse(clients.isEmpty());
        Assertions.assertEquals(1, clients.getContent().size());
    }

    @Test
    void findAllPageNotFound() {

        Mockito.when(repository.findAll(PageRequest.of(1, 1))).thenReturn(new PageImpl<>(List.of()));

        Assertions.assertThrows(NotFoundException.class, () -> clientService.findAllPaged(1, 1));
    }

    @Test
    void activateAndDeactivateClientById() {

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(client));

        Client client = new Client();
        client.setIsActive(false);

        Mockito.when(repository.save(Mockito.any())).thenReturn(client);

        Client result = clientService.activateAndDeactivateClientById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getIsActive());
    }

    @Test
    void activateAndDeactivateClientByIdIsActiveFalse() {

        Client client = new Client();
        client.setIsActive(false);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(client));

        Client client2 = new Client();
        client2.setIsActive(true);

        Mockito.when(repository.save(Mockito.any())).thenReturn(client2);

        Client result = clientService.activateAndDeactivateClientById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getIsActive());
    }

    @Test
    void activateAndDeactivateClientByIdFail() {

        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> clientService.activateAndDeactivateClientById(1L));
    }

    @Test
    void deleteById() {

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(new Client()));

        clientService.deleteById(1L);

        Mockito.verify(repository).findById(1L);
    }

    @Test
    void deleteByIdNotFound() {

        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> clientService.deleteById(1L));
    }
}