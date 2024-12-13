import { Telegraf, Markup } from 'telegraf';
import axiosConfig from '/config/axiosConfig.js';
import dotenv from 'dotenv';
dotenv.config();

const BOT_TOKEN = '7477824545:AAGgE7PVm9DwLEZahSyCwzN0oN9-mZqpWeo';
const WEB_APP_URL = 'https://ruby-tunnel.ru';
const bot = new Telegraf(BOT_TOKEN);


bot.command('start', async (ctx) => {
    try {
        await ctx.reply(
            'Добро пожаловать в RubyTunnel! 🚀\n\nВыберите действие:',
            Markup.keyboard([
                [Markup.button.webApp('🌐 Открыть приложение', WEB_APP_URL)],
                ['ℹ️ Информация', '🆘 Поддержка']
            ]).resize()
        );
    } catch (e) {
        console.error('Error in start command:', e);
    }
});


bot.command('webapp', async (ctx) => {
    try {
        await ctx.reply(
            'Выберите действие:',
            Markup.inlineKeyboard([
                [Markup.button.webApp('🌐 Открыть WebApp', WEB_APP_URL)]
            ])
        );
    } catch (e) {
        console.error('Error in webapp command:', e);
    }
});


bot.on('text', async (ctx) => {
    try {
        const message = ctx.message;
        const text = message.text;

        switch (text) {
            case 'ℹ️ Информация':
                await ctx.reply(
                    'RubyTunnel - ваш надежный VPN-сервис!\n\n' +
                    '🔒 Безопасное соединение\n' +
                    '🚀 Высокая скорость\n' +
                    '🌍 Серверы по всему миру'
                );
                break;

            case '🆘 Поддержка':
                await ctx.reply(
                    'Если у вас возникли вопросы, обратитесь в нашу службу поддержки:\n' +
                    '@rubytunnel_support'
                );
                break;
        }
    } catch (e) {
        console.error('Error processing text message:', e);
    }
});


bot.on('web_app_data', async (ctx) => {
    try {
        if (ctx.webAppData) {
            const data = ctx.webAppData.data;
            await ctx.reply(`Получены данные из WebApp: ${data}`);
        }
    } catch (e) {
        console.error('Error processing web_app_data:', e);
        await ctx.reply('Произошла ошибка при обработке данных');
    }
});


bot.catch((err, ctx) => {
    const errorMessage = err instanceof Error ? err.message : 'Unknown error';
    console.error(`Error for ${ctx.updateType}:`, errorMessage);
});


function scheduleSubscriptionCheck() {
    setInterval(async () => {
        try {
            // Отправляем GET-запрос к вашему бэкенду через axiosConfig
            const res = await axiosConfig.get();
            // Предполагаем, что res.data – это объект вида { "chatId1": "message1", "chatId2": "message2", ... }
            const resultMap = res.data;

            for (const [chatId, message] of Object.entries(resultMap)) {
                await bot.telegram.sendMessage(chatId, message, { parse_mode: 'Markdown' });
                console.log(`Отправлено сообщение пользователю ${chatId}`);
            }
        } catch (error) {
            console.error('Error checking subscriptions:', error.message);
        }
    }, 60 * 60 * 1000); // 1 час = 3600000 мс
}


bot.launch()
    .then(() => {
        console.log('Bot started successfully');
        scheduleSubscriptionCheck();
    })
    .catch((err) => {
        const errorMessage = err instanceof Error ? err.message : 'Unknown error';
        console.error('Error starting bot:', errorMessage);
    });


process.once('SIGINT', () => bot.stop('SIGINT'));
process.once('SIGTERM', () => bot.stop('SIGTERM'));
