"use client"
import React, { useState } from 'react';
import axios from 'axios';
import { toast, Toaster } from 'react-hot-toast';
import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';

const toastConfig = {
    duration: Infinity,
    style: {
        padding: '16px',
        color: '#fff',
        borderRadius: '8px',
    },
};

interface DailySalesData {
    id: string;
    date: string;
    totalInvoicedOrders: number;
    totalItems: number;
    totalRevenue: number;
}
const successToast = (message: string) => {
    toast(
        (t: any) => (
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <span>{message}</span>
                <button 
                    onClick={() => toast.dismiss(t.id)}
                    style={{ 
                        background: 'none', 
                        border: 'none', 
                        color: 'white',
                        marginLeft: '10px',
                        cursor: 'pointer'
                    }}
                >
                    ✕
                </button>
            </div>
        ),
        {
            duration: 3000, // 3 seconds
            style: {
                background: '#22c55e',
                color: '#fff',
                padding: '16px',
                borderRadius: '8px',
            }
        }
    );
};

const warningToast = (message: string) => {
    toast(
        (t: any) => (
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <span>{message}</span>
                <button 
                    onClick={() => toast.dismiss(t.id)}
                    style={{ 
                        background: 'none', 
                        border: 'none', 
                        color: 'white',
                        marginLeft: '10px',
                        cursor: 'pointer'
                    }}
                >
                    ✕
                </button>
            </div>
        ),
        {
            ...toastConfig,
            style: {
                ...toastConfig.style,
                background: '#f59e0b',
            }
        }
    );
};

const errorToast = (message: string) => {
    toast(
        (t: any) => (
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <span>{message}</span>
                <button 
                    onClick={() => toast.dismiss(t.id)}
                    style={{ 
                        background: 'none', 
                        border: 'none', 
                        color: 'white',
                        marginLeft: '10px',
                        cursor: 'pointer'
                    }}
                >
                    ✕
                </button>
            </div>
        ),
        {
            ...toastConfig,
            style: {
                ...toastConfig.style,
                background: '#ef4444',
            }
        }
    );
};

const DailySalesReportPage = () => {
    const token = sessionStorage.getItem('token');
    const { isAuthenticated } = useAuth();
    
    // Calculate default dates
    const today = new Date();
    const sixtyDaysAgo = new Date(today);
    const tenDaysAgo = new Date(today);
    sixtyDaysAgo.setDate(today.getDate() - 60);
    tenDaysAgo.setDate(today.getDate() - 10);


    // Format dates to YYYY-MM-DD
    const formatDate = (date: Date) => {
        return date.toISOString().split('T')[0];
    };

    // Initialize with last 10 days range
    const [startDate, setStartDate] = useState(formatDate(tenDaysAgo));
    const [endDate, setEndDate] = useState(formatDate(today));
    const [salesData, setSalesData] = useState<DailySalesData[]>([]);
    
    // Add ref to prevent double fetch
    const isInitialMount = React.useRef(true);

    const validateDateRange = (start: string, end: string): boolean => {
        const startDateTime = new Date(start);
        const endDateTime = new Date(end);
        
        if (startDateTime < sixtyDaysAgo) {
            errorToast('Cannot retrieve data older than 60 days');
            return false;
        }
        if (endDateTime > today) {
            errorToast('End date cannot be in the future');
            return false;
        }
        if (startDateTime > endDateTime) {
            errorToast('Start date cannot be after end date');
            return false;
        }
        return true;
    };

    const fetchDailySalesReport = async () => {
        if (!startDate || !endDate) {
            warningToast('Please select both start and end dates');
            return;
        }

        if (!validateDateRange(startDate, endDate)) {
            return;
        }

        try {
            const response = await axios.post(
                'http://localhost:8080/api/daily-stats',
                {
                    startDate: startDate + "T00:00:00.000Z",
                    endDate: endDate + "T23:59:59.999Z"
                },
                {
                    headers: {
                        'Authorization': token
                    }
                }
            );
            setSalesData(response.data);
            // successToast('Daily sales report fetched successfully');
        } catch (error) {
            if (axios.isAxiosError(error) && error.response?.data?.message) {
                errorToast(error.response.data.message);
            } else {
                errorToast('Error fetching daily sales report: ' + (error instanceof Error ? error.message : 'Unknown error'));
            }
        }
    };

    // Fetch data on component mount
    React.useEffect(() => {
        if (isAuthenticated && isInitialMount.current) {
            isInitialMount.current = false;
            fetchDailySalesReport();
        }
    }, [isAuthenticated]);

    if(!isAuthenticated){
        return (
            <div className="container mt-4">
                <Toaster position="top-right" />
                <h2>Please Login to continue</h2>
                <Link href="/signup" className="btn btn-primary">Login</Link>
            </div>
        );
       }
    return (
        <div className="container mt-4">
            <Toaster position="top-right" />
            <div className="card sales-report-card">
                <div className="card-header">
                    <h2 className="mb-0">Day on Day Sales Report</h2>
                </div>
                <div className="card-body">
                    <div className="row compact-form">
                        <div className="col-md-3">
                            <div className="form-group">
                                <label className="form-label">Start Date</label>
                                <input 
                                    type="date" 
                                    className="form-control"
                                    value={startDate}
                                    min={formatDate(sixtyDaysAgo)}
                                    max={formatDate(today)}
                                    onChange={(e) => setStartDate(e.target.value)} 
                                />
                            </div>
                        </div>
                        <div className="col-md-3">
                            <div className="form-group">
                                <label className="form-label">End Date</label>
                                <input 
                                    type="date" 
                                    className="form-control"
                                    value={endDate}
                                    min={formatDate(sixtyDaysAgo)}
                                    max={formatDate(today)}
                                    onChange={(e) => setEndDate(e.target.value)} 
                                />
                            </div>
                        </div>
                        <div className="col-md-3">
                            <button 
                                className="btn btn-primary w-100"
                                onClick={fetchDailySalesReport}
                            >
                                Generate Report
                            </button>
                        </div>
                    </div>

                    {salesData.length > 0 ? (
                        <div className="table-responsive">
                            <table className="table table-striped table-bordered">
                                <thead className="table-dark">
                                    <tr>
                                        <th>S.No</th>
                                        <th>Date</th>
                                        <th>Invoiced Orders Count</th>
                                        <th>Invoiced Items Count</th>
                                        <th>Total Revenue</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {salesData.map((data, index) => (
                                        <tr key={data.id}>
                                            <td>{index + 1}</td>
                                            <td>{new Date(data.date).toLocaleDateString()}</td>
                                            <td>{data.totalInvoicedOrders}</td>
                                            <td>{data.totalItems}</td>
                                            <td>Rs.{data.totalRevenue.toFixed(2)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <div className="text-center p-4">
                            <p className="h5 text-muted">No data available for the selected date range</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default DailySalesReportPage; 