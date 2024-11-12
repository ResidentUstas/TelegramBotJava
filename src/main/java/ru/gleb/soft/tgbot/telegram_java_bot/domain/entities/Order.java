package ru.gleb.soft.tgbot.telegram_java_bot.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name="order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int order_id;

    @Column(name="client_id")
    private int client_id;

    @Column(name="dish_id")
    private int dish_id;

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getDish_id() {
        return dish_id;
    }

    public void setDish_id(int dish_id) {
        this.dish_id = dish_id;
    }
}
