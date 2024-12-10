'use client'

import React, { useState } from 'react'
import Layout from '../components/Layout'
import { Users, Copy, CheckCircle, Gift } from 'lucide-react'

export default function Referrals() {
  const [referralCode] = useState('RUBY123')
  const [referralCount] = useState(5)
  const [copied, setCopied] = useState(false)
  const [inputCode, setInputCode] = useState('')

  const copyReferralCode = () => {
    navigator.clipboard.writeText(referralCode)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const submitReferralCode = (e) => {
    e.preventDefault()
    console.log('Отправлен реферальный код:', inputCode)
    setInputCode('')
  }

  return (
    <Layout currentPage="referrals">
      <div className="space-y-8">
        <h1 className="text-3xl font-bold text-red-500 text-center">Рубиновая Реферальная Сеть</h1>
        <div className="bg-gradient-to-r from-red-900 to-black p-6 rounded-lg shadow-lg border border-red-800">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-2xl font-semibold text-red-400">Ваша Коллекция Драгоценностей</h2>
            <div className="bg-red-700 rounded-full p-2">
              <Users className="w-6 h-6 text-white" />
            </div>
          </div>
          <p className="text-4xl font-bold text-red-300">{referralCount}</p>
          <p className="text-red-400 mt-2">Драгоценных камней в вашей сети</p>
        </div>
        <div className="bg-gradient-to-r from-red-900 to-black p-6 rounded-lg shadow-lg border border-red-800">
          <h2 className="text-2xl font-semibold text-red-400 mb-4">Ваш Рубиновый Код</h2>
          <div className="flex items-center space-x-2">
            <input
              type="text"
              value={referralCode}
              readOnly
              className="bg-black text-red-300 px-4 py-2 rounded-l-md flex-grow"
            />
            <button
              onClick={copyReferralCode}
              className="bg-red-700 text-white p-2 rounded-r-md hover:bg-red-600 transition-colors"
            >
              {copied ? <CheckCircle className="w-6 h-6" /> : <Copy className="w-6 h-6" />}
            </button>
          </div>
        </div>
        <form onSubmit={submitReferralCode} className="bg-gradient-to-r from-red-900 to-black p-6 rounded-lg shadow-lg border border-red-800">
          <h2 className="text-2xl font-semibold text-red-400 mb-4">Добавьте Драгоценность в Свою Коллекцию</h2>
          <div className="flex items-center space-x-2">
            <input
              type="text"
              value={inputCode}
              onChange={(e) => setInputCode(e.target.value)}
              placeholder="Введите рубиновый код"
              className="bg-black text-red-300 px-4 py-2 rounded-l-md flex-grow"
            />
            <button
              type="submit"
              className="bg-red-700 text-white px-4 py-2 rounded-r-md hover:bg-red-600 transition-colors"
            >
              <Gift className="w-6 h-6" />
            </button>
          </div>
        </form>
      </div>
    </Layout>
  )
}

