package ru.gleb.soft.tgbot.telegram_java_bot.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.entities.Client;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.repositories.ClientRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }
    public List<Client> findByName(String name) {
        return clientRepository.findByNameStartingWith(name);
    }

    public Client findOne(int id) {
        Optional<Client> book = clientRepository.findById(id);

        return book.orElse(null);
    }

    @Transactional
    public void save(Client client) {
        clientRepository.save(client);
    }

    @Transactional
    public void update(int id, Client client) {
        client.setId(id);
        clientRepository.save(client);
    }

    @Transactional
    public void delete(Client client) {
        clientRepository.delete(client);
    }

    @Transactional
    public void saveBookImageUrl(String url){

    }
    @Transactional
    public void deleteById(int id) {
        clientRepository.deleteById(id);
    }
}
