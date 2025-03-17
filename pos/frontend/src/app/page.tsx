'use client';

import { useAuth } from '@/context/AuthContext';
import styles from "./page.module.css";
import 'bootstrap/dist/css/bootstrap.min.css';
import Link from 'next/link';

export default function Home() {
  const { isAuthenticated, role } = useAuth();

  if (!isAuthenticated) {
    return (
      <div className={styles.page}>
        <div className={styles.hero}>
          <h1 className={`${styles.title} display-4`}>Welcome to SmartPOS</h1>
          <p className={`${styles.subtitle} lead`}>Please Login to continue</p>
          <Link href="/login" className={styles.signupButton}>
            Login Now
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.page}>
      <div className={styles.hero}>
        <h1 className={`${styles.title} display-4`}>Welcome to SmartPOS</h1>
        <p className={`${styles.subtitle} lead`}>
          Hello {role}! You are logged in as a {role === 'supervisor' ? 'Supervisor' : 'Operator'}
        </p>
      </div>
      
      <div className={`${styles.features} row justify-content-center`}>
        <div className={`col-md-4 ${styles.featureCard}`}>
          <div className={styles.cardContent}>
            <i className="fas fa-chart-line" style={{ fontSize: '2.5rem', color: '#4CAF50' }}></i>
            <h3>Easy Sales Management</h3>
            <p>Process transactions quickly and efficiently with our intuitive interface</p>
          </div>
        </div>
        <div className={`col-md-4 ${styles.featureCard}`}>
          <div className={styles.cardContent}>
            <i className="fas fa-warehouse" style={{ fontSize: '2.5rem', color: '#2196F3' }}></i>
            <h3>Inventory Tracking</h3>
            <p>Real-time inventory management to keep your stock organized</p>
          </div>
        </div>
        <div className={`col-md-4 ${styles.featureCard}`}>
          <div className={styles.cardContent}>
            <i className="fas fa-file-alt" style={{ fontSize: '2.5rem', color: '#FF9800' }}></i>
            <h3>Reports & Analytics</h3>
            <p>Gain valuable insights with detailed sales and performance reports</p>
          </div>
        </div>
      </div>
    </div>
  );
}
