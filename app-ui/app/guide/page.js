'use client'

import React from 'react'
import Layout from '../components/Layout'
import { CheckCircle, Download, Lock, Zap, Shield, Power } from 'lucide-react'

export default function Guide() {
  const steps = [
    { icon: Download, text: 'Скачайте приложение RubyTunnel', description: 'Извлеките наше драгоценное VPN-приложение из цифрового карьера.' },
    { icon: Zap, text: 'Установите на ваше устройство', description: 'Отшлифуйте необработанный камень быстрой и безупречной установкой.' },
    { icon: Lock, text: 'Защитите хранилище', description: 'Охраняйте свои цифровые сокровища непроницаемыми учетными данными.' },
    { icon: Shield, text: 'Настройте RubyTunnel', description: 'Аккуратно поместите свой драгоценный камень в безопасную, персонализированную оправу.' },
    { icon: Power, text: 'Активируйте Рубиновый Щит', description: 'Раскройте полное великолепие вашей VPN-защиты.' },
  ]

  return (
    <Layout currentPage="guide">
      <div className="space-y-8">
        <h1 className="text-3xl font-bold text-red-500 text-center">Создание Вашего Цифрового Драгоценного Камня</h1>
        <div className="space-y-6">
          {steps.map((step, index) => (
            <div key={index} className="bg-gradient-to-r from-red-900 to-black p-4 rounded-lg shadow-lg border border-red-800 hover:border-red-600 transition-all duration-300 transform hover:scale-105">
              <div className="flex items-start space-x-4">
                <div className="bg-red-700 rounded-full p-2 flex-shrink-0 animate-pulse">
                  <step.icon className="w-6 h-6 text-white" />
                </div>
                <div className="flex-grow">
                  <h2 className="text-xl font-semibold text-red-400 mb-2">{step.text}</h2>
                  <p className="text-red-300">{step.description}</p>
                </div>
                <CheckCircle className="w-6 h-6 text-green-500 flex-shrink-0" />
              </div>
              <div className="mt-4 overflow-hidden h-8">
                <p className="text-red-300 text-sm animate-float">Раскрывая блеск безопасного просмотра...</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  )
} 