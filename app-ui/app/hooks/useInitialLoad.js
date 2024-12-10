import { useState, useEffect } from 'react'

export function useInitialLoad() {
  const [isInitialLoad, setIsInitialLoad] = useState(true)

  useEffect(() => {
    setIsInitialLoad(false)
  }, [])

  return isInitialLoad
} 