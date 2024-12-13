import React from 'react'
import Link from 'next/link'
import Image from 'next/image'
import { HelpCircle, MessageCircle } from 'lucide-react'

export default function Layout({ children, currentPage }) {
  const navItems = [
    { icon: Home, label: 'Главная', href: '/' },
    { icon: CreditCard, label: 'Оплата', href: '/payment' },
    { icon: User, label: 'Профиль', href: '/profile' },
    { icon: Users, label: 'Рефералы', href: '/referrals' },
  ]

  return (
      <div className="flex flex-col min-h-screen bg-gradient-to-b from-black via-red-950 to-black text-red-500">
        <header className="bg-black bg-opacity-50 backdrop-blur-md sticky top-0 z-10">
          <div className="max-w-4xl mx-auto px-4 py-3 flex justify-between items-center">
            <Link href="/">
              <Image
                  src="/logo.png"
                  alt="RubyTunnel Logo"
                  width={40} // Укажите размер логотипа для меню
                  height={40}
                  className="rounded-full"
              />
            </Link>
            <div className="flex items-center space-x-4">
              <Link href="/guide" className="text-red-400 hover:text-red-300 transition-colors">
                <HelpCircle className="w-6 h-6" />
              </Link>
              <button className="w-10 h-10 bg-red-600 rounded-full flex items-center justify-center animate-pulse">
                <MessageCircle className="w-6 h-6 text-black" />
              </button>
            </div>
          </div>
        </header>
        <main className="flex-grow">
          <div className="max-w-4xl mx-auto px-4 py-6">
            {children}
          </div>
        </main>
        <nav className="bg-black bg-opacity-50 backdrop-blur-md sticky bottom-0 z-10">
          <div className="max-w-4xl mx-auto px-4 py-3 flex justify-around items-center">
            {navItems.map(({ icon: Icon, label, href }) => (
                <Link
                    key={label}
                    href={href}
                    className={`flex flex-col items-center ${
                        currentPage === label.toLowerCase() ? 'text-red-500' : 'text-gray-500'
                    } hover:text-red-400 transition-colors`}
                >
                  <Icon className="w-6 h-6" />
                  <span className="text-xs mt-1">{label}</span>
                </Link>
            ))}
          </div>
        </nav>
      </div>
  )
}
