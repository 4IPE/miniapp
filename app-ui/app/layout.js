import './globals.css'
import React from 'react'
import { Preloader } from './components/Preloader'
import { UserProvider } from './contexts/UserContext'

export const metadata = {
  title: 'RubyTunnel VPN',
  description: 'Secure your digital presence with next-gen cryptographic VPN technology.',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <meta charSet="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
      </head>
      <body suppressHydrationWarning>
        <UserProvider>
          <Preloader>{children}</Preloader>
        </UserProvider>
      </body>
    </html>
  )
} 