import React from 'react'
import Image from 'next/image'

export default function LoadingScreen() {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black z-[9999]">
      <div className="text-center">
        <div className="w-32 h-32 bg-gradient-to-br from-red-600 to-red-900 rounded-full mx-auto mb-4 flex items-center justify-center animate-pulse">
          <Image src="/logo.png" alt="RubyTunnel Logo" width={64} height={64} priority />
        </div>
        <h1 className="text-3xl font-bold text-red-500 animate-pulse">RubyTunnel</h1>
        <p className="mt-4 text-red-300">Загрузка приложения...</p>
      </div>
    </div>
  )
} 