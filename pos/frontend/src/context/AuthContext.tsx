"use client";
import { createContext, useContext, useEffect, useState } from 'react';

interface AuthContextType {
  userId: string | null;
  role: string | null;
  login: (email: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [userId, setUserId] = useState<string | null>(null);
  const [role, setRole] = useState<string | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);

  const checkAuth = () => {
    const storedUserId = sessionStorage.getItem('userId');
    const storedRole = sessionStorage.getItem('role');
    const storedToken = sessionStorage.getItem('token');

    if (storedUserId && storedRole && storedToken) {
      setUserId(storedUserId);
      setRole(storedRole);
      setToken(storedToken);
      setIsAuthenticated(true);
    } else {
      // Clear everything if any of the required items are missing
      setUserId(null);
      setRole(null);
      setToken(null);
      setIsAuthenticated(false);
    }
  };

  useEffect(() => {
    checkAuth();
  }, []);

  const login = async (email: string) => {
    try {
      console.log('Sending login request with:', { email });
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        body: JSON.stringify({ email }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Login failed');
      }

      const data = await response.json();
      setUserId(data.email);
      setRole(data.role);
      setToken(data.token);
      setIsAuthenticated(true);
      
      sessionStorage.setItem('userId', data.email);
      sessionStorage.setItem('role', data.role);
      sessionStorage.setItem('token', data.token);
      
      window.location.href = '/';
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  };

  const logout = () => {
    setUserId(null);
    setRole(null);
    setToken(null);
    setIsAuthenticated(false);
    
    sessionStorage.removeItem('userId');
    sessionStorage.removeItem('role');
    sessionStorage.removeItem('token');
    
    window.location.href = '/login';
  };

  return (
    <AuthContext.Provider value={{ 
      userId, 
      role, 
      login, 
      logout,
      isAuthenticated
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
} 