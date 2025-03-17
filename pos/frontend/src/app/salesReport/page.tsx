"use client"
import React, { useState } from 'react';
import axios from 'axios';
import { toast, Toaster } from 'react-hot-toast';
import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';

// Define the SalesReportData interface
interface SalesReportData {
    clientName: string;
    productName: string;
    quantity: number;
    sellingPrice: number;
    revenue: number;
}

const SalesReportPage = () => {
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
    const [clientName, setClientName] = useState('');
    const [salesData, setSalesData] = useState<SalesReportData[]>([]);

    const toastConfig = {
        duration: Infinity,
        style: {
            padding: '16px',
            color: '#fff',
            borderRadius: '8px',
        },
    };

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

    // Add date validation
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

    const fetchSalesReport = async () => {
        if (!startDate || !endDate) {
            warningToast('Please select both start and end dates');
            return;
        }

        if (!validateDateRange(startDate, endDate)) {
            return;
        }

        try {
            const response = await axios.post(
                `http://localhost:8080/api/sales-report/get${clientName ? `?clientName=${encodeURIComponent(clientName)}` : ''}`,
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
            // successToast('Sales report fetched successfully');
        } catch (error) {
            if (axios.isAxiosError(error) && error.response?.data?.message) {
                errorToast(error.response.data.message);
            } else {
                errorToast('Error fetching sales report: ' + (error instanceof Error ? error.message : 'Unknown error'));
            }
        }
    };

    // Add a ref to track initial mount
    const isInitialMount = React.useRef(true);

    // Update useEffect to prevent double fetch
    React.useEffect(() => {
        if (isAuthenticated && isInitialMount.current) {
            isInitialMount.current = false;
            fetchSalesReport();
        }
    }, [isAuthenticated]); // Only depend on isAuthenticated

    if(!isAuthenticated){
        return (
            <div className="container mt-4">
                <Toaster position="top-right" />
                <h2>Please Login to continue</h2>
                <Link href="/login" className="btn btn-primary">Login</Link>
            </div>
        );
       }
    return (
        <div className="container mt-4">
            <Toaster position="top-right" />
            <div className="card sales-report-card">
                <div className="card-header">
                    <h2 className="mb-0">Sales Report</h2>
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
                            <div className="form-group">
                                <label className="form-label">Client Name</label>
                                <input 
                                    type="text" 
                                    className="form-control" 
                                    placeholder="Enter client name"
                                    value={clientName}
                                    onChange={(e) => setClientName(e.target.value)} 
                                />
                            </div>
                        </div>
                        <div className="col-md-3">
                            <button 
                                className="btn btn-primary w-100"
                                onClick={fetchSalesReport}
                            >
                                Get Sales Report
                            </button>
                        </div>
                    </div>

                    {salesData.length > 0 ? (
                        <div className="table-responsive">
                            <table className="table table-striped table-bordered">
                                <thead className="table-dark">
                                    <tr>
                                        <th>S.No</th>
                                        <th>Client Name</th>
                                        <th>Product Name</th>
                                        <th>Quantity</th>
                                        <th>Revenue</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {salesData.map((data, index) => (
                                        <tr key={index}>
                                            <td>{index + 1}</td>
                                            <td>{data.clientName}</td>
                                            <td>{data.productName}</td>
                                            <td>{data.quantity}</td>
                                            <td>Rs.{data.revenue.toFixed(2)}</td>
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

export default SalesReportPage;
