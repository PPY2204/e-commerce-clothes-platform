'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { apiCall } from '@/lib/api';

export default function LoginPage() {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await apiCall('/users/auth/login', {
        method: 'POST',
        body: JSON.stringify(formData),
      });

      // Store token (Simple approach for MVP)
      if (typeof window !== 'undefined') {
        localStorage.setItem('auth_token', response.data.token);
        localStorage.setItem('user_session', JSON.stringify(response.data));
      }

      router.push('/shop');
    } catch (err: any) {
      setError(err.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4">
      <div className="glass-card w-full max-w-md p-8 animate-fade-in">
        <div className="text-center mb-8">
          <h1 className="text-3xl premium-gradient-text">Welcome Back</h1>
          <p className="text-secondary mt-2">Enter your credentials to access the club.</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-500 p-3 rounded-lg mb-6 text-sm border border-red-100 text-center">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="email"
            placeholder="Email Address"
            className="input-field"
            required
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
          <div className="space-y-1">
            <input
              type="password"
              placeholder="Password"
              className="input-field"
              required
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            />
            <div className="text-right">
              <Link href="/auth/forgot-password" virtual-id="forgot-password-link" className="text-xs text-secondary hover:text-primary">
                Forgot password?
              </Link>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full mt-2"
          >
            {loading ? 'Authenticating...' : 'Sign In'}
          </button>
        </form>

        <p className="text-center mt-6 text-sm text-secondary">
          Don't have an account?{' '}
          <Link href="/auth/register" className="text-primary font-semibold hover:underline">
            Register now
          </Link>
        </p>
      </div>
    </div>
  );
}
