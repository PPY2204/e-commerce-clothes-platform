'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { apiCall } from '@/lib/api';

export default function ForgotPasswordPage() {
  const [step, setStep] = useState(1); // 1: Request, 2: Reset
  const [email, setEmail] = useState('');
  const [code, setCode] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const router = useRouter();

  const handleRequest = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await apiCall(`/users/auth/forgot-password?email=${encodeURIComponent(email)}`, {
        method: 'POST',
      });
      setStep(2);
    } catch (err: any) {
      setError(err.message || 'Failed to send reset code.');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await apiCall(`/users/auth/reset-password?email=${encodeURIComponent(email)}&code=${code}&newPassword=${encodeURIComponent(newPassword)}`, {
        method: 'POST',
      });
      setSuccess('Password updated! Redirecting to login...');
      setTimeout(() => router.push('/auth/login'), 2000);
    } catch (err: any) {
      setError(err.message || 'Reset failed. Invalid code.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4">
      <div className="glass-card w-full max-w-md p-8 animate-fade-in">
        <div className="text-center mb-8">
          <h1 className="text-3xl premium-gradient-text">
            {step === 1 ? 'Reset Password' : 'New Credentials'}
          </h1>
          <p className="text-secondary mt-2">
            {step === 1 
              ? "Enter your email to receive a recovery code." 
              : "Enter the code we sent and choose a new password."}
          </p>
        </div>

        {error && <div className="bg-red-50 text-red-500 p-3 rounded-lg mb-6 text-sm border border-red-100 text-center">{error}</div>}
        {success && <div className="bg-green-50 text-green-600 p-3 rounded-lg mb-6 text-sm border border-green-100 text-center font-medium">{success}</div>}

        {step === 1 ? (
          <form onSubmit={handleRequest} className="space-y-4">
            <input
              type="email"
              placeholder="Email Address"
              className="input-field"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <button type="submit" disabled={loading} className="btn-primary w-full mt-2">
              {loading ? 'Sending...' : 'Send Recovery Code'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleReset} className="space-y-4">
            <input
              type="text"
              placeholder="6-Digit Code"
              className="input-field text-center font-mono tracking-widest"
              required
              onChange={(e) => setCode(e.target.value)}
            />
            <input
              type="password"
              placeholder="New Secure Password"
              className="input-field"
              required
              onChange={(e) => setNewPassword(e.target.value)}
            />
            <button type="submit" disabled={loading || success !== ''} className="btn-primary w-full mt-2">
              {loading ? 'Updating...' : 'Reset Password'}
            </button>
          </form>
        )}

        <div className="text-center mt-6">
          <Link href="/auth/login" className="text-sm text-secondary hover:text-primary transition-colors">
            Back to Sign In
          </Link>
        </div>
      </div>
    </div>
  );
}
