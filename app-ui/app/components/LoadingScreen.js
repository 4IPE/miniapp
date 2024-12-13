import React from 'react'
import Image from 'next/image'

export default function LoadingScreen() {
    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black z-[9999]">
            <div className="text-center">
                <Image
                    src="/logo.png"
                    alt="RubyTunnel Logo"
                    width={120}
                    height={120}
                    className="animate-pulse mx-auto"
                    priority
                />
                <h1 className="text-3xl font-bold text-red-500 animate-pulse mt-4">RubyTunnel</h1>
            </div>
        </div>
    )
}
