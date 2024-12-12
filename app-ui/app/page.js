'use client';

import React, { useState, useEffect } from 'react';
import Layout from './components/Layout';
import { Download, Shield, Lock, Gem } from 'lucide-react';
import { useUser } from './contexts/UserContext';
import axiosConfig from './config/axiosConfig';

export default function Home() {
  const { userData, updateUserData } = useUser();
  const [isPaid, setIsPaid] = useState(false);
  const [isInitialized, setIsInitialized] = useState(false);

  useEffect(() => {
    const initUser = async () => {
      if (isInitialized || !window.Telegram?.WebApp) {
        return;
      }

      const user = window.Telegram?.WebApp?.initDataUnsafe?.user;
      if (!user) {
        console.error('User data not found');
        return;
      }

      const username = user.username ? user.username : user.id;

      try {
        const params = new URLSearchParams({
          userId: user.id || '770055005',
          username: username || 'all0b0y',
        });

        const response = await axiosConfig.get(`/user/profile?${params.toString()}`);
        const userInfo = response.data;

        updateUserData(userInfo);
        setIsPaid(userInfo.active);
        setIsInitialized(true);
      } catch (error) {
        console.error('Error initializing user:', error);
      }
    };

    if (window.Telegram?.WebApp) {
      initUser();
    }
  }, [updateUserData, isInitialized]);

  const handlePurchase = () => {
    if (!isPaid) {
      if (window.Telegram?.WebApp) {
        window.Telegram.WebApp.showAlert('Перенаправление на страницу оплаты...');
      }
      window.location.href = '/payment';
    } else {
      if (window.Telegram?.WebApp) {
        window.Telegram.WebApp.showAlert('Подписка уже активна.');
      }
    }
  };

  const handleDownloadConfig = (server) => {
    if (!isPaid) {
      if (window.Telegram?.WebApp) {
        window.Telegram.WebApp.showAlert('Пожалуйста, приобретите VPN для загрузки конфигурации');
      }
      return;
    }

    const downloadUrl = `https://ruby-tunnel.ru/api/user/config?userId=${userData.chatId}`;

    if (window.Telegram?.WebApp) {
      // Перенаправляем в браузер
      window.Telegram.WebApp.openLink(downloadUrl);
    } else {
      // Если Telegram Web App недоступен, используем обычный переход
      window.location.href = downloadUrl;
    }
  };

  const formatSubscriptionDate = (dateString) => {
    if (!dateString) return '';
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

  const subscriptionText = isPaid
    ? `Подписка активна до ${formatSubscriptionDate(userData?.subscriptionEndDate)}`
    : `Купить VPN за ${userData?.price || '999'} ₽`;

  return (
    <Layout currentPage="home">
      <div className="flex flex-col items-center justify-center space-y-6">
        <div className="relative">
          <Shield className="w-20 h-20 text-red-500" />
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="w-10 h-10 bg-red-600 rounded-full transform rotate-45"></div>
          </div>
        </div>
        <h1 className="text-3xl font-bold text-red-500 text-center">RubyTunnel</h1>
        <p className="text-red-300 text-center max-w-md">
          Откройте для себя скрытый драгоценный камень VPN-технологии. RubyTunnel: Где безопасность сияет ярко, как отполированный рубин.
        </p>
        <div className="flex space-x-4">
          <button
            onClick={handlePurchase}
            className={`${isPaid ? 'bg-green-600 hover:bg-green-700' : 'bg-red-600 hover:bg-red-700'} text-white px-6 py-2 rounded-full flex items-center justify-center transition-colors duration-300 text-sm`}
          >
            <Gem className="w-4 h-4 mr-2" />
            {subscriptionText}
          </button>
        </div>

        <div className="w-full max-w-md space-y-4">
          <div className="bg-gradient-to-br from-red-900 via-red-800 to-black p-6 rounded-2xl shadow-2xl border-2 border-red-600 relative overflow-hidden">
            <h2 className="text-xl font-bold text-red-300 mb-4 flex items-center">
              <span className="mr-2 text-2xl" aria-label="Флаг Нидерландов">🇳🇱</span>
              Нидерланды
            </h2>
            <button
              onClick={() => handleDownloadConfig('Нидерланды')}
              className="w-full bg-red-700 hover:bg-red-600 text-white py-2 px-4 rounded-full flex items-center justify-center transition-colors duration-300"
              disabled={!isPaid}
            >
              <Download className="w-4 h-4 mr-2" />
              Скачать конфигурацию
            </button>
            {!isPaid && (
              <div className="absolute inset-0 bg-black bg-opacity-70 flex items-center justify-center">
                <Lock className="w-12 h-12 text-red-500 animate-pulse" />
              </div>
            )}
          </div>

          <div className="bg-gradient-to-br from-red-900 via-red-800 to-black p-6 rounded-2xl shadow-2xl border-2 border-red-600 relative overflow-hidden">
            <h2 className="text-xl font-bold text-red-300 mb-4 flex items-center">
              <span className="mr-2 text-2xl" aria-label="Флаг Швеции">🇸🇪</span>
              Швеция
            </h2>
            <button
              onClick={() => handleDownloadConfig('Швеция')}
              className="w-full bg-red-700 hover:bg-red-600 text-white py-2 px-4 rounded-full flex items-center justify-center transition-colors duration-300"
              disabled={!isPaid}
            >
              <Download className="w-4 h-4 mr-2" />
              Скачать конфигурацию
            </button>
            {!isPaid && (
              <div className="absolute inset-0 bg-black bg-opacity-70 flex items-center justify-center">
                <Lock className="w-12 h-12 text-red-500 animate-pulse" />
              </div>
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
}
