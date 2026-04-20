'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { apiCall } from '@/lib/api';

export default function RegisterPage() {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phone: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await apiCall('/users/auth/register', {
        method: 'POST',
        body: JSON.stringify(formData),
      });
      // Redirect to OTP verification page
      router.push(`/auth/verify-otp?email=${encodeURIComponent(formData.email)}`);
    } catch (err: any) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4">
      <div className="glass-card w-full max-w-md p-8 animate-fade-in">
        <div className="text-center mb-8">
          <h1 className="text-3xl premium-gradient-text">Create Account</h1>
          <p className="text-secondary mt-2">Join the YAMATEE club today.</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-500 p-3 rounded-lg mb-6 text-sm border border-red-100">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <input
              type="text"
              placeholder="First Name"
              className="input-field"
              required
              onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
            />
            <input
              type="text"
              placeholder="Last Name"
              className="input-field"
              required
              onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
            />
          </div>
          <input
            type="email"
            placeholder="Email Address"
            className="input-field"
            required
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          />
          <input
            type="tel"
            placeholder="Phone Number"
            className="input-field"
            onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
          />
          <input
            type="password"
            placeholder="Password"
            className="input-field"
            required
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
          />

          <button
            type="submit"
            disabled={loading}
            className="btn-primary w-full mt-4"
          >
            {loading ? 'Creating Account...' : 'Register Now'}
          </button>
        </form>

        <p className="text-center mt-6 text-sm text-secondary">
          Already have an account?{' '}
          <Link href="/auth/login" className="text-primary font-semibold hover:underline">
            Log in
          </Link>
        </p>
      </div>
    </div>
  );
}
