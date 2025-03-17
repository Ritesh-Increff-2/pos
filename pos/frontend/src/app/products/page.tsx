"use client"
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import "./products.css"; // Import your CSS file for styling
import { toast, Toaster, Toast } from 'react-hot-toast';
import 'bootstrap-icons/font/bootstrap-icons.css';
import { useAuth } from '@/context/AuthContext';
import Link from 'next/link';
interface Product {
    clientName: string;
    productName: string;
    barcode: string;
    mrp: number;
    imageUrl: string;
    inventoryQuantity: number;
}

interface Client {
    id: string;
    name: string;
}

const ProductsPage: React.FC = () => {
    const token = sessionStorage.getItem('token');
    const { isAuthenticated, role } = useAuth();
    const [products, setProducts] = useState<{
        content: Product[];
        totalPages: number;
        totalElements: number;
        number: number;
    }>({
        content: [],
        totalPages: 0,
        totalElements: 0,
        number: 0
    });
    const [currentPage, setCurrentPage] = useState(0);
    const [quantities, setQuantities] = useState<{ [barcode: string]: number }>({quantity:0});
    const [newProduct, setNewProduct] = useState({
        barcode: '',
        clientName: '',
        productName: '',
        mrp: 0,
        imageUrl: ''
    });
    const [editProduct, setEditProduct] = useState<Product | null>(null); // State for editing a product
    const [loading, setLoading] = useState<boolean>(true);
    const [showForm, setShowForm] = useState<boolean>(false); // State to manage form visibility
    const [searchQuery, setSearchQuery] = useState<string>(''); // State for search query
    const [updateInventory, setUpdateInventory] = useState<{ barcode: string; quantity: number }>({ barcode: '', quantity: 0 });
    const [searchCriteria, setSearchCriteria] = useState<'productName' | 'clientName'>('productName'); // State for search criteria
    const [isSearchMode, setIsSearchMode] = useState(false);
    const [showTsvModal, setShowTsvModal] = useState(false);
    const [showInventoryTsvModal, setShowInventoryTsvModal] = useState(false);
    const [clients, setClients] = useState<Client[]>([]);
    const [showClientDropdown, setShowClientDropdown] = useState<boolean>(false);
    const [showSearchClientDropdown, setShowSearchClientDropdown] = useState<boolean>(false);

    // Add this ref to track component mount
    const mountedRef = React.useRef(false);

    // Add these toast configurations after your state declarations
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
            (t: Toast) => (
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

    const errorToast = (message: string) => {
        toast(
            (t: Toast) => (
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

    const warningToast = (message: string) => {
        toast(
            (t: Toast) => (
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

    // Update the useEffect hook to handle pagination for both search and normal modes
    useEffect(() => {
        // Only fetch if not mounted yet
        if (!mountedRef.current) {
            mountedRef.current = true;
            fetchProducts();
        } else {
            // After mounted, fetch based on search mode
            if (isSearchMode && searchQuery) {
                handleSearch();
            } else {
                fetchProducts();
            }
        }
    }, [currentPage]); // Keep currentPage in dependencies

    const fetchProducts = async () => {
        try {
            const response = await axios.post('http://localhost:8080/api/product/get-all', {
                    page: currentPage,
                    size: 6
                }, {
                    headers: {
                        'Authorization': token
                    }
                });
            
            setProducts({
                content: response.data.content,
                totalPages: response.data.totalPages,
                totalElements: response.data.totalElements,
                number: response.data.number
            });
        } catch (err) {
            if (axios.isAxiosError(err)) {
                const backendError = err.response?.data?.message || 'Failed to fetch products';
                errorToast(backendError);
            } else {
                errorToast('An unexpected error occurred');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        if (name === "mrp") {
            const price = Number(value);
            if (price > 100000) {
                errorToast('Selling price cannot exceed Rs.100,000');
                return;
            }
        }
        setNewProduct((prev) => ({ ...prev, [name]: value }));
    };

    const handleEditInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        if (name === "mrp") {
            const price = Number(value);
            if (price > 100000) {
                errorToast('Selling price cannot exceed Rs.100,000');
                return;
            }
        }
        if (editProduct) {
            setEditProduct((prev) => ({ ...prev!, [name]: value }));
        }
    };

    const handleAddProduct = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/product/add', newProduct, {
                headers: {
                    'Authorization': token
                }
            });
            setProducts((prev) => ({
                ...prev,
                content: [...prev.content, response.data],
                totalElements: prev.totalElements + 1
            }));
            setNewProduct({ barcode: '', clientName: '', productName: '', mrp: 0, imageUrl: '' });
            setShowForm(false);
            successToast('Product added successfully!');
        } catch (err) {
            if (axios.isAxiosError(err)) {
                errorToast(err.response?.data.message || 'Failed to add product');
            } else {
                errorToast('An unexpected error occurred while adding the product');
            }
        }
        fetchProducts();
    };

    const handleEditProduct = async (barcode: string) => {
        if (editProduct) {
            try {
                const response = await axios.put(`http://localhost:8080/api/product`, {
                    barcode: barcode,
                    clientName: String(editProduct.clientName),
                    productName: String(editProduct.productName),
                    mrp: Number(editProduct.mrp),
                    imageUrl: String(editProduct.imageUrl),
                }, {
                    headers: {
                        'Authorization': token
                    }
                });

                setProducts((prev) => ({
                    ...prev,
                    content: prev.content.map(product => product.barcode === barcode ? response.data : product),
                    totalElements: prev.totalElements
                }));
                setEditProduct(null);
                successToast('Product updated successfully!');
            } catch (err) {
                if (axios.isAxiosError(err)) {
                    const backendError = err.response?.data?.message || 'Failed to update product';
                    errorToast(backendError);
                } else {
                    errorToast('An unexpected error occurred while updating the product');
                }
            }
        }
        fetchProducts();
    };

    const handleSearch = async (e?: React.MouseEvent) => {
        if (e) {
            e.preventDefault();
            setCurrentPage(0); // Only reset to page 0 when initiating a new search
        }
        
        if (!searchQuery.trim()) {
            warningToast("Please enter a search term");
            return;
        }
        setIsSearchMode(true);
        
        try {
            const response = await axios.post('http://localhost:8080/api/product/search', {
                searchPattern: searchQuery,
                searchType: searchCriteria === 'clientName' ? 'client' : 'product',
                page: currentPage,
                size: 6
            }, {
                headers: {
                    'Authorization': token
                }
            });

            // First set the products without quantities
            const productsData = response.data;
            setProducts({
                content: productsData.content,
                totalPages: productsData.totalPages,
                totalElements: productsData.totalElements,
                number: productsData.number
            });

            // Then fetch quantities for each product
            const quantityPromises = productsData.content.map(async (product: Product) => {
                try {
                    const quantityResponse = await axios.get(`http://localhost:8080/api/inventory/${product.barcode}`, {
                        headers: {
                            'Authorization': token
                        }
                    });
                    
                    // Update the products state with the fetched quantity
                    setProducts(prevState => ({
                        ...prevState,
                        content: prevState.content.map(p => 
                            p.barcode === product.barcode 
                                ? { ...p, inventoryQuantity: quantityResponse.data.quantity }
                                : p
                        )
                    }));
                } catch (error) {
                    // console.error(`Error fetching quantity for ${product.barcode}:`, error);
                    // If there's an error, set quantity to 0 or handle as needed
                    setProducts(prevState => ({
                        ...prevState,
                        content: prevState.content.map(p => 
                            p.barcode === product.barcode 
                                ? { ...p, inventoryQuantity: 0 }
                                : p
                        )
                    }));
                }
            });

            // Wait for all quantity fetches to complete
            await Promise.all(quantityPromises);

        } catch (err) {
            if (axios.isAxiosError(err)) {
                const backendError = err.response?.data?.message || 'Failed to search products';
                errorToast(backendError);
            } else {
                errorToast('An unexpected error occurred while searching');
            }
        }
    };

    const handleSearchCriteriaChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setSearchCriteria(e.target.value as 'productName' | 'clientName');
    };

    const handleCancelSearch = async () => {
        setSearchQuery('');
        setIsSearchMode(false);
        setCurrentPage(0);
        await fetchProducts();
    };

    // Add this new function to handle inventory update
    const handleUpdateInventory = async (barcode: string) => {
        if (!updateInventory || updateInventory.quantity < 0) {
            errorToast("Please enter a valid quantity!");
            return;
        }

        try {
            await axios.post('http://localhost:8080/api/inventory', {
                barcode: barcode,
                quantity: updateInventory.quantity
            }, {
                headers: {
                    'Authorization': token
                }
            });
            
            setProducts(prev => ({
                ...prev,
                content: prev.content.map(product => 
                    product.barcode === barcode 
                        ? { ...product, inventoryQuantity: updateInventory.quantity } 
                        : product
                )
            }));
            
            setUpdateInventory({ barcode: '', quantity: 0 });
            successToast("Inventory updated successfully!");
        } catch (err) {
            if (axios.isAxiosError(err)) {
                const backendError = err.response?.data?.message || 'Failed to update inventory';
                errorToast(backendError);
            } else {
                errorToast('An unexpected error occurred while updating inventory');
            }
        }
        fetchProducts();
    };

    // Add this utility function at the top level of your component
    const downloadErrorsCSV = (errorString: string) => {
        // Convert TSV to CSV
        const rows = errorString.split('\n');
        const csvContent = rows.map(row => {
            const [barcode, error] = row.split('\t');
            // Properly escape and quote CSV fields
            return `"${barcode}","${error || ''}"`;
        }).join('\n');

        // Create blob and download link
        const blob = new Blob([csvContent], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'error_report.csv');
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    };
     
    
    // Update the handleFileUpload function
    const handleFileUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            const formData = new FormData();
            formData.append("file", file);

            try {
                const response = await axios.post('http://localhost:8080/api/product/tsv', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        'Authorization': token
                    },
                });

                successToast("File uploaded successfully!");
                await fetchProducts();
                
            } catch (err) {
                if (axios.isAxiosError(err)) {
                    const backendError = err.response?.data?.message || 'Failed to upload file';
                    
                    // If there's error data in TSV format
                    if (typeof err.response?.data === 'string' && err.response.data.includes('barcode\terror')) {
                        toast(
                            (t: any) => (
                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                    <div>
                                        <span>Upload failed. Some products could not be added. </span>
                                        <button 
                                            onClick={() => downloadErrorsCSV(err.response!.data)}
                                            style={{ 
                                                background: 'none',
                                                border: '1px solid white',
                                                color: 'white',
                                                padding: '4px 8px',
                                                borderRadius: '4px',
                                                cursor: 'pointer',
                                                marginLeft: '8px'
                                            }}
                                        >
                                            Download Error Report
                                        </button>
                                    </div>
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
                    } else {
                        errorToast(backendError);
                    }
                } else {
                    errorToast('An unexpected error occurred while uploading the file');
                }
            }
        }
    };

    // Add this new function to handle inventory file upload
    const handleInventoryFileUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            const formData = new FormData();
            formData.append("file", file);

            try {
                const response = await axios.post('http://localhost:8080/api/inventory/tsv', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                        'Authorization': token
                    },
                });

                successToast("Inventory uploaded successfully!");
                
                // Refresh quantities after successful upload
                await fetchProducts();
                
            } catch (err) {
                if (axios.isAxiosError(err)) {
                    const backendError = err.response?.data?.message || 'Failed to upload inventory file';
                    errorToast(backendError);
                    
                    // If there's detailed error data, show it
                    if (err.response?.data?.errors) {
                        err.response.data.errors.forEach((error: string) => {
                            errorToast(`Validation Error: ${error}`);
                        });
                    }
                } else {
                    errorToast('An unexpected error occurred while uploading the inventory file');
                }
            }
        }
    };

    const downloadSampleTsv = () => {
        const base64String = 'YmFyY29kZQljbGllbnROYW1lCXByb2R1Y3ROYW1lCW1ycAlpbWFnZVVybAoxMjM0NTY3OAlDbGllbnQgMQlQcm9kdWN0IDEJMjAwMAlFeGFtcGxlaW1hZ2UucG5nCjk4NzY1NDMyCUNsaWVudCAyCVByb2R1Y3QgMgk1MDAwCUV4YW1wbGVpbWFnZS5wbmcKOTg3NjU0MzIJQ2xpZW50IDMJUHJvZHVjdCAzCTQwMDAJRXhhbXBsZWltYWdlLnBuZw==';
        const decodedString = atob(base64String);
        const blob = new Blob([decodedString], { type: 'text/tab-separated-values' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'sample_products.tsv';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    };

    const downloadSampleInventoryTsv = () => {
        const base64String = 'YmFyY29kZQlxdWFudGl0eQozNDg5MzQ3OQkxMDAKNzY4OTc2MzcJMTAw';
        const decodedString = atob(base64String);
        const blob = new Blob([decodedString], { type: 'text/tab-separated-values' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'sample_inventory.tsv';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    };

    const fetchClients = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/client', {
                headers: {
                    'Authorization': token
                }
            });
            setClients(response.data);
        } catch (err) {
            errorToast('Failed to fetch clients');
        }
    };

    const handleShowForm = () => {
        fetchClients();
        setShowForm(true);
    };

    // Update the useEffect for handling clicks outside dropdowns
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            const dropdowns = document.getElementsByClassName('dropdown-list');
            let clickedInside = false;
            
            Array.from(dropdowns).forEach(dropdown => {
                if (dropdown.contains(event.target as Node)) {
                    clickedInside = true;
                }
            });
            
            if (!clickedInside && !(event.target as Element).matches('input')) {
                setShowClientDropdown(false);
                setShowSearchClientDropdown(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    if (loading) return <div>Loading...</div>;
    if(!isAuthenticated){
        return (
            <div className="container mt-4">
                <Toaster position="top-right" />
                <h2>Please Login to continue</h2>
                <Link href="/login
                " className="btn btn-primary">Login</Link>
            </div>
        );
       }
    return (
        <div className="container">
            <Toaster 
                position="top-right"
                toastOptions={{
                    duration: Infinity,
                }}
            />
            <div className="d-flex justify-content-between align-items-center my-4">
                <h1 className="mb-0">Products</h1>
                {role === 'supervisor' && (
                    <div className="d-flex gap-2">
                        <button 
                            className="btn btn-primary d-flex align-items-center gap-2" 
                            onClick={handleShowForm}
                        >
                            <i className="bi bi-plus-circle"></i>
                            Add Product
                        </button>
                        <button 
                            className="btn btn-primary d-flex align-items-center gap-2" 
                            onClick={() => setShowTsvModal(true)}
                        >
                            <i className="bi bi-file-earmark-arrow-up"></i>
                            Upload Products
                        </button>
                        <button 
                            className="btn btn-primary d-flex align-items-center gap-2" 
                            onClick={() => setShowInventoryTsvModal(true)}
                        >
                            <i className="bi bi-file-earmark-arrow-up"></i>
                            Upload Inventory
                        </button>
                    </div>
                )}
            </div>
            <div className="d-flex mb-3 search-section">
                <select className="form-select" style={{ width: '230px', flex: '0 0 auto' }} value={searchCriteria} onChange={handleSearchCriteriaChange}>
                    <option value="productName">Search by Product Name</option>
                    <option value="clientName">Search by Client Name</option>
                </select>
                <div className="position-relative flex-grow-1 d-flex gap-2 ms-2">
                    <div className="position-relative flex-grow-1">
                        <input
                            type="text"
                            className="form-control"
                            placeholder={`Search by ${searchCriteria === 'productName' ? 'Product Name' : 'Client Name'}`}
                            value={searchQuery}
                            onClick={() => {
                                if (searchCriteria === 'clientName') {
                                    setShowSearchClientDropdown(true);
                                    fetchClients();
                                }
                            }}
                            onChange={(e) => setSearchQuery(e.target.value)}
                        />
                        {showSearchClientDropdown && searchCriteria === 'clientName' && (
                            <div className="dropdown-list client-dropdown">
                                <div className="dropdown-items">
                                    {clients.map((client) => (
                                        <div
                                            key={client.id}
                                            className="dropdown-item"
                                            onClick={() => {
                                                setSearchQuery(client.name);
                                                setShowSearchClientDropdown(false);
                                            }}
                                        >
                                            {client.name}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        )}
                    </div>
                    <button className="btn btn-primary" style={{ width: '100px' }} onClick={handleSearch}>
                        <i className="bi bi-search"></i> Search
                    </button>
                    {searchQuery && (
                        <button className="btn btn-secondary" style={{ width: '100px' }} onClick={handleCancelSearch}>
                            <i className="bi bi-x-circle"></i> Cancel
                        </button>
                    )}
                </div>
            </div>
            
            {showForm && (
                <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Add New Product</h5>
                                <button type="button" className="btn-close" onClick={() => setShowForm(false)}></button>
                            </div>
                            <form onSubmit={handleAddProduct}>
                                <div className="modal-body">
                                    <div className="mb-3">
                                        <label htmlFor="barcode" className="form-label">Barcode</label>
                                        <input
                                            type="text"
                                            className="form-control"
                                            id="barcode"
                                            name="barcode"
                                            placeholder="Enter barcode"
                                            value={newProduct.barcode}
                                            onChange={(e) => {
                                                // Allow only numbers
                                                if (/^\d*$/.test(e.target.value)) {
                                                    setNewProduct(prev => ({
                                                        ...prev,
                                                        barcode: e.target.value
                                                    }));
                                                }
                                            }}
                                            required
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="clientName" className="form-label">Client Name</label>
                                        <div className="position-relative">
                                            <input 
                                                type="text" 
                                                className="form-control" 
                                                id="clientName"
                                                name="clientName" 
                                                value={newProduct.clientName} 
                                                onChange={handleInputChange}
                                                onClick={() => setShowClientDropdown(true)}
                                                placeholder="Enter or select client name"
                                                required 
                                            />
                                            {showClientDropdown && (
                                                <div className="dropdown-list client-dropdown">
                                                    <div className="dropdown-items">
                                                        {clients.map((client) => (
                                                            <div
                                                                key={client.id}
                                                                className="dropdown-item"
                                                                onClick={() => {
                                                                    setNewProduct(prev => ({
                                                                        ...prev,
                                                                        clientName: client.name
                                                                    }));
                                                                    setShowClientDropdown(false);
                                                                }}
                                                            >
                                                                {client.name}
                                                            </div>
                                                        ))}
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                        <small className="text-muted">
                                            Click to see available clients or enter a new one
                                        </small>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="productName" className="form-label">Product Name</label>
                                        <input 
                                            type="text" 
                                            className="form-control" 
                                            id="productName"
                                            name="productName" 
                                            value={newProduct.productName} 
                                            onChange={handleInputChange} 
                                            required 
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="mrp" className="form-label">MRP</label>
                                        <input 
                                            type="number" 
                                            className="form-control" 
                                            id="mrp"
                                            name="mrp" 
                                            min="0"
                                            max="100000"
                                            value={newProduct.mrp} 
                                            onChange={handleInputChange} 
                                            required 
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="imageUrl" className="form-label">Image URL</label>
                                        <input 
                                            type="text" 
                                            className="form-control" 
                                            id="imageUrl"
                                            name="imageUrl" 
                                            value={newProduct.imageUrl} 
                                            onChange={handleInputChange} 
                                            required 
                                        />
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowForm(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary">
                                        Add Product
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
            <div className="row">
                {products.content.map((product) => (
                    <div className="col-md-4 mb-4" key={product.barcode}>
                        <div className="card product-card">
                            <div className="product-image-container">
                                <span className={`stock-badge ${product.inventoryQuantity > 0 ? 'in-stock' : 'out-of-stock'}`}>
                                    {product.inventoryQuantity > 0 ? 'In Stock' : 'Out of Stock'}
                                </span>
                                <div className="edit-badge" onClick={() => setEditProduct(product)}>
                                    <i className="bi bi-pencil"></i>
                                </div>
                                <img 
                                    src={product.imageUrl} 
                                    onError={(e) => { 
                                        const target = e.target as HTMLImageElement; 
                                        target.onerror = null; 
                                        target.src = 'https://cdn.pixabay.com/photo/2024/07/20/17/12/warning-8908707_1280.png'; 
                                    }} 
                                    alt="Product Image" 
                                    className="product-image" 
                                />
                            </div>
                            <div className="card-body">
                                <h5 className="product-title">{product.productName}</h5>
                                
                                <div className="product-info">
                                    <div className="product-info-row">
                                        <div className="info-item">
                                            <i className="bi bi-person-circle"></i>
                                            <span>{product.clientName}</span>
                                        </div>
                                        <div className="info-item">
                                            <i className="bi bi-upc-scan"></i>
                                            <span>{product.barcode}</span>
                                        </div>
                                    </div>
                                    
                                    <div className="product-info-row">
                                        <div className="info-item">
                                            <i className="bi bi-tag-fill"></i>
                                            <span>₹{product.mrp.toFixed(2)}</span>
                                        </div>
                                        <div className="info-item">
                                            <i className="bi bi-box-seam"></i>
                                            <span>{product.inventoryQuantity}</span>
                                            <i 
                                                className="bi bi-pencil-square inventory-edit"
                                                onClick={() => setUpdateInventory({ 
                                                    barcode: product.barcode, 
                                                    quantity: product.inventoryQuantity 
                                                })}
                                            ></i>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
            {/* Pagination Controls */}
            <nav className="mt-3 d-flex justify-content-center">
                <ul className="pagination">
                    <li className={`page-item ${currentPage === 0 ? "disabled" : ""}`}>
                        <button 
                            className="page-link" 
                            onClick={() => setCurrentPage(currentPage - 1)}
                            disabled={currentPage === 0}
                        >
                            Previous
                        </button>
                    </li>
                    {[...Array(products.totalPages)].map((_, index) => (
                        <li key={index} className={`page-item ${currentPage === index ? "active" : ""}`}>
                            <button className="page-link" onClick={() => setCurrentPage(index)}>
                                {index + 1}
                            </button>
                        </li>
                    ))}
                    <li className={`page-item ${currentPage === products.totalPages - 1 ? "disabled" : ""}`}>
                        <button 
                            className="page-link" 
                            onClick={() => setCurrentPage(currentPage + 1)}
                            disabled={currentPage === products.totalPages - 1}
                        >
                            Next
                        </button>
                    </li>
                </ul>
            </nav>
           
            {/* Add Modal */}
            {showTsvModal && (
                <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Upload Products TSV File</h5>
                                <button type="button" className="btn-close" onClick={() => setShowTsvModal(false)}></button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-3">
                                    <label htmlFor="tsvFileInput" className="form-label">Choose TSV File</label>
                                    <input
                                        type="file"
                                        className="form-control"
                                        id="tsvFileInput"
                                        accept=".tsv"
                                        onChange={(e) => {
                                            handleFileUpload(e);
                                            setShowTsvModal(false);
                                        }}
                                    />
                                </div>
                                <div className="text-center">
                                    <button 
                                        className="btn btn-link"
                                        onClick={downloadSampleTsv}
                                    >
                                        Download Sample TSV File
                                    </button>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={() => setShowTsvModal(false)}>
                                    Close
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            {showInventoryTsvModal && (
                <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Upload Inventory TSV File</h5>
                                <button type="button" className="btn-close" onClick={() => setShowInventoryTsvModal(false)}></button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-3">
                                    <label htmlFor="inventoryTsvFileInput" className="form-label">Choose TSV File</label>
                                    <input
                                        type="file"
                                        className="form-control"
                                        id="inventoryTsvFileInput"
                                        accept=".tsv"
                                        onChange={(e) => {
                                            handleInventoryFileUpload(e);
                                            setShowInventoryTsvModal(false);
                                        }}
                                    />
                                </div>
                                <div className="text-center">
                                    <button 
                                        className="btn btn-link"
                                        onClick={downloadSampleInventoryTsv}
                                    >
                                        Download Sample Inventory TSV File
                                    </button>
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={() => setShowInventoryTsvModal(false)}>
                                    Close
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            {/* Edit Product Modal */}
            {editProduct && (
                <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Edit Product Details</h5>
                                <button type="button" className="btn-close" onClick={() => setEditProduct(null)}></button>
                            </div>
                            <form onSubmit={(e) => { e.preventDefault(); handleEditProduct(editProduct.barcode); }}>
                                <div className="modal-body">
                                    <div className="mb-3">
                                        <label htmlFor="editProductName" className="form-label">Product Name</label>
                                        <input 
                                            type="text" 
                                            className="form-control" 
                                            id="editProductName"
                                            name="productName" 
                                            value={editProduct.productName} 
                                            onChange={handleEditInputChange}
                                            required 
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="editMrp" className="form-label">MRP</label>
                                        <input 
                                            type="number" 
                                            className="form-control" 
                                            id="editMrp"
                                            name="mrp" 
                                            min="0"
                                            max="100000"
                                            value={editProduct.mrp} 
                                            onChange={handleEditInputChange}
                                            required 
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="editImageUrl" className="form-label">Image URL</label>
                                        <input 
                                            type="text" 
                                            className="form-control" 
                                            id="editImageUrl"
                                            name="imageUrl" 
                                            value={editProduct.imageUrl} 
                                            onChange={handleEditInputChange}
                                            required 
                                        />
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button type="button" className="btn btn-secondary" onClick={() => setEditProduct(null)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary">
                                        Update Product
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
            {/* Update Inventory Modal */}
            {updateInventory.barcode && (
                <div className="modal show d-block" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Update Inventory</h5>
                                <button 
                                    type="button" 
                                    className="btn-close" 
                                    onClick={() => setUpdateInventory({ barcode: '', quantity: 0 })}
                                ></button>
                            </div>
                            <form onSubmit={(e) => { 
                                e.preventDefault(); 
                                handleUpdateInventory(updateInventory.barcode); 
                            }}>
                                <div className="modal-body">
                                    <div className="mb-3">
                                        <label htmlFor="updateQuantity" className="form-label">New Quantity</label>
                                        <input 
                                            type="number" 
                                            className="form-control" 
                                            id="updateQuantity"
                                            value={updateInventory.quantity}
                                            onChange={(e) => {
                                                const quantity = parseInt(e.target.value);
                                                if (quantity > 10000) {
                                                    errorToast('Quantity cannot exceed 10,000');
                                                    return;
                                                }
                                                setUpdateInventory({
                                                    ...updateInventory,
                                                    quantity: quantity
                                                });
                                            }}
                                            min="0"
                                            max="10000"
                                            required
                                        />
                                    </div>
                                </div>
                                <div className="modal-footer">
                                    <button 
                                        type="button" 
                                        className="btn btn-secondary"
                                        onClick={() => setUpdateInventory({ barcode: '', quantity: 0 })}
                                    >
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary">
                                        Update Inventory
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ProductsPage;
