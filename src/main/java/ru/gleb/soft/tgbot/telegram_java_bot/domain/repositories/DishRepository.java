package ru.gleb.soft.tgbot.telegram_java_bot.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.entities.Dish;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {
    List<Dish> findByNameStartingWith(String name);
}
