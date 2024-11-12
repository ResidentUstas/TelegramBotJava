package ru.gleb.soft.tgbot.telegram_java_bot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.entities.Client;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.services.ClientService;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.services.DishService;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBotFrame extends TelegramLongPollingBot {
    @Autowired
    DefaultBotOptions defaultBotOptions;
    private final ClientService clientService;
    private final DishService dishService;

    public TelegramBotFrame(DefaultBotOptions defaultBotOptions, @Value("${bot.token}") String botToken, ClientService clientService, DishService dishService) {
        super(defaultBotOptions, botToken);
        this.clientService = clientService;
        this.dishService = dishService;
        List<BotCommand> botCommandList = new ArrayList<>();
        botCommandList.add(new BotCommand("/getMenu", "Посмотреть меню на сегодня"));
        try {
             this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        }catch (TelegramApiException e){
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    Client client = new Client();
                    client.setLogin(update.getMessage().getChat().getFirstName());
                    client.setName(update.getMessage().getChat().getLastName());
                    client.setChatId(chatId);
                    clientService.save(client);
                    break;
                case "/getmenu":
                    String answer = "Привет, " + name + ". Актуальное меню: \r\n";
                    sendMessage(chatId, answer);
                    getMenuReceived(chatId);
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            var userId = update.getCallbackQuery().getFrom().getId();
            var userName = update.getCallbackQuery().getFrom().getFirstName();
            var receivedMessage = update.getCallbackQuery().getData();

            botAnswerUtils(receivedMessage, chatId, userName);
        }
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
        switch (receivedMessage) {
            case "/makeOrder":
                String answer = "Делаем заказ";
                sendMessage(chatId, answer);
                break;
            default:
                break;
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name + ". Булушка мастер гастрономии, лучший булочный шеф. Готовы сделать закаааз?";
        sendMessage(chatId, answer);
    }

    private void getMenuReceived(Long chatId) {
        String answer = "";
        var menu = dishService.findAll();
        for (var dish : menu){
            answer += "Блюдо: " + dish.getName() + " \r\n"
                    + "Состав: " + dish.getStruct() + " \r\n"
                    + "Время приготовления: " + dish.getCookTime();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(answer);
            sendMessage.setReplyMarkup(Buttons.inlineMarkup());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {

            }
            answer = "";
        }
    }
    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return "bot_eat_maker";
    }
}
