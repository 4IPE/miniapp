'use client';

import React, { useState, useEffect } from 'react';
import Layout from './components/Layout';
import { Download, Shield, Lock, Gem } from 'lucide-react';
import { useUser } from './contexts/UserContext';
import axiosConfig from "./config/axiosConfig";

const API_URL = 'https://1bc3-176-126-49-56.ngrok-free.app/api';

export default function Home() {
  const { userData, updateUserData } = useUser();
  const [isPaid, setIsPaid] = useState(userData?.subscriptionActive || false);
  const [isInitialized, setIsInitialized] = useState(false);

  // Функция для тестового запроса
  const handleTestRequest = async () => {
    try {
      console.log('Starting test request...');
      
      // Логируем доступность Telegram WebApp
      console.log('Telegram WebApp available:', !!window.Telegram?.WebApp);
      
      const tg = window.Telegram?.WebApp;
      const userId = tg?.initDataUnsafe?.user?.id || '1';
      const username = tg?.initDataUnsafe?.user?.username || 'test_user';
      
      console.log('Request parameters:', { userId, username });

      // Добавляем полный URL для отладки
      const fullUrl = `${API_URL}/user/profile?userId=${userId}&username=${username}`;
      console.log('Making request to:', fullUrl);

      try {
        // Пробуем сначала с axios
        const response = await axiosConfig.get(`/user/profile`, {
          params: { userId, username }
        });
        console.log('Axios response:', response);
      } catch (axiosError) {
        console.error('Axios error:', axiosError);
        
        // Пробуем с fetch как запасной вариант
        console.log('Trying with fetch...');
        const fetchResponse = await fetch(fullUrl);
        const data = await fetchResponse.json();
        console.log('Fetch response:', data);
      }

      // Показываем результат в UI
      alert('Запрос выполнен, проверьте консоль');
      
    } catch (error) {
      console.error('Final error:', error);
      alert(`Ошибка: ${error.message}`);
    }
  };

  useEffect(() => {
    const initUser = async () => {
      // Проверяем, был ли уже инициализирован пользователь
      if (isInitialized || !window.Telegram?.WebApp) {
        return;
      }

      try {
        const params = new URLSearchParams({
          userId: userData?.id,
          username: userData?.username
        });

        const response = await axiosConfig.get(`${API_URL}/user/profile?${params}`);
        const userInfo = response.data;
        
        updateUserData(userInfo);
        setIsPaid(userInfo.subscriptionActive);
        setIsInitialized(true); // Помечаем, что инициализация прошла
      } catch (error) {
        console.error('Error initializing user:', error);
      }
    };

    if (window.Telegram?.WebApp) {
      initUser();
    }
  }, [updateUserData]);

  const handlePurchase = () => {
    setIsPaid(true);
    if (window.Telegram?.WebApp) {
      window.Telegram.WebApp.showAlert('Подписка успешно активирована!');
    }
  };

  const handleDownloadConfig = (server) => {
    if (isPaid) {
      console.log(`Загрузка конфигурации для ${server}...`);
      if (window.Telegram?.WebApp) {
        window.Telegram.WebApp.showAlert(`Загрузка конфигурации для ${server} началась...`);
      }
    } else {
      if (window.Telegram?.WebApp) {
        window.Telegram.WebApp.showAlert('Пожалуйста, приобретите VPN для загрузки конфигурации');
      }
    }
  };

  return (
    <Layout currentPage="home">
      <div className="flex flex-col items-center justify-center space-y-6">
        <button 
          onClick={handleTestRequest}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Тест соединения
        </button>
        <div className="relative">
          <Shield className="w-20 h-20 text-red-500" />
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="w-10 h-10 bg-red-600 rounded-full transform rotate-45"></div>
          </div>
        </div>
        <h1 className="text-3xl font-bold text-red-500 text-center">RubyTunnel</h1>
        <p className="text-red-300 text-center max-w-md">
          Откройте для себя скрытый драгоценный камень VPN-технологии. RubyTunnel: Где безопасность сияет ярко, как отполированный руби��.
        </p>
        <div className="flex space-x-4">
          <button 
            onClick={handlePurchase} 
            className={`${isPaid ? 'bg-green-600 hover:bg-green-700' : 'bg-red-600 hover:bg-red-700'} text-white px-6 py-2 rounded-full flex items-center justify-center transition-colors duration-300 text-sm`}
          >
            <Gem className="w-4 h-4 mr-2" />
            {isPaid ? `Подписка активна до ${userData?.subscriptionEndDate}` : `Купить VPN за ${userData?.price || '999'} ₽`}
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
              <span className="mr-2 text-2xl" aria-label="Флаг Казахстана">🇰🇿</span>
              Казахстан
            </h2>
            <button
              onClick={() => handleDownloadConfig('Казахстан')}
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

