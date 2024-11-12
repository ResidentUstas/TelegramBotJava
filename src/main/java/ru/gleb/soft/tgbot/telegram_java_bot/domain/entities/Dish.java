package ru.gleb.soft.tgbot.telegram_java_bot.domain.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "dishes")
public class Dish {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "struct")
    private String struct;

    @Column(name = "cookTime")
    private String cookTime;

    @ManyToMany(mappedBy = "order")
    private List<Client> clients;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStruct() {
        return struct;
    }

    public void setStruct(String struct) {
        this.struct = struct;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }
}
