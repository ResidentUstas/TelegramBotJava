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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class TelegramBotFrame extends TelegramLongPollingBot {
    @Autowired
    DefaultBotOptions defaultBotOptions;
    private List<String> phrases;
    private int phrases_count;
    private Random rand;
    private int mode;

    public TelegramBotFrame(DefaultBotOptions defaultBotOptions, @Value("${bot.token}") String botToken) {
        super(defaultBotOptions, botToken);

        List<BotCommand> botCommandList = new ArrayList<>();
        phrases = getPhrasesList();
        phrases_count = phrases.size();
        rand = new Random();
        mode = 0;
        try {
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().equals("/start")) {
                sendMessage(update.getMessage().getChatId(), "Привет петухи", 0);
            }
            if (update.getMessage().getText().equals("/add")) {
                mode = 1;
                sendMessage(update.getMessage().getChatId(), "Пришлите фразы для добавление", 0);
            }
            if (update.getMessage().getText().equals("/stopadd")) {
                mode = 0;
                sendMessage(update.getMessage().getChatId(), "Фразы добавлены", 0);
            }
        }

        if (update.hasMessage() && update.getMessage().getReplyToMessage() != null)
            if (mode == 0) {
                if (update.getMessage().getReplyToMessage().getFrom().getId() == 8013072863L) {
                    var chatId = update.getMessage().getChatId();
                    var replyMess = update.getMessage();
                    String messageText = phrases.get(rand.nextInt(phrases_count));
                    sendMessage(chatId, messageText, replyMess.getMessageId());
                }
            } else {
                var newPhrase = update.getMessage();
                try {
                    addPhrase(newPhrase.getText());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    private void sendMessage(Long chatId, String textToSend, int replyId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        if (replyId > 0) {
            sendMessage.setReplyToMessageId(replyId);
        }
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    private ArrayList<String> getPhrasesList() {
        try {
            var reader = new BufferedReader(new FileReader("src\\main\\resources\\phrases.txt"));
            String line = reader.readLine();
            var result = new ArrayList<String>();
            result.add(line);
            while (line != null) {
                line = reader.readLine();
                result.add(line);
            }

            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addPhrase(String phrase) throws IOException {
        FileWriter writer = new FileWriter("src\\main\\resources\\phrases.txt", true);
        BufferedWriter bufferWriter = new BufferedWriter(writer);
        bufferWriter.write(phrase + "\r\n");
        bufferWriter.close();
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return "Paha_Svin";
    }
}
