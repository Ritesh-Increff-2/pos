// import "../globals.css";
'use client'
import Navbar from "./components/NavBar";
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import { AuthProvider } from '@/context/AuthContext';

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>
        <AuthProvider>
          <Navbar />
          <main className="container mt-4">{children}</main>
        </AuthProvider>
      </body>
    </html>
  );
}
