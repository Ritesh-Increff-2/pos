"use client"
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './manageOrder.css'; 
import { Toaster, toast } from 'react-hot-toast';
import { useAuth } from '@/context/AuthContext';
import Link from 'next/link';
import 'bootstrap-icons/font/bootstrap-icons.css';

interface OrderItem {
    productId: string;
    quantity: number;
    orderItemTotal: number;
    sellingPrice: number;
}

interface Product {
    barcode: string;
}

interface Order {
    orderId: string;
    status: string;
    customerName: string;
    customerEmail: string;
    orderItems: OrderItem[];
    orderTotal: number;
    sellingPrice: number;
    orderDate: string;
    orderTime: string;
}

const ManageOrderPage: React.FC = () => {
    const token = sessionStorage.getItem('token');
    const { isAuthenticated, role } = useAuth();
    const [orders, setOrders] = useState<{
        content: Order[];
        totalPages: number;
        totalElements: number;
        number: number;
    }>({
        content: [],
        totalPages: 0,
        totalElements: 0,
        number: 0
    });
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [expandedOrderIndex, setExpandedOrderIndex] = useState<number | null>(null);
    const [productDetails, setProductDetails] = useState<{ [productId: string]: Product }>({});
    // const [invoicePaths, setInvoicePaths] = useState<{ [orderId: string]: string }>({});
    
    const [currentPage, setCurrentPage] = useState(0);
    const ordersPerPage = 6; // Set to 6 orders per page

    // Add these new state variables for the modal
    const [showModal, setShowModal] = useState(false);
    const [customerName, setCustomerName] = useState("");
    const [customerEmail, setCustomerEmail] = useState("");
    const [orderItems, setOrderItems] = useState<OrderItem[]>([
        { productId: "", quantity: 0, orderItemTotal: 0, sellingPrice: 0 }
    ]);

    // Add new state for search
    const [searchOrderId, setSearchOrderId] = useState("");
    const [isSearchActive, setIsSearchActive] = useState(false);

    // Add new state variables for status search
    const [searchType, setSearchType] = useState<'orderId' | 'status'>('orderId');
    const [searchStatus, setSearchStatus] = useState<string>('');
    
    const ORDER_STATUSES = ['INVOICED', 'CANCELLED', 'UNFULFILLABLE', 'FULFILLABLE'];

    // Calculate the date range limits
    const today = new Date();
    const sixtyDaysAgo = new Date(today);
    sixtyDaysAgo.setDate(today.getDate() - 60);

    // Format dates to YYYY-MM-DD for input default values
    const formatDate = (date: Date) => {
        return date.toISOString().split('T')[0];
    };

    // Set default values for date inputs
    const [startDate, setStartDate] = useState<string>(formatDate(sixtyDaysAgo));
    const [endDate, setEndDate] = useState<string>(formatDate(today));
    const [isDateFilterActive, setIsDateFilterActive] = useState(false);

    // Add these new state variables at the top with other state declarations
    const [barcodes, setBarcodes] = useState<number[]>([]);
    const [showBarcodeDropdown, setShowBarcodeDropdown] = useState<number | null>(null);

    // Add this new state for the confirmation modal
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [orderToCancel, setOrderToCancel] = useState<{ orderId: string; isFulfillable: boolean } | null>(null);

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

    useEffect(() => {
        // Create a flag to track if the component is mounted
        let isMounted = true;

        const fetchData = async () => {
            if (!isMounted) return;

            if (isSearchActive) {
                const searchCriteria = buildSearchCriteria();
                await fetchOrders(searchCriteria);
            } else {
                await fetchOrders();
            }
        };

        fetchData();

        // Cleanup function
        return () => {
            isMounted = false;
        };
    }, [currentPage, isSearchActive]);

    const fetchOrders = async (searchCriteria?: any) => {
        try {
            const paginationData = {
                page: currentPage,
                size: ordersPerPage,
                ...(searchCriteria || {}) // Merge search criteria if provided
            };

            // Use different endpoints based on whether we're searching or fetching all orders
            const endpoint = searchCriteria 
                ? 'http://localhost:8080/api/order/search'
                : 'http://localhost:8080/api/order/get';

            const response = await axios.post(
                endpoint,
                paginationData,
                {
                    headers: {
                        'Authorization': token
                    }
                }
            );
            
            setOrders(response.data);
            await fetchProductDetails(response.data.content);
        } catch (err) {
            errorToast('Failed to fetch orders: ' + (err instanceof Error ? err.message : 'Unknown error'));
        } finally {
            setLoading(false);
        }
    };

    const fetchProductDetails = async (orders: Order[]) => {
        const productPromises = orders.flatMap(order =>
            order.orderItems.map(async (item) => {
                try {
                    const response = await axios.get(
                        `http://localhost:8080/api/product/${item.productId}`, 
                        {
                        headers: {
                            'Authorization': token
                        }
                    });
                    if (response.data) {
                        setProductDetails(prev => ({
                            ...prev,
                            [item.productId]: response.data,
                        }));
                    }
                } catch (error) {
                    console.error(`Failed to fetch product details for productId ${item.productId}:`, error);
                    
                }
            })
        );
        await Promise.all(productPromises);

    };

    const toggleOrderDetails = (index: number) => {
        setExpandedOrderIndex(expandedOrderIndex === index ? null : index);
    };


    const handleCancelOrder = async (order: Order) => {
        setOrderToCancel({ orderId: order.orderId, isFulfillable: true });
        setShowConfirmModal(true);
    };

    const handleRetryOrder = async (orderId: string) => {
        try {
            const response = await axios.put(`http://localhost:8080/api/order/${orderId}`, {},{
                headers: {
                    'Authorization': token
                }
            });
            // Refresh the orders list after retry
            await fetchOrders();
            successToast('Order retry successful');
        } catch (err) {
            if (axios.isAxiosError(err) && err.response?.data?.message) {
                errorToast(err.response.data.message);
            } else {
                errorToast("Failed to FullFill order");
            }
        }
    };

    const handleGenerateInvoice = async (orderId: string) => {
        try {
            const response = await axios.post(`http://localhost:8080/api/invoice/generate/${orderId}`, {},{
                headers: {
                    'Authorization': token
                }
            });
            await fetchOrders();
            if (response.data) {
                successToast('Invoice generated successfully');
            }
        } catch (err) {
            errorToast('Failed to generate invoice: ' + (err instanceof Error ? err.message : 'Unknown error'));
        }
    };

    const handleDownloadInvoice = async (orderId: string) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/invoice/download/${orderId}`, {
                headers: {
                    'Authorization': token
                }
            });
            await fetchOrders();
            if (response.data) {
                const base64String = response.data;
                const byteCharacters = atob(base64String);
                const byteNumbers = new Array(byteCharacters.length);
                for (let i = 0; i < byteCharacters.length; i++) {
                    byteNumbers[i] = byteCharacters.charCodeAt(i);
                }
                const byteArray = new Uint8Array(byteNumbers);
                const blob = new Blob([byteArray], { type: 'application/pdf' });
                const blobUrl = URL.createObjectURL(blob);
                window.open(blobUrl, '_blank', 'noopener,noreferrer');
                successToast('Invoice downloaded successfully');
            }
        } catch (err) {
            errorToast('Failed to download invoice: ' + (err instanceof Error ? err.message : 'Unknown error'));
        }
    };

    // Add these new handlers for the create order modal
    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>, index: number) => {
        const { name, value } = e.target;
        const updatedOrderItems = [...orderItems];

        if (name === "quantity") {
            const quantity = Number(value);
            if (quantity > 1000) {
                errorToast('Quantity cannot exceed 1000');
                return;
            }
            updatedOrderItems[index] = { ...updatedOrderItems[index], [name]: quantity };
        } else if (name === "sellingPrice") {
            const price = Number(value);
            if (price > 100000) {
                errorToast('Selling price cannot exceed Rs.100,000');
                return;
            }
            updatedOrderItems[index] = { ...updatedOrderItems[index], [name]: price };
        } else {
            updatedOrderItems[index] = { ...updatedOrderItems[index], [name]: value };
        }

        setOrderItems(updatedOrderItems);
    };

    const handleAddItem = () => {
        if (orderItems.length < 10) {
            setOrderItems([...orderItems, { 
                productId: "", 
                quantity: 0, 
                orderItemTotal: 0, 
                sellingPrice: 0 
            }]);
        }
    };

    const handleRemoveItem = (index: number) => {
        setOrderItems(orderItems.filter((_, i) => i !== index));
    };

    // Add this function to reset the modal form
    const resetModalForm = () => {
        setCustomerName("");
        setCustomerEmail("");
        setOrderItems([{ productId: "", quantity: 0, orderItemTotal: 0, sellingPrice: 0 }]);
    };

    // Update the modal open handler to reset form
    const handleOpenModal = () => {
        resetModalForm();
        setShowModal(true);
    };

    const handleCreateOrder = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const orderData = {
            customerName,
            customerEmail,
            orderItems: orderItems.map(item => ({
                barcode: item.productId,
                quantity: item.quantity,
                sellingPrice: item.sellingPrice
            }))
        };

        try {
            const response = await axios.post("http://localhost:8080/api/order", orderData, {
                headers: {
                    'Authorization': token
                }
            });
            successToast(`Order placed successfully! Your Order ID is: ${response.data.orderId}`);
            resetModalForm();
            setShowModal(false);
            fetchOrders();
        } catch (err) {
            if (axios.isAxiosError(err) && err.response?.data?.message) {
                errorToast(err.response.data.message);
            } else {
                errorToast("Failed to create order");
            }
        }
    };

    // Add this new helper function to build search criteria
    const buildSearchCriteria = () => {
        const searchCriteria: any = {
            page: currentPage,
            size: ordersPerPage
        };

        if (searchType === 'orderId' && searchOrderId.trim() !== '') {
            searchCriteria.orderId = searchOrderId;
        }

        if (searchType === 'status' && searchStatus !== '') {
            searchCriteria.status = searchStatus;
        }

        if (isDateFilterActive && startDate && endDate) {
            const startDateTime = new Date(startDate);
            startDateTime.setHours(0, 0, 0, 0);
            const endDateTime = new Date(endDate);
            endDateTime.setHours(23, 59, 59, 999);

            searchCriteria.startDate = startDateTime.toISOString();
            searchCriteria.endDate = endDateTime.toISOString();
        }

        return searchCriteria;
    };

    // Update the handleSearch function
    const handleSearch = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const hasOrderId = searchOrderId.trim() !== '';
        const hasStatus = searchStatus !== '';
        const hasDateRange = isDateFilterActive && startDate && endDate;

        if (!hasOrderId && !hasStatus && !hasDateRange) {
            errorToast('Please enter either Order ID, Status, or select a date range');
            return;
        }

        setIsSearchActive(true);
        setCurrentPage(0); // Reset to first page when searching

        try {
            const searchCriteria = buildSearchCriteria();
            await fetchOrders(searchCriteria);
        } catch (err) {
            errorToast('Failed to search orders: ' + (err instanceof Error ? err.message : 'Unknown error'));
        }
    };

    const handleCancelSearch = async () => {
        setSearchOrderId("");
        setSearchStatus("");
        setIsDateFilterActive(false);
        setStartDate(formatDate(sixtyDaysAgo));
        setEndDate(formatDate(today));
        setIsSearchActive(false);
        setCurrentPage(0);
        
        try {
            await fetchOrders();
        } catch (err) {
            errorToast('Failed to fetch orders: ' + (err instanceof Error ? err.message : 'Unknown error'));
        }
    };


    // Add this new function to fetch barcodes
    const fetchBarcodes = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/product/allBarcodes', {
                headers: {
                    'Authorization': token
                }
            });
            // Convert string barcodes to numbers
            const numericBarcodes = response.data.map((barcode: string) => parseInt(barcode, 10));
            setBarcodes(numericBarcodes);
        } catch (err) {
            errorToast('Failed to fetch barcodes');
        }
    };

    // Add useEffect to fetch barcodes when modal opens
    useEffect(() => {
        if (showModal) {
            fetchBarcodes();
        }
    }, [showModal]);

    // Add this useEffect for handling outside clicks
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (showBarcodeDropdown !== null) {
                const dropdowns = document.getElementsByClassName('barcode-dropdown');
                let clickedInside = false;
                
                Array.from(dropdowns).forEach(dropdown => {
                    if (dropdown.contains(event.target as Node)) {
                        clickedInside = true;
                    }
                });
                
                if (!clickedInside) {
                    setShowBarcodeDropdown(null);
                }
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showBarcodeDropdown]);

    // Update handleCancelUnfulfillableOrder function
    const handleCancelUnfulfillableOrder = async (orderId: string) => {
        setOrderToCancel({ orderId, isFulfillable: false });
        setShowConfirmModal(true);
    };

    // Update handleConfirmCancel function
    const handleConfirmCancel = async () => {
        if (!orderToCancel) return;

        try {
            // Only make the cancel API call
            await axios.post(`http://localhost:8080/api/order/cancel/${orderToCancel.orderId}`, {}, {
                headers: {
                    'Authorization': token
                }
            });

            await fetchOrders();
            successToast('Order cancelled successfully');
        } catch (err) {
            errorToast('Failed to cancel order: ' + (err instanceof Error ? err.message : 'Unknown error'));
        } finally {
            setShowConfirmModal(false);
            setOrderToCancel(null);
        }
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>{error}</div>;
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
        <div className="manage-order-container">
            <Toaster 
                position="top-right"
                toastOptions={{
                    duration: Infinity,
                }}
            />
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h1>Manage Orders</h1>
                {role === 'supervisor' && (
                <button className="btn btn-primary d-flex align-items-center gap-2" onClick={handleOpenModal}>
                    <i className="bi bi-plus-circle"></i>
                    Create New Order
                </button>
                )}
            </div>

            <form onSubmit={handleSearch} className="mb-4">
                <div className="row g-3 align-items-end">
                    <div className="col-auto">
                        <label className="form-label" style={{fontWeight: '500'}}>Search by:</label>
                        <select 
                            className="form-select"
                            value={searchType}
                            onChange={(e) => {
                                setSearchType(e.target.value as 'orderId' | 'status');
                                setSearchOrderId('');
                                setSearchStatus('');
                            }}
                        >
                            <option value="orderId">Order ID</option>
                            <option value="status">Status</option>
                        </select>
                    </div>

                    {searchType === 'orderId' ? (
                        <div className="col">
                            <label className="form-label" style={{fontWeight: '500'}}>Order ID</label>
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Enter Order ID..."
                                value={searchOrderId}
                                onChange={(e) => setSearchOrderId(e.target.value)}
                            />
                        </div>
                    ) : (
                        <div className="col">
                            <label className="form-label" style={{fontWeight: '500'}}>Status</label>
                            <select
                                className="form-select"
                                value={searchStatus}
                                onChange={(e) => setSearchStatus(e.target.value)}
                            >
                                <option value="">Select Status...</option>
                                {ORDER_STATUSES.map(status => (
                                    <option key={status} value={status}>{status}</option>
                                ))}
                            </select>
                        </div>
                    )}

                    <div className="col">
                        <label className="form-label" style={{fontWeight: '500'}}>Start Date</label>
                        <input
                            type="date"
                            className="form-control"
                            value={startDate}
                            onChange={(e) => {
                                setStartDate(e.target.value);
                                setIsDateFilterActive(true);
                            }}
                            min={formatDate(sixtyDaysAgo)}
                            max={formatDate(today)}
                        />
                    </div>
                    <div className="col">
                        <label className="form-label" style={{fontWeight: '500'}}>End Date</label>
                        <input
                            type="date"
                            className="form-control"
                            value={endDate}
                            onChange={(e) => {
                                setEndDate(e.target.value);
                                setIsDateFilterActive(true);
                            }}
                            min={formatDate(sixtyDaysAgo)}
                            max={formatDate(today)}
                        />
                    </div>

                    <div className="col-auto">
                        <button 
                            type="submit" 
                            className="btn btn-primary"
                        >
                           <i className="bi bi-search"></i> Search
                        </button>
                    </div>

                    {(searchOrderId || searchStatus || isDateFilterActive) && (
                        <div className="col-auto">
                            <button 
                                type="button" 
                                className="btn btn-secondary" 
                                onClick={handleCancelSearch}
                            >
                               <i className="bi bi-x-circle"></i> Cancel
                            </button>
                        </div>
                    )}
                </div>
            </form>

            {/* Create Order Modal - Remove error/success alerts */}
            <div className={`modal ${showModal ? 'show' : ''}`} 
                 style={{ display: showModal ? 'block' : 'none' }}
                 tabIndex={-1}>
                <div className="modal-dialog modal-lg">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title">Create New Order</h5>
                            <button 
                                type="button" 
                                className="btn-close" 
                                onClick={() => {
                                    setShowModal(false);
                                    resetModalForm();
                                }}
                            ></button>
                        </div>
                        <div className="modal-body">
                            <form onSubmit={handleCreateOrder}>
                                <div className="mb-3">
                                    <label className="form-label">Customer Name:</label>
                                    <input
                                        type="text"
                                        className="form-control"
                                        value={customerName}
                                        onChange={(e) => setCustomerName(e.target.value)}
                                        required
                                    />
                                </div>
                                <div className="mb-3">
                                    <label className="form-label">Customer Email:</label>
                                    <input
                                        type="email"
                                        className="form-control"
                                        value={customerEmail}
                                        onChange={(e) => setCustomerEmail(e.target.value)}
                                        required
                                    />
                                </div>
                                
                                <h6 className="mt-4">Order Items</h6>
                                {orderItems.map((item, index) => (
                                    <div key={index} className="row mb-3 align-items-center">
                                        <div className="col position-relative">
                                            <input
                                                type="text"
                                                name="productId"
                                                className="form-control"
                                                placeholder="Enter product barcode"
                                                value={item.productId}
                                                onChange={(e) => handleInputChange(e, index)}
                                                onClick={() => setShowBarcodeDropdown(index)}
                                                required
                                            />
                                            {showBarcodeDropdown === index && (
                                                <div className="barcode-dropdown">
                                                    <div className="barcode-list">
                                                        {barcodes.map((barcode) => (
                                                            <div
                                                                key={barcode}
                                                                className="barcode-item"
                                                                onClick={() => {
                                                                    handleInputChange({
                                                                        target: {
                                                                            name: 'productId',
                                                                            value: barcode.toString()
                                                                        }
                                                                    } as React.ChangeEvent<HTMLInputElement>, index);
                                                                    setShowBarcodeDropdown(null);
                                                                }}
                                                            >
                                                                {barcode}
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                        <div className="col">
                                            <input
                                                type="number"
                                                name="quantity"
                                                className="form-control"
                                                placeholder="Enter quantity"
                                                min="1"
                                                max="1000"
                                                value={item.quantity || ''}
                                                onChange={(e) => handleInputChange(e, index)}
                                                required
                                            />
                                        </div>
                                        <div className="col">
                                            <input
                                                type="number"
                                                name="sellingPrice"
                                                className="form-control"
                                                placeholder="Enter price in Rs."
                                                min="0"
                                                max="100000"
                                                step="1"
                                                value={item.sellingPrice || ''}
                                                onChange={(e) => handleInputChange(e, index)}
                                                required
                                            />
                                        </div>
                                        <div className="col-auto">
                                            <button 
                                                type="button" 
                                                className="btn btn-danger"
                                                onClick={() => handleRemoveItem(index)}
                                            >
                                                Remove
                                            </button>
                                        </div>
                                    </div>
                                ))}
                                
                                <div className="d-flex gap-2 mb-3">
                                    <button type="button" className="btn btn-secondary" onClick={handleAddItem}>
                                        Add More Items
                                    </button>
                                    <button type="submit" className="btn btn-primary">Create Order</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            {/* Modal Backdrop */}
            {showModal && <div className="modal-backdrop fade show"></div>}

            {orders.content.map((order, index) => (
                <div key={order.orderId} className={`order-card ${order.status === 'FULFILLABLE' || order.status === 'INVOICED' ? 'order-success' : 'order-failure'}`}>
                    <div className="order-summary" onClick={() => toggleOrderDetails(index)}>
                        <h2>Order ID: {order.orderId}</h2>
                        <p className={`order-status ${order.status === 'FULFILLABLE' || order.status === 'INVOICED' ? 'fulfillable' : 'unfulfillable'}`}>
                            Status: {order.status}
                        </p>
                        <p className="info-with-icon">
                            <i className="bi bi-person-fill"></i>
                            {order.customerName}
                        </p>
                        <p className="info-with-icon">
                            <i className="bi bi-envelope-fill"></i>
                            {order.customerEmail}
                        </p>
                        <p className="info-with-icon">
                            <i className="bi bi-currency-rupee"></i>
                            {order.orderTotal}
                        </p>
                        <p className="info-with-icon">
                            <i className="bi bi-clock-fill"></i>
                            {new Date(order.orderTime).toLocaleString('en-IN', {
                                year: 'numeric',
                                month: 'long',
                                day: 'numeric',
                                hour: '2-digit',
                                minute: '2-digit',
                                hour12: true
                            })}
                        </p>
                        {order.status === 'FULFILLABLE' && (
                            <>
                                <div className="button-container" >
                                    <button 
                                        className="btn btn-danger"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleCancelOrder(order);
                                        }}
                                    >
                                        Cancel Order
                                    </button>
                                    <button 
                                        className="btn btn-primary"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleGenerateInvoice(order.orderId);
                                        }}
                                    >
                                        Generate Invoice
                                    </button>
                                </div>
                            </>
                        )}
                         {order.status === 'INVOICED' && (
                            <div className="button-container" >
                        <button 
                            className="btn btn-success"
                            onClick={(e) => {
                                e.stopPropagation();
                                handleDownloadInvoice(order.orderId);
                            }}
                        >
                            Download Invoice
                        </button>
                        </div>
                    )}

                        {order.status === 'UNFULFILLABLE' && (
                            <div className="button-container" >
                                 <button 
                                    className="btn btn-danger"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        handleCancelUnfulfillableOrder(order.orderId);
                                    }}
                                >
                                    Cancel Order
                                </button>
                                <button 
                                    className="btn btn-primary"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        handleRetryOrder(order.orderId);
                                    }}
                                >
                                    Retry Order
                                </button>
                               
                            </div>
                        )}
                    </div>
                    {expandedOrderIndex === index && (
                        <div className="order-details w-100">
                            <h4>Order Items:</h4>
                            <table className="table table-striped table-bordered w-100">
                                <thead>
                                    <tr >
                                        <th className="text-center"style={{ width: '10%' }}>S.No.</th>
                                        <th className="text-end"style={{ width: '25%' }}>Barcode</th>
                                        <th className="text-end"style={{ width: '25%' }}>Quantity</th>
                                        <th className="text-end"style={{ width: '25%' }}>Selling Price (Rs.)</th>
                                        <th className="text-end"style={{ width: '25%' }}>Item Total (Rs.)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {order.orderItems.map((item, itemIndex) => (
                                        <tr key={itemIndex}>
                                            <td className="text-center">{itemIndex + 1}</td>
                                            <td className="text-end">{productDetails[item.productId]?.barcode || 'Loading...'}</td>
                                            <td className="text-end">{item.quantity}</td>
                                            <td className="text-end">{item.sellingPrice.toLocaleString('en-IN')}</td>
                                            <td className="text-end">{item.orderItemTotal.toLocaleString('en-IN')}</td>
                                        </tr>
                                    ))}
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colSpan={4} className="text-end fw-bold">Order Total:</td>
                                        <td className="text-end fw-bold">Rs. {order.orderTotal.toLocaleString('en-IN')}</td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    )}
                </div>
            ))}

            {/* Pagination Controls */}
            <nav className="mt-3 d-flex justify-content-center">
                <ul className="pagination">
                    <li className={`page-item ${orders.number === 0 ? "disabled" : ""}`}>
                        <button 
                            className="page-link" 
                            onClick={() => setCurrentPage(orders.number - 1)}
                            disabled={orders.number === 0}
                        >
                            Previous
                        </button>
                    </li>
                    {[...Array(orders.totalPages)].map((_, index) => (
                        <li key={index} className={`page-item ${orders.number === index ? "active" : ""}`}>
                            <button 
                                className="page-link" 
                                onClick={() => setCurrentPage(index)}
                            >
                                {index + 1}
                            </button>
                        </li>
                    ))}
                    <li className={`page-item ${orders.number === orders.totalPages - 1 ? "disabled" : ""}`}>
                        <button 
                            className="page-link" 
                            onClick={() => setCurrentPage(orders.number + 1)}
                            disabled={orders.number === orders.totalPages - 1}
                        >
                            Next
                        </button>
                    </li>
                </ul>
            </nav>

            {/* Confirmation Modal */}
            {showConfirmModal && (
                <>
                    <div className="modal show" style={{ display: 'block' }}>
                        <div className="modal-dialog">
                            <div className="modal-content">
                                <div className="modal-header">
                                    <h5 className="modal-title">Confirm Cancellation</h5>
                                    <button 
                                        type="button" 
                                        className="btn-close" 
                                        onClick={() => {
                                            setShowConfirmModal(false);
                                            setOrderToCancel(null);
                                        }}
                                    ></button>
                                </div>
                                <div className="modal-body">
                                    <p>Are you sure you want to cancel this order?</p>
                                </div>
                                <div className="modal-footer">
                                    <button 
                                        type="button" 
                                        className="btn btn-secondary" 
                                        onClick={() => {
                                            setShowConfirmModal(false);
                                            setOrderToCancel(null);
                                        }}
                                    >
                                        No
                                    </button>
                                    <button 
                                        type="button" 
                                        className="btn btn-danger" 
                                        onClick={handleConfirmCancel}
                                    >
                                        Yes
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="modal-backdrop fade show"></div>
                </>
            )}
        </div>
    );
};

export default ManageOrderPage;
