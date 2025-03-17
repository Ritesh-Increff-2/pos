"use client";
import React, { useState } from 'react';
import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';
import { usePathname } from 'next/navigation';
import '../styles/globals.css';

const Navbar: React.FC = () => {
    const { isAuthenticated, logout } = useAuth();
    const pathname = usePathname();
    const [isOpen, setIsOpen] = useState(false);

    const toggleMenu = () => {
        setIsOpen(!isOpen);
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark text-white fixed-top">
            <div className="container-fluid">
                <Link href="/" className="navbar-brand text-white">Point Of Sale(POS)</Link>
                
                {isAuthenticated && (
                    <>
                        <button 
                            className="navbar-toggler" 
                            type="button" 
                            onClick={toggleMenu}
                            aria-controls="navbarNav" 
                            aria-expanded={isOpen} 
                            aria-label="Toggle navigation"
                        >
                            <span className="navbar-toggler-icon"></span>
                        </button>

                        <div className={`collapse navbar-collapse ${isOpen ? 'show' : ''}`} id="navbarNav">
                            <div className="navbar-nav me-auto">
                                <Link 
                                    href="/clients" 
                                    className="nav-link text-white nav-item"
                                    aria-current={pathname === '/clients' ? 'page' : undefined}
                                    onClick={() => setIsOpen(false)}
                                    style={{
                                        transition: 'color 0.3s ease',
                                    }}
                                >Clients</Link>
                                <Link 
                                    href="/products" 
                                    className="nav-link text-white nav-item"
                                    aria-current={pathname === '/products' ? 'page' : undefined}
                                    onClick={() => setIsOpen(false)}
                                    style={{
                                        transition: 'color 0.3s ease',
                                    }}
                                >Products</Link>
                                <Link 
                                    href="/manageOrder" 
                                    className="nav-link text-white nav-item"
                                    aria-current={pathname === '/manageOrder' ? 'page' : undefined}
                                    onClick={() => setIsOpen(false)}
                                    style={{
                                        transition: 'color 0.3s ease',
                                    }}
                                >Manage Orders</Link>
                                <Link 
                                    href="/salesReport" 
                                    className="nav-link text-white nav-item"
                                    aria-current={pathname === '/salesReport' ? 'page' : undefined}
                                    onClick={() => setIsOpen(false)}
                                    style={{
                                        transition: 'color 0.3s ease',
                                    }}
                                >Sales Report</Link>
                                <Link 
                                    href="/dailySalesReport" 
                                    className="nav-link text-white nav-item"
                                    aria-current={pathname === '/dailySalesReport' ? 'page' : undefined}
                                    onClick={() => setIsOpen(false)}
                                    style={{
                                        transition: 'color 0.3s ease',
                                    }}
                                >Daily Sales Report</Link>
                            </div>
                            <button 
                                onClick={() => {
                                    setIsOpen(false);
                                    logout();
                                }} 
                                className="btn btn-danger"
                            >
                                Logout
                            </button>
                        </div>
                    </>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
