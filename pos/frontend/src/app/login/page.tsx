'use client';

import { useState } from 'react';
import { useAuth } from '@/context/AuthContext';

export default function Signup() {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    try {
      await login(email);
    } catch (err) {
      setError('Signup failed. Please try again.');
    }
  };

  return (
    <div className="container-fluid min-vh-100 d-flex align-items-center justify-content-center bg-gradient bg-primary bg-opacity-10">
      <div className="card shadow-lg p-4 rounded-4" style={{ maxWidth: '450px' }}>
        <div className="text-center mb-4">
          <h1 className="h2 fw-bold text-primary mb-2">Login into POS</h1>
        </div>

        <form onSubmit={handleSubmit}>
          {error && (
            <div className="alert alert-danger alert-dismissible fade show" role="alert">
              {error}
            </div>
          )}

          <div className="mb-4">
            <label htmlFor="email" className="form-label fw-semibold">
              Email Address
            </label>
            <div className="input-group">
              <input
                type="email"
                className="form-control form-control-lg bg-light"
                id="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="Enter your email"
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary btn-lg w-100 mb-3 shadow-sm"
          >
            Login
          </button>
          
        </form>
      </div>
    </div>
  );
} 