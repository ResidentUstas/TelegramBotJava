package ru.gleb.soft.tgbot.telegram_java_bot.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.entities.Dish;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.entities.Order;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.repositories.DishRepository;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.repositories.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findOne(int id) {
        Optional<Order> book = orderRepository.findById(id);

        return book.orElse(null);
    }

    @Transactional
    public void save(Order dish) {
        orderRepository.save(dish);
    }

    @Transactional
    public void update(int id, Order order) {
        order.setOrder_id(id);
        orderRepository.save(order);
    }

    @Transactional
    public void delete(Order order) {
        orderRepository.delete(order);
    }

    @Transactional
    public void saveBookImageUrl(String url) {

    }

    @Transactional
    public void deleteById(int id) {
        orderRepository.deleteById(id);
    }
}
