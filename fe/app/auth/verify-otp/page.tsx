'use client';

import { useState, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { apiCall } from '@/lib/api';

function VerifyOtpForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const email = searchParams.get('email') || '';
  
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await apiCall(`/users/auth/verify-otp?email=${encodeURIComponent(email)}&code=${code}`, {
        method: 'POST',
      });
      setSuccess('Account activated! Redirecting to login...');
      setTimeout(() => router.push('/auth/login'), 2000);
    } catch (err: any) {
      setError(err.message || 'Verification failed. Invalid or expired code.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="glass-card w-full max-w-md p-8 animate-fade-in">
      <div className="text-center mb-8">
        <h1 className="text-3xl premium-gradient-text">Verify Email</h1>
        <p className="text-secondary mt-2">
          We've sent a 6-digit code to <br />
          <span className="text-foreground font-medium">{email}</span>
        </p>
      </div>

      {error && (
        <div className="bg-red-50 text-red-500 p-3 rounded-lg mb-6 text-sm border border-red-100 text-center">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-50 text-green-600 p-3 rounded-lg mb-6 text-sm border border-green-100 text-center font-medium">
          {success}
        </div>
      )}

      <form onSubmit={handleVerify} className="space-y-6">
        <div className="flex justify-center">
          <input
            type="text"
            maxLength={6}
            placeholder="000000"
            className="input-field text-center text-3xl tracking-[0.5em] font-mono w-48 py-4"
            required
            autoFocus
            onChange={(e) => setCode(e.target.value)}
          />
        </div>

        <button
          type="submit"
          disabled={loading || success !== ''}
          className="btn-primary w-full"
        >
          {loading ? 'Verifying...' : 'Verify & Activate'}
        </button>
      </form>

      <div className="text-center mt-8">
        <button 
          onClick={() => alert('OTP resent! (Mock)')}
          className="text-primary text-sm font-semibold hover:underline"
        >
          Resend Code
        </button>
      </div>
    </div>
  );
}

export default function VerifyOtpPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4">
      <Suspense fallback={<div className="text-secondary font-medium italic">Preparing verification...</div>}>
        <VerifyOtpForm />
      </Suspense>
    </div>
  );
}
