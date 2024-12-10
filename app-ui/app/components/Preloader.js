'use client'

import React, { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import Script from 'next/script'
import LoadingScreen from './LoadingScreen'

const routesToPreload = ['/','/payment', '/profile', '/referrals', '/guide', '/']

export function Preloader({ children }) {
  const router = useRouter()
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    let mounted = true

    const initialize = async () => {
      try {
        for (const route of routesToPreload) {
          await router.prefetch(route)
        }
        
        setTimeout(() => {
          if (mounted) {
            setIsLoading(false)
          }
        }, 500)
      } catch (error) {
        console.error('Error during initialization:', error)
        if (mounted) {
          setIsLoading(false)
        }
      }
    }

    initialize()
    return () => { mounted = false }
  }, [router])

  if (isLoading) {
    return (
      <>
        <Script
          src="https://telegram.org/js/telegram-web-app.js"
          strategy="beforeInteractive"
          onLoad={() => {
            if (window.Telegram?.WebApp) {
              window.Telegram.WebApp.ready()
            }
          }}
        />
        <LoadingScreen />
      </>
    )
  }

  return <>{children}</>
} 