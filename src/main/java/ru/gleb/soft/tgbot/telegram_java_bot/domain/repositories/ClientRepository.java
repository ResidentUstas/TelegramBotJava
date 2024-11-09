package ru.gleb.soft.tgbot.telegram_java_bot.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.entities.Client;

import java.util.List;

@Repository
public interface ClientRepository  extends JpaRepository<Client, Integer> {
    List<Client> findByNameStartingWith(String name);
}
