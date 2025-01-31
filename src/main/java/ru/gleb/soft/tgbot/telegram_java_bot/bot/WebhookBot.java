package ru.gleb.soft.tgbot.telegram_java_bot.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.gleb.soft.tgbot.telegram_java_bot.enums.modes;

import java.io.*;
import java.util.*;

@Slf4j
@Getter
@Setter
public class WebhookBot extends TelegramWebhookBot {
    private String botPath;
    private String botUsername;
    private String botToken;
    DefaultBotOptions defaultBotOptions;
    private HashMap<Integer, String> phrases;
    private int phrases_count;
    private Random rand;
    private Queue<Integer> recently_phrases;
    private modes mode;

    public WebhookBot(DefaultBotOptions options) {
        super(options);
        phrases = getPhrasesList();
        assert phrases != null;
        phrases_count = phrases.size();
        rand = new Random();
        mode = modes.dialog;
        recently_phrases = new LinkedList<>();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("Запрос получен " + getMode());
        log.info(update.toString());
        try {
            if (checkCommands(update)) return null;
            log.info("Команды проверены " + getMode());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        switch (mode) {
            case dialog -> getBotAnswer(update);
            case adding -> setBotPhrase(update);
        }
        return null;
    }

    private void getBotAnswer(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().getReplyToMessage() != null) {
                if (update.getMessage().getReplyToMessage().getFrom().getId() == 8013072863L) {
                    var chatId = update.getMessage().getChatId();
                    var phraseID = getPhraseID();
                    var replyMess = update.getMessage();
                    String messageText = phrases.get(phraseID);
                    sendMessage(chatId, messageText, replyMess.getMessageId());
                }
            }
        }
    }

    private int getPhraseID() {
        var phraseID = rand.nextInt(phrases_count);
        if (recently_phrases.isEmpty()) {
            recently_phrases.add(phraseID);
            return phraseID;
        }
        while (recently_phrases.contains(phraseID)) {
            phraseID = rand.nextInt(phrases_count);
        }
        recently_phrases.add(phraseID);
        if (recently_phrases.size() > 40) {
            recently_phrases.poll();
        }
        log.info("выбрано id: " + phraseID);
        return phraseID;
    }

    private void setBotPhrase(Update update) {
        log.info("SetBotPhrase " + getMode());
        var newPhrase = update.getMessage();
        try {
            addPhrase(newPhrase.getText(), true);
            log.info("SetBotPhrase OK " + getMode());
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        } catch (TelegramApiException ignored) {

        }
    }

    private boolean checkCommands(Update update) throws TelegramApiException {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var command = update.getMessage().getText().replaceAll("@.*", "");
            switch (command) {
                case "/start":
                    sendMessage(update.getMessage().getChatId(), phrases.get(rand.nextInt(phrases_count)), 0);
                    return true;
                case "/add":
                    mode = modes.adding;
                    sendMessage(update.getMessage().getChatId(), "Пришлите фразы для добавление", 0);
                    return true;
                case "/stop":
                    mode = modes.dialog;
                    phrases.clear();
                    phrases = getPhrasesList();
                    phrases_count = phrases.size();
                    log.info("количество фраз: " + phrases_count);
                    sendPhrasesList(506238949L);
                    sendPhrasesList(update.getMessage().getChatId());
                    sendMessage(update.getMessage().getChatId(), "Фразы добавлены", 0);
                    return true;
                case "/phrases":
                    sendPhrasesList(update.getMessage().getChatId());
                    return true;
                case "/count":
                    sendMessage(update.getMessage().getChatId(), "Фраз в списке: " + phrases.size(), 0);
                    return true;
                case "/docker":
                    sendMessage(update.getMessage().getChatId(), "привет поцы", 0);
                    return true;
            }
        }
        if (update.hasMessage() && update.getMessage().hasDocument() && update.getMessage().getFrom().getId() != 8013072863L) {
            log.info("Получил документ");
            setPhrasesFromFile(update);
            sendMessage(update.getMessage().getChatId(), "фразы загружены", 0);
            return false;
        }
        return false;
    }

    private HashMap<Integer, String> getPhrasesList() {
        try {
            var reader = new BufferedReader(new FileReader("phrases.txt"));
            String line = reader.readLine();
            var result = new HashMap<Integer, String>();
            int index = 1;
            result.put(index++, line);
            while (line != null) {
                line = reader.readLine();
                result.put(index++, line);
            }
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addPhrase(String phrase, boolean append) throws IOException {
        log.info("открываю в файл");
        FileWriter writer = new FileWriter("phrases.txt", append);
        BufferedWriter bufferWriter = new BufferedWriter(writer);
        log.info("пишу в файл: " + phrase);
        bufferWriter.write(phrase + "\r\n");
        bufferWriter.close();
        log.info("закрыл файл");
    }

    private void sendPhrasesList(long chatId) throws TelegramApiException {
        File file = new File("phrases.txt");
        InputFile inputFile = new InputFile(file, "phrases.txt");

        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        sendDocumentRequest.setDocument(inputFile);
        execute(sendDocumentRequest);
    }

    private void setPhrasesFromFile(Update update) {
        GetFile getFile = getGetFile(update);
        try {
            org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
            downloadFile(file, new File("phrasefile.txt"));
            var reader = new BufferedReader(new FileReader("phrasefile.txt"));
            String line = reader.readLine();
            addPhrase(line, false);
            log.info(line);
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    addPhrase(line, true);
                }
                log.info(line);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static GetFile getGetFile(Update update) {
        String doc_id = update.getMessage().getDocument().getFileId();
        String doc_name = update.getMessage().getDocument().getFileName();
        String doc_mine = update.getMessage().getDocument().getMimeType();
        int doc_size = Math.toIntExact(update.getMessage().getDocument().getFileSize());

        Document document = new Document();
        document.setMimeType(doc_mine);
        document.setFileName(doc_name);
        document.setFileSize((long) doc_size);
        document.setFileId(doc_id);

        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        return getFile;
    }

    @Scheduled(cron = "* 0 */2 * * *")
    private void sendSchedulerMessage() {
        log.info("сру по распианию!");
        var chatId = -1002362332718L;
        var phraseID = getPhraseID();
        String messageText = phrases.get(phraseID);
        sendMessage(chatId, "сру по распианию!", 0);
        sendMessage(chatId, messageText, 0);
    }

    @Override
    public String getBotUsername() {
        return "Paha_Svin";
    }
}
