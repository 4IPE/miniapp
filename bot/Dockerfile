FROM node:18-alpine

WORKDIR /app

# Копируем файлы зависимостей
COPY package*.json ./

# Устанавливаем зависимости
RUN npm install

# Копируем исходный код и конфиг TypeScript
COPY . .
COPY tsconfig.json .

# Собираем TypeScript в JavaScript
RUN npm run build

# Указываем порт (если нужно)
EXPOSE 8000

# Запускаем собранный бот
CMD ["node", "dist/bot.js"] 