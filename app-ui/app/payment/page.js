'use client'

import React from 'react'
import Layout from '../components/Layout'
import { CreditCard, Zap } from 'lucide-react'

export default function Payment() {
  const handlePayment = () => {
    console.log('Инициализация оплаты через YooMoney')
  }

  return (
    <Layout currentPage="payment">
      <div className="space-y-12 py-8">
        <h1 className="text-4xl font-bold text-red-500 text-center bg-clip-text text-transparent bg-gradient-to-r from-red-500 to-orange-500">
          Доступ к Квантовому Хранилищу
        </h1>
        <div className="max-w-md mx-auto bg-gradient-to-br from-red-900 via-red-800 to-black p-8 rounded-2xl shadow-lg border border-red-700 hover:border-red-500 transition-all duration-300 transform hover:scale-105">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-semibold text-red-300">YooMoney Шлюз</h2>
            <div className="bg-red-600 rounded-full p-3 animate-pulse">
              <CreditCard className="w-6 h-6 text-white" />
            </div>
          </div>
          <p className="text-red-200 mb-8">
            Защитите свой доступ к RubyTunnel с помощью нашего передового платежного портала. 
            Одноразовая активация для пожизненной цифровой защиты.
          </p>
          <div className="space-y-4">
            <div className="flex justify-between items-center text-red-300">
              <span>Квантовое Шифрование</span>
              <span>Включено</span>
            </div>
            <div className="flex justify-between items-center text-red-300">
              <span>Временной Файервол</span>
              <span>Включено</span>
            </div>
            <div className="flex justify-between items-center text-red-300">
              <span>Нейросетевая Защита</span>
              <span>Включено</span>
            </div>
          </div>
          <div className="mt-8">
            <button
              onClick={handlePayment}
              className="w-full bg-gradient-to-r from-yellow-400 via-red-500 to-pink-500 text-white py-3 px-6 rounded-full flex items-center justify-center space-x-2 hover:from-yellow-500 hover:via-red-600 hover:to-pink-600 transition-all duration-300 transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50"
            >
              <span className="text-lg font-semibold">Активировать через YooMoney</span>
              <Zap className="w-5 h-5" />
            </button>
          </div>
        </div>
      </div>
    </Layout>
  )
}