'use client';

import Link from 'next/link';
import { useCart } from '@/context/CartContext';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';

export default function Navbar() {
  const { totalItems } = useCart();
  const [user, setUser] = useState<any>(null);
  const router = useRouter();

  useEffect(() => {
    const session = localStorage.getItem('user_session');
    if (session) setUser(JSON.parse(session));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_session');
    setUser(null);
    router.push('/auth/login');
  };

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-white/70 backdrop-blur-xl border-b border-white/20 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-20">
          <div className="flex items-center space-x-12">
            <Link href="/" className="text-2xl font-black italic premium-gradient-text tracking-tighter">
              YAMATEE
            </Link>
            <div className="hidden md:flex space-x-8 text-sm font-semibold uppercase tracking-widest text-slate-600">
              <Link href="/shop" className="hover:text-primary transition-colors">Shop</Link>
              <Link href="/collections" className="hover:text-primary transition-colors">Collections</Link>
              <Link href="/about" className="hover:text-primary transition-colors">About</Link>
            </div>
          </div>

          <div className="flex items-center space-x-6">
            <Link href="/cart" className="relative p-2 group">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-slate-700 group-hover:text-primary transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              {totalItems > 0 && (
                <span className="absolute top-0 right-0 bg-primary text-white text-[10px] font-bold h-5 w-5 rounded-full flex items-center justify-center border-2 border-white animate-fade-in">
                  {totalItems}
                </span>
              )}
            </Link>

            {user ? (
              <div className="flex items-center space-x-4">
                <span className="hidden sm:block text-sm font-medium text-slate-700">
                  Hi, {user.firstName || 'User'}
                </span>
                <button 
                  onClick={handleLogout}
                  className="text-xs font-bold uppercase tracking-wider text-slate-400 hover:text-red-500 transition-colors"
                >
                  Logout
                </button>
              </div>
            ) : (
              <Link href="/auth/login" className="btn-primary py-2 px-5 text-xs">
                Join Now
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
