'use client'

import React from 'react'
import Layout from '../components/Layout'
import { User, HelpCircle, Download, Apple, ComputerIcon as Windows, SmartphoneIcon as Android, Users, CreditCard } from 'lucide-react'
import Link from 'next/link'
import { useUser } from '../contexts/UserContext'

export default function Profile() {
  const { userData } = useUser()

  const userInfo = {
    name: userData?.nameTg || 'Гость',
    active: userData?.active || false,
    subscriptionEndDate: userData?.subscriptionEndDate || 'Не активирована',
    price: userData?.price ? `${userData.price} ₽/месяц` : 'Не указана',
  }

  const faqItems = [
    { question: 'Как использовать RubyTunnel?', answer: 'Скачайте приложение, установите и следуйте инструкциям по настройке.' },
    { question: 'Какие устройства поддерживаются?', answer: 'RubyTunnel поддерживает iOS, Android, macOS и Windows.' },
    { question: 'Как обновить подписку?', answer: 'Перейдите в раздел "Оплата" и выберите план продления.' },
  ]

  const downloadLinks = [
    { name: 'iOS', icon: Apple, link: '#' },
    { name: 'Android', icon: Android, link: '#' },
    { name: 'macOS', icon: Apple, link: '#' },
    { name: 'Windows', icon: Windows, link: '#' },
  ]

  return (
    <Layout currentPage="profile">
      <div className="space-y-8">
        <div className="flex flex-col items-center">
          <div className="w-24 h-24 bg-gradient-to-br from-red-600 to-red-900 rounded-full flex items-center justify-center mb-4">
            <User className="w-12 h-12 text-white" />
          </div>
          <h1 className="text-3xl font-bold text-red-500">{userInfo.name}</h1>
          {userData?.chatId && (
            <p className="text-red-400 mt-2">ID: {userData.chatId}</p>
          )}
        </div>

        <div className="space-y-4">
          <div className="bg-gradient-to-r from-red-800 to-black p-4 rounded-md">
            <h2 className="text-xl font-semibold text-red-400 mb-2">Статус подписки</h2>
            <p className="text-red-300 text-lg">
              {userInfo.active ? 'Активна' : 'Неактивна'}
            </p>
          </div>
          <div className="bg-gradient-to-r from-red-800 to-black p-4 rounded-md">
            <h2 className="text-xl font-semibold text-red-400 mb-2">Подписка истекает</h2>
            <p className="text-red-300 text-lg">{userInfo.subscriptionEndDate}</p>
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
          <Link 
            href="/community"
            className="bg-red-600 hover:bg-red-700 text-white px-6 py-3 rounded-full flex items-center justify-center transition-colors duration-300"
          >
            <Users className="w-5 h-5 mr-2" />
            Присоединиться к сообществу
          </Link>
        </div>

        <div className="bg-gradient-to-r from-red-900 to-black p-6 rounded-lg shadow-lg border border-red-800">
          <h2 className="text-2xl font-semibold text-red-400 mb-4">Часто задаваемые вопросы</h2>
          <div className="space-y-4">
            {faqItems.map((item, index) => (
              <div key={index} className="border-b border-red-800 pb-4 last:border-b-0 last:pb-0">
                <h3 className="text-lg font-medium text-red-300 flex items-center">
                  <HelpCircle className="w-5 h-5 mr-2 text-red-500" />
                  {item.question}
                </h3>
                <p className="mt-2 text-red-400">{item.answer}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-gradient-to-r from-red-900 to-black p-6 rounded-lg shadow-lg border border-red-800">
          <h2 className="text-2xl font-semibold text-red-400 mb-4">Скачать RubyTunnel</h2>
          <div className="grid grid-cols-2 gap-4">
            {downloadLinks.map((item, index) => (
              <Link 
                key={index} 
                href={item.link}
                className="flex items-center justify-center bg-red-800 hover:bg-red-700 text-white p-3 rounded-md transition-colors duration-300"
              >
                <item.icon className="w-6 h-6 mr-2" />
                <span>{item.name}</span>
              </Link>
            ))}
          </div>
        </div>
      </div>
    </Layout>
  )
} 