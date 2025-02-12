#
FROM  maven:3.8.3-openjdk-17 as build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-slim
COPY --from=build /target/telegram-java-bot-0.0.1-SNAPSHOT.jar CryptoService.jar

COPY --from=build /target/classes/phrases.txt phrases.txt

COPY --from=build /target/classes/audio/audio_0.ogg audio_0.ogg
COPY --from=build /target/classes/audio/audio_1.ogg audio_1.ogg
COPY --from=build /target/classes/audio/audio_2.ogg audio_2.ogg
COPY --from=build /target/classes/audio/audio_3.ogg audio_3.ogg
COPY --from=build /target/classes/audio/audio_4.ogg audio_4.ogg
COPY --from=build /target/classes/audio/audio_5.ogg audio_5.ogg
COPY --from=build /target/classes/audio/audio_6.ogg audio_6.ogg
COPY --from=build /target/classes/audio/audio_7.ogg audio_7.ogg

COPY --from=build /target/classes/stickers/webm/sticker_0.webm sticker_0.webm
COPY --from=build /target/classes/stickers/webm/sticker_1.webm sticker_1.webm
COPY --from=build /target/classes/stickers/webm/sticker_2.webm sticker_2.webm
COPY --from=build /target/classes/stickers/webm/sticker_3.webm sticker_3.webm
COPY --from=build /target/classes/stickers/webm/sticker_4.webm sticker_4.webm
COPY --from=build /target/classes/stickers/webm/sticker_5.webm sticker_5.webm
COPY --from=build /target/classes/stickers/webm/sticker_6.webm sticker_6.webm
COPY --from=build /target/classes/stickers/webm/sticker_7.webm sticker_7.webm
COPY --from=build /target/classes/stickers/webm/sticker_8.webm sticker_8.webm
COPY --from=build /target/classes/stickers/webm/sticker_9.webm sticker_9.webm
COPY --from=build /target/classes/stickers/webm/sticker_10.webm sticker_10.webm

COPY --from=build /target/classes/stickers/web/sticker_0.webp sticker_0.webp
COPY --from=build /target/classes/stickers/web/sticker_1.webp sticker_1.webp
COPY --from=build /target/classes/stickers/web/sticker_2.webp sticker_2.webp

COPY --from=build /target/classes/video/video_0.mp4 video_0.mp4
COPY --from=build /target/classes/video/video_1.mp4 video_1.mp4
COPY --from=build /target/classes/video/video_2.mp4 video_2.mp4
COPY --from=build /target/classes/video/video_3.mp4 video_3.mp4
COPY --from=build /target/classes/video/video_4.mp4 video_4.mp4
COPY --from=build /target/classes/video/video_5.mp4 video_5.mp4
COPY --from=build /target/classes/video/video_6.mp4 video_6.mp4
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","CryptoService.jar"]