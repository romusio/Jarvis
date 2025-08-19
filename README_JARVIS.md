# Jarvis (MVP)

Запуск:

1) Установите Java 17+ и Maven
2) Сборка и запуск:

```
mvn spring-boot:run
```

API:

POST /api/v1/command

Body:

```
{ "text": "Джарвис, напомни позвонить маме завтра в 10" }
```

Ответ: `reply`, `intent`, `data`

Поиск в интернете (реальное время):
- Требуется Google Custom Search JSON API
- Переменные окружения:
  - `GOOGLE_API_KEY` — ключ API
  - `JARVIS_GOOGLE_CSE_ID` — идентификатор поисковой машины (cx)
- Примеры запросов: «поиск погода москва сейчас», «что такое квантовая запутанность», "search latest java lts"

Голос (опционально):

- POST /api/v1/voice/speak (text/plain) — возвращает аудиопоток.
- По умолчанию возвращается пустой поток (Noop). Для Google TTS:
  - Включите переменную окружения `GOOGLE_APPLICATION_CREDENTIALS` на JSON ключ GCP
  - Установите свойство `-Djarvis.voice.provider=google` и, опционально, `-Djarvis.voice.language=ru-RU`
  - Пример: `mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Djarvis.voice.provider=google -Djarvis.voice.language=ru-RU"`

Календарь (опционально):
Веб-страница для голосового чата:

- Откройте `http://localhost:8080/chat.html`
- Кнопка “Говорить” использует Web Speech API (в Chrome/Edge) для клиентского распознавания речи
- Ответ озвучивается через `/api/v1/voice/speak`


- По умолчанию используется in-memory календарь.
- Для Google Calendar:
  - Включите Calendar API в Google Cloud Console
  - Установите переменную окружения `GOOGLE_APPLICATION_CREDENTIALS` на путь к JSON ключу (Service Account или OAuth ADC)
  - Запускайте с `-Djarvis.calendar.provider=google` и, при необходимости, `-Djarvis.calendar.timezone=Europe/Moscow`
  - Пример: `mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Djarvis.calendar.provider=google -Djarvis.calendar.timezone=Europe/Moscow"`


