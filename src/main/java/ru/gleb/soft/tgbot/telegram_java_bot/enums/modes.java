package ru.gleb.soft.tgbot.telegram_java_bot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum modes {
    dialog(0, "Отвечает на сообщения"),

    adding(1, "Добавляем фразы");

    private final int modeId;
    private final String modeDescription;
}
