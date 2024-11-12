package ru.gleb.soft.tgbot.telegram_java_bot.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.entities.Dish;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.repositories.DishRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DishService {
    private final DishRepository dishRepository;

    @Autowired
    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public List<Dish> findByName(String name) {
        return dishRepository.findByNameStartingWith(name);
    }

    public Dish findOne(int id) {
        Optional<Dish> book = dishRepository.findById(id);

        return book.orElse(null);
    }

    @Transactional
    public void save(Dish dish) {
        dishRepository.save(dish);
    }

    @Transactional
    public void update(int id, Dish dish) {
        dish.setId(id);
        dishRepository.save(dish);
    }

    @Transactional
    public void delete(Dish dish) {
        dishRepository.delete(dish);
    }

    @Transactional
    public void saveBookImageUrl(String url) {

    }

    @Transactional
    public void deleteById(int id) {
        dishRepository.deleteById(id);
    }
}
