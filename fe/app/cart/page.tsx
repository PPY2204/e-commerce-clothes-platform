'use client';

import Link from 'next/link';
import { useCart } from '@/context/CartContext';
import Image from 'next/image';

export default function CartPage() {
  const { items, updateQuantity, removeItem, totalPrice, totalItems } = useCart();

  if (items.length === 0) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50 p-4">
        <div className="text-center animate-fade-in">
          <div className="bg-slate-200 h-32 w-32 rounded-full flex items-center justify-center mx-auto mb-6">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-16 w-16 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
          <h1 className="text-3xl font-bold text-slate-900 mb-2">Your cart is empty</h1>
          <p className="text-secondary mb-8">Looks like you haven't added anything to your cart yet.</p>
          <Link href="/shop" className="btn-primary">
            Browse Collections
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 pt-28 pb-12 px-4 shadow-inner">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-black italic premium-gradient-text mb-12">Your Shopping Bag ({totalItems})</h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
          {/* Cart Items List */}
          <div className="lg:col-span-2 space-y-6">
            {items.map((item) => (
              <div key={item.id} className="glass-card p-6 flex items-center space-x-6 animate-fade-in hover:shadow-lg transition-shadow">
                <div className="h-32 w-24 flex-shrink-0 bg-slate-200 rounded-xl overflow-hidden">
                  <img src={item.image} alt={item.name} className="h-full w-full object-cover" />
                </div>
                
                <div className="flex-grow">
                  <div className="flex justify-between items-start">
                    <div>
                      <h2 className="text-lg font-bold text-slate-900">{item.name}</h2>
                      <p className="text-sm text-secondary font-mono mt-1">${item.price}</p>
                    </div>
                    <button 
                      onClick={() => removeItem(item.id)}
                      className="text-slate-400 hover:text-red-500 transition-colors"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>

                  <div className="flex items-center justify-between mt-6">
                    <div className="flex items-center border border-slate-200 rounded-lg overflow-hidden bg-white/50">
                      <button 
                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                        className="px-3 py-1 hover:bg-slate-100 transition-colors"
                      >
                        -
                      </button>
                      <span className="px-4 py-1 text-sm font-bold border-x border-slate-200 min-w-[40px] text-center">
                        {item.quantity}
                      </span>
                      <button 
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                        className="px-3 py-1 hover:bg-slate-100 transition-colors"
                      >
                        +
                      </button>
                    </div>
                    <p className="font-bold text-slate-900">${(item.price * item.quantity).toFixed(2)}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Summary Sidebar */}
          <div className="lg:col-span-1">
            <div className="glass-card p-8 sticky top-32">
              <h2 className="text-xl font-bold text-slate-900 mb-6">Order Summary</h2>
              
              <div className="space-y-4 text-sm mb-6 border-b border-slate-200 pb-6">
                <div className="flex justify-between text-secondary">
                  <span>Subtotal</span>
                  <span className="text-slate-900 font-medium">${totalPrice.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-secondary">
                  <span>Shipping</span>
                  <span className="text-green-600 font-bold uppercase tracking-widest text-xs">Free</span>
                </div>
                <div className="flex justify-between text-secondary">
                  <span>System Tax</span>
                  <span className="text-slate-900 font-medium">$0.00</span>
                </div>
              </div>

              <div className="flex justify-between items-center mb-8">
                <span className="text-lg font-bold text-slate-900">Total</span>
                <span className="text-2xl font-black premium-gradient-text">${totalPrice.toFixed(2)}</span>
              </div>

              <Link href="/checkout" className="btn-primary w-full block text-center py-4 mb-4">
                Proceed to Checkout
              </Link>
              
              <p className="text-[10px] text-center text-secondary uppercase tracking-[0.2em]">
                Secure payment powered by YAMATEE Cloud
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
