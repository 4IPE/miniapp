import { Telegraf, Markup, Context } from 'telegraf';
import { Update, Message } from 'telegraf/typings/core/types/typegram';
import dotenv from 'dotenv';

// Конфигурация бота
const BOT_TOKEN = '7477824545:AAGgE7PVm9DwLEZahSyCwzN0oN9-mZqpWeo';
const WEB_APP_URL = 'https://aba2-176-126-49-56.ngrok-free.app';

const bot = new Telegraf(BOT_TOKEN);

// Обработка команды /start
bot.command('start', async (ctx: Context<Update>) => {
    try {
        await ctx.reply(
            'Добро пожаловать в RubyTunnel! 🚀\n\nВыберите действие:',
            Markup.keyboard([
                [Markup.button.webApp('🌐 Открыть приложение', `${WEB_APP_URL}`)],
                ['ℹ️ Информация', '🆘 Поддержка']
            ]).resize()
        );
    } catch (e: unknown) {
        console.error('Error in start command:', e);
    }
});

// Inline keyboard с WebApp
bot.command('webapp', async (ctx: Context<Update>) => {
    try {
        await ctx.reply(
            'Выберите действие:',
            Markup.inlineKeyboard([
                [Markup.button.webApp('🌐 Открыть WebApp', `${WEB_APP_URL}`)]
            ])
        );
    } catch (e: unknown) {
        console.error('Error in webapp command:', e);
    }
});

// Обработка текстовых сообщений
bot.on('text', async (ctx: Context<Update.MessageUpdate>) => {
    try {
        const message = ctx.message as Message.TextMessage;
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
    } catch (e: unknown) {
        console.error('Error processing text message:', e);
    }
});

// Обработка данных из WebApp
bot.on('web_app_data', async (ctx: Context<Update>) => {
    try {
        if (ctx.webAppData) {
            const data = ctx.webAppData.data;
            await ctx.reply(`Получены данные из WebApp: ${data}`);
        }
    } catch (e: unknown) {
        console.error('Error processing web_app_data:', e);
        await ctx.reply('Произошла ошибка при обработке данных');
    }
});

// Обработка ошибок
bot.catch((err: unknown, ctx: Context<Update>) => {
    const errorMessage = err instanceof Error ? err.message : 'Unknown error';
    console.error(`Error for ${ctx.updateType}:`, errorMessage);
});

// Запуск бота
bot.launch()
    .then(() => {
        console.log('Bot started successfully');
    })
    .catch((err: unknown) => {
        const errorMessage = err instanceof Error ? err.message : 'Unknown error';
        console.error('Error starting bot:', errorMessage);
    });

// Graceful stop
process.once('SIGINT', () => bot.stop('SIGINT'));
process.once('SIGTERM', () => bot.stop('SIGTERM')); 