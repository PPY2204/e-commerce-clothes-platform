'use client';

import { useState } from 'react';
import { useCart } from '@/context/CartContext';
import { useRouter } from 'next/navigation';
import { apiCall } from '@/lib/api';

export default function CheckoutPage() {
  const { items, totalPrice, clearCart } = useCart();
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [orderId, setOrderId] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    address: '',
    city: '',
    phone: '',
    paymentMethod: 'CREDIT_CARD'
  });

  const handleCheckout = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      // 1. Create Order
      const userSession = JSON.parse(localStorage.getItem('user_session') || '{}');
      const orderResponse = await apiCall('/orders', {
        method: 'POST',
        body: JSON.stringify({
          userId: userSession.id || 'anonymous',
          items: items.map(i => ({ productId: i.id, quantity: i.quantity, price: i.price })),
          totalAmount: totalPrice,
          shippingAddress: `${formData.address}, ${formData.city}`,
          phoneNumber: formData.phone
        }),
      });

      // 2. Process Payment (Mock)
      await apiCall('/payments', {
        method: 'POST',
        body: JSON.stringify({
          orderId: orderResponse.data.id,
          userId: userSession.id || 'anonymous',
          amount: totalPrice,
          paymentMethod: formData.paymentMethod
        }),
      });

      setOrderId(orderResponse.data.id);
      clearCart();
      setTimeout(() => router.push('/shop'), 3000);
    } catch (err) {
      alert('Checkout failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (orderId) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="glass-card p-12 text-center animate-fade-in max-w-lg">
          <div className="bg-green-100 text-green-600 h-20 w-20 rounded-full flex items-center justify-center mx-auto mb-6">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h1 className="text-3xl font-bold text-slate-900 mb-2">Order Confirmed!</h1>
          <p className="text-secondary mb-6 italic">Order ID: #{orderId}</p>
          <p className="text-slate-600 mb-8">Thank you for your purchase. We've sent a confirmation to your email. Redirecting you back to the shop...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 pt-28 pb-12 px-4">
      <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-12">
        {/* Checkout Form */}
        <div className="animate-fade-in">
          <h1 className="text-3xl font-black italic premium-gradient-text mb-8">Secure Checkout</h1>
          <form onSubmit={handleCheckout} className="glass-card p-8 space-y-6">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-secondary">First Name</label>
                <input required className="input-field" onChange={e => setFormData({...formData, firstName: e.target.value})} />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-secondary">Last Name</label>
                <input required className="input-field" onChange={e => setFormData({...formData, lastName: e.target.value})} />
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-bold uppercase tracking-widest text-secondary">Shipping Address</label>
              <input required className="input-field" placeholder="House number and street name" onChange={e => setFormData({...formData, address: e.target.value})} />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-secondary">City</label>
                <input required className="input-field" onChange={e => setFormData({...formData, city: e.target.value})} />
              </div>
              <div className="space-y-2">
                <label className="text-xs font-bold uppercase tracking-widest text-secondary">Phone</label>
                <input required type="tel" className="input-field" onChange={e => setFormData({...formData, phone: e.target.value})} />
              </div>
            </div>

            <div className="pt-6 border-t border-slate-200">
              <h3 className="font-bold text-slate-800 mb-4">Payment Method</h3>
              <div className="grid grid-cols-2 gap-4">
                <label className={`cursor-pointer p-4 border rounded-xl flex flex-col items-center transition-all ${formData.paymentMethod === 'CREDIT_CARD' ? 'border-primary bg-blue-50' : 'border-slate-200'}`}>
                  <input type="radio" className="hidden" name="payment" checked={formData.paymentMethod === 'CREDIT_CARD'} onChange={() => setFormData({...formData, paymentMethod: 'CREDIT_CARD'})} />
                  <span className="text-xs font-bold uppercase">Credit Card</span>
                </label>
                <label className={`cursor-pointer p-4 border rounded-xl flex flex-col items-center transition-all ${formData.paymentMethod === 'CRYPTO' ? 'border-primary bg-blue-50' : 'border-slate-200'}`}>
                  <input type="radio" className="hidden" name="payment" checked={formData.paymentMethod === 'CRYPTO'} onChange={() => setFormData({...formData, paymentMethod: 'CRYPTO'})} />
                  <span className="text-xs font-bold uppercase">Crypto</span>
                </label>
              </div>
            </div>

            <button type="submit" disabled={loading} className="btn-primary w-full py-4 text-lg">
              {loading ? 'Processing Order...' : `Pay $${totalPrice.toFixed(2)} Now`}
            </button>
          </form>
        </div>

        {/* Order Preview */}
        <div className="hidden lg:block">
          <div className="glass-card p-8 sticky top-32">
            <h2 className="text-xl font-bold text-slate-900 mb-6">Your Items</h2>
            <div className="space-y-4 max-h-[400px] overflow-y-auto pr-2 custom-scrollbar">
              {items.map(item => (
                <div key={item.id} className="flex items-center space-x-4">
                  <div className="h-16 w-12 bg-slate-100 rounded overflow-hidden flex-shrink-0">
                    <img src={item.image} className="h-full w-full object-cover" />
                  </div>
                  <div className="flex-grow">
                    <p className="text-sm font-bold text-slate-800">{item.name}</p>
                    <p className="text-xs text-secondary">Qty: {item.quantity}</p>
                  </div>
                  <p className="text-sm font-mono font-bold">${(item.price * item.quantity).toFixed(2)}</p>
                </div>
              ))}
            </div>
            <div className="mt-8 pt-8 border-t border-slate-200">
               <div className="flex justify-between items-center text-lg font-bold">
                 <span>Subtotal</span>
                 <span className="premium-gradient-text">${totalPrice.toFixed(2)}</span>
               </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
