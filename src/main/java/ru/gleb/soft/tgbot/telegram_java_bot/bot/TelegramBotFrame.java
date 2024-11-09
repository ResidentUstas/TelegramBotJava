package ru.gleb.soft.tgbot.telegram_java_bot.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.entities.Client;
import ru.gleb.soft.tgbot.telegram_java_bot.domain.services.ClientService;

import java.util.List;

@Component
public class TelegramBotFrame extends TelegramLongPollingBot {
    @Autowired
    DefaultBotOptions defaultBotOptions;
    private final ClientService clientService;
    public TelegramBotFrame(DefaultBotOptions defaultBotOptions, @Value("${bot.token}") String botToken, ClientService clientService) {
        super(defaultBotOptions, botToken);
        this.clientService = clientService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    Client client = new Client();
                    client.setLogin(update.getMessage().getChat().getFirstName());
                    client.setName(update.getMessage().getChat().getLastName());
                    client.setChatId(chatId);
                    clientService.save(client);
                    break;
            }
        }

    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "я люююблюю тебя до слёёёёз кажый день как пелвыыый лаз, блеск тових класивых гаааз это облаааако из лооооз";
        sendMessage(chatId, answer);
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
        return "dsdsdsds";
    }
}
