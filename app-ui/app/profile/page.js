'use client';

import React, { useEffect, useState } from 'react';
import Layout from '../components/Layout';
import {
  User,
  HelpCircle,
  Apple,
  Smartphone as Android,
  Monitor as Windows,
  Users,
  CreditCard,
} from 'lucide-react';
import { useUser } from '../contexts/UserContext';
import Link from 'next/link';

export default function Profile() {
  const { userData } = useUser();
  const [platform, setPlatform] = useState('unknown');

  useEffect(() => {
    const userAgent = navigator.userAgent.toLowerCase();
    if (/android/.test(userAgent)) {
      setPlatform('android');
    } else if (/iphone|ipad|ipod/.test(userAgent)) {
      setPlatform('ios');
    } else if (/win/.test(userAgent)) {
      setPlatform('windows');
    } else if (/mac/.test(userAgent)) {
      setPlatform('mac');
    }
  }, []);

  const userInfo = {
    name: userData?.nameTg || 'Гость',
    active: userData?.active || false,
    subscriptionEndDate: userData?.subscriptionEndDate || 'Не активирована',
    price: userData?.price ? `${userData.price} ₽/месяц` : 'Не указана',
  };

  const faqItems = [
    { question: 'Как использовать RubyTunnel?', answer: 'Скачайте приложение, установите и следуйте инструкциям по настройке.' },
    { question: 'Какие устройства поддерживаются?', answer: 'RubyTunnel поддерживает iOS, Android, macOS и Windows.' },
    { question: 'Как обновить подписку?', answer: 'Перейдите в раздел "Оплата" и выберите план продления.' },
    {
      question: 'Как работает ваша реферальная система?',
      answer: `Реферал — это участник партнёрской программы, который регистрируется на проекте по рекомендации другого участника. Вы рекомендуете нас друзьям, а мы вознаграждаем вас скидками на услуги. Всё просто! Ознакомьтесь с дополнительной информацией можно в нашем сообществе!`,
    },
  ];

  const downloadLinks = {
    ios: {
      name: 'iOS',
      icon: Apple,
      link: 'https://itunes.apple.com/us/app/wireguard/id1441195209?ls=1&mt=8',
    },
    android: {
      name: 'Android',
      icon: Android,
      link: 'https://play.google.com/store/apps/details?id=com.wireguard.android',
    },
    mac: {
      name: 'macOS',
      icon: Apple,
      link: 'https://itunes.apple.com/us/app/wireguard/id1451685025?ls=1&mt=12',
    },
    windows: {
      name: 'Windows',
      icon: Windows,
      link: 'https://download.wireguard.com/windows-client/wireguard-installer.exe',
    },
  };

  const formatSubscriptionDate = (dateString) => {
    if (!dateString || dateString === 'Не активирована') return 'Не активирована';
    const date = new Date(dateString);
    const options = {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    };
    return date.toLocaleString('ru-RU', options);
  };

  const handleDownloadClick = () => {
    const currentLink = downloadLinks[platform]?.link || '#';
    if (currentLink === '#') {
      alert('Ссылка для вашего устройства пока недоступна.');
    } else if (window.Telegram?.WebApp) {
      window.Telegram.WebApp.openLink(currentLink);
    } else {
      window.location.href = currentLink;
    }
  };

  const handleOpenTelegramGroup = () => {
    const groupLink = 'https://t.me/rubytun_vpn';
    if (window.Telegram?.WebApp) {
      window.Telegram.WebApp.openLink(groupLink);
    } else {
      window.location.href = groupLink;
    }
  };

  return (
      <Layout currentPage="profile">
        <div className="space-y-8">
          <div className="flex flex-col items-center">
            <div className="w-24 h-24 bg-gradient-to-br from-red-600 to-red-900 rounded-full flex items-center justify-center mb-4">
              <User className="w-12 h-12 text-white" />
            </div>
            <h1 className="text-3xl font-bold text-red-500">{userInfo.name}</h1>
            {userData?.chatId && <p className="text-red-400 mt-2">ID: {userData.chatId}</p>}
          </div>

          <div className="space-y-4">
            <div className="bg-gradient-to-r from-red-800 to-black p-4 rounded-md">
              <h2 className="text-xl font-semibold text-red-400 mb-2">Статус подписки</h2>
              <p className="text-red-300 text-lg">{userInfo.active ? 'Активна' : 'Неактивна'}</p>
            </div>
            <div className="bg-gradient-to-r from-red-800 to-black p-4 rounded-md">
              <h2 className="text-xl font-semibold text-red-400 mb-2">Подписка истекает</h2>
              <p className="text-red-300 text-lg">{formatSubscriptionDate(userInfo.subscriptionEndDate)}</p>
            </div>
            <div className="bg-gradient-to-r from-red-800 to-black p-4 rounded-md">
              <h2 className="text-xl font-semibold text-red-400 mb-2">Текущая цена подписки</h2>
              <p className="text-red-300 text-lg flex items-center">
                <CreditCard className="w-5 h-5 mr-2" />
                {userInfo.price}
              </p>
            </div>
          </div>

          <div className="flex justify-center">
            <button
                onClick={handleOpenTelegramGroup}
                className="bg-red-600 hover:bg-red-700 text-white px-6 py-3 rounded-full flex items-center justify-center transition-colors duration-300"
            >
              <Users className="w-5 h-5 mr-2" />
              Присоединиться к Telegram-группе
            </button>
          </div>

          <div className="bg-gradient-to-r from-red-900 to-black p-6 rounded-lg shadow-lg border border-red-800">
            <h2 className="text-2xl font-semibold text-red-400 mb-4">Скачать RubyTunnel</h2>
            <div className="flex items-center justify-center">
              {platform !== 'unknown' && downloadLinks[platform] ? (
                  (() => {
                    const { icon: Icon, name } = downloadLinks[platform]; // Деструктурируем icon и name
                    return (
                        <button
                            onClick={handleDownloadClick}
                            className="flex items-center justify-center bg-red-800 hover:bg-red-700 text-white p-3 rounded-md transition-colors duration-300"
                        >
                          <Icon className="w-6 h-6 mr-2" />
                          Скачать для {name}
                        </button>
                    );
                  })()
              ) : (
                  <p className="text-red-300">Ваше устройство пока не поддерживается.</p>
              )}
            </div>
          </div>

          <div className="bg-gradient-to-r from-red-900 to-black p-6 rounded-lg shadow-lg border border-red-800">
            <h2 className="text-2xl font-semibold text-red-400 mb-4">Часто задаваемые вопросы</h2>
            <div className="space-y-4">
              {faqItems.map((item, index) => (
                  <div key={index} className="bg-red-800 p-4 rounded-md">
                    <h3 className="text-lg font-semibold text-red-300 mb-2">{item.question}</h3>
                    <p className="text-red-200">{item.answer}</p>
                  </div>
              ))}
            </div>
          </div>
        </div>
      </Layout>
  );
}
