"use client";
import { useState, useEffect, useRef } from "react";
import axios from "axios";
import { toast, Toaster } from 'react-hot-toast';
import { useAuth } from '@/context/AuthContext';
import Link from 'next/link';
export default function ClientsPage() {
    const token = sessionStorage.getItem('token');

    const { isAuthenticated, role } = useAuth();
    const [clients, setClients] = useState<{
        content: { id: string; name: string }[];
        totalPages: number;
        totalElements: number;
        number: number;
    }>({
        content: [],
        totalPages: 0,
        totalElements: 0,
        number: 0
    });
    const [searchTerm, setSearchTerm] = useState("");
    const [currentPage, setCurrentPage] = useState(0);
    const clientsPerPage = 10;

    const [showAddModal, setShowAddModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [newClientName, setNewClientName] = useState("");
    const [editOldClientName, setEditOldClientName] = useState("");
    const [editNewClientName, setEditNewClientName] = useState("");

    const [isSearchMode, setIsSearchMode] = useState(false);

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
                    background: '#f59e0b', // Yellow color for warnings
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

    const initialFetchDone = useRef(false);

    useEffect(() => {
        if (!initialFetchDone.current) {
            fetchClients();
            initialFetchDone.current = true;
        } else if (currentPage > 0 || isSearchMode) {
            fetchClients();
        }
    }, [currentPage, isSearchMode]);

    const fetchClients = async () => {
        try {
            let response;
            if (isSearchMode && searchTerm) {
                response = await axios.post('http://localhost:8080/api/client/search', {
                    searchPattern: searchTerm,
                    page: currentPage,
                    size: clientsPerPage
                },
            {
                headers: {
                    'Authorization': token
                }
            });
                setClients({
                    content: response.data.content,
                    totalPages: response.data.totalPages,
                    totalElements: response.data.totalElements,
                    number: response.data.number
                });
            } else {
                response = await axios.post('http://localhost:8080/api/client/get', {
                    page: currentPage,
                    size: clientsPerPage
                },
            {
                headers: {
                    'Authorization': token
                }
            });
                setClients(response.data);
            }
        } catch (err) {
            errorToast('Failed to fetch clients: ' + (err instanceof Error ? err.message : 'Unknown error'));
        }
    };

    const handleAddClient = () => {
        if (!newClientName) {
            warningToast("Client name is required!");
            return;
        }

        axios
            .post("http://localhost:8080/api/client", { name: newClientName }, {
                headers: {
                    'Authorization': token
                }
            })
            .then(() => {
                setShowAddModal(false);
                setNewClientName("");
                fetchClients();
                successToast("Client added successfully!");
            })
            .catch((error) => {
                errorToast(error.response.data.message);
            });
    };

    const openEditModal = (oldName: string) => {
        setEditOldClientName(oldName);
        setEditNewClientName(oldName);
        setShowEditModal(true);
    };

    const handleUpdateClient = () => {
        if (!editOldClientName || !editNewClientName) {
            warningToast("Client name cannot be empty!");
            return;
        }

        axios
            .put(`http://localhost:8080/api/client`, {
                oldClientName: editOldClientName,
                newClientName: editNewClientName
            }, {
                headers: { "Content-Type": "application/json", "Authorization": token },
            })
            .then(() => {
                setShowEditModal(false);
                setEditOldClientName("");
                setEditNewClientName("");
                fetchClients();
                successToast("Client updated successfully!");
            })
            .catch((error) => {
                warningToast(error.response.data.message);
            });
    };

    const handleSearch = async (e: React.MouseEvent) => {
        e.preventDefault();
        if (!searchTerm.trim()) {
            warningToast("Please enter a search term");
            return;
        }
        setIsSearchMode(true);
        setCurrentPage(0);
    };

    const handleCancelSearch = async (e: React.MouseEvent) => {
        e.preventDefault();
        setSearchTerm("");
        setIsSearchMode(false);
        setCurrentPage(0);
    };
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
            <div className="header-container">
                <h2>Clients</h2>
                {role === 'supervisor' && (
                    <button className="btn btn-primary d-flex align-items-center gap-2" onClick={() => setShowAddModal(true)}>
                        <i className="bi bi-plus-circle"></i>
                        Add Client
                    </button>
                )}
            </div>
            
            {/* Modified search section */}
            <div className="d-flex justify-content-end align-items-center mb-3" style={{ width: '100%' }}>
                {/* <div className="d-flex gap-2" style={{ width: '50%' }}>
                    <input
                        type="text"
                        className="form-control"
                        placeholder="Search by client name"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <button 
                        className="btn btn-primary"
                        onClick={handleSearch}
                    >
                        Search
                    </button>
                    {searchTerm && (
                        <button 
                            className="btn btn-secondary"
                            onClick={handleCancelSearch}
                        >
                            Cancel
                        </button>
                    )}
                </div> */}
            </div>
            

            {/* Client list */}
            <div className="row">
                {clients.content.map((client) => (
                    <div key={client.id} className="col-6 mb-4">
                        <div className="list-group-item shadow-sm hover-effect p-3">
                            <div className="row align-items-center">
                                <div className="col">
                                    <span className="fw-bold">{client.name}</span>
                                </div>
                                <div className="col-auto">
                                    {role === 'supervisor' && (
                                        <button 
                                            className="btn btn-link text-black p-0" 
                                            onClick={() => openEditModal(client.name)}
                                            style={{ fontSize: '1.2rem' }}
                                        >
                                            <i className="bi bi-pencil-square"></i>
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {/* Pagination controls */}
            {clients.totalPages > 0 && (
                <nav aria-label="Page navigation">
                    <ul className="pagination justify-content-center mt-4">
                        <li className={`page-item ${currentPage === 0 ? 'disabled' : ''}`}>
                            <button 
                                className="page-link" 
                                onClick={() => setCurrentPage(currentPage - 1)}
                                disabled={currentPage === 0}
                            >
                                Previous
                            </button>
                        </li>
                        {[...Array(clients.totalPages)].map((_, index) => (
                            <li key={index} className={`page-item ${currentPage === index ? 'active' : ''}`}>
                                <button 
                                    className="page-link" 
                                    onClick={() => setCurrentPage(index)}
                                >
                                    {index + 1}
                                </button>
                            </li>
                        ))}
                        <li className={`page-item ${currentPage === clients.totalPages - 1 ? 'disabled' : ''}`}>
                            <button 
                                className="page-link" 
                                onClick={() => setCurrentPage(currentPage + 1)}
                                disabled={currentPage === clients.totalPages - 1}
                            >
                                Next
                            </button>
                        </li>
                    </ul>
                </nav>
            )}

            {/* Add Client Modal */}
            {showAddModal && (
                <div className="modal fade show d-block" tabIndex={-1}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Add Client</h5>
                                <button type="button" className="btn-close" onClick={() => setShowAddModal(false)}></button>
                            </div>
                            <div className="modal-body">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Enter client name"
                                    value={newClientName}
                                    required
                                    onChange={(e) => setNewClientName(e.target.value)}
                                />
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={() => setShowAddModal(false)}>
                                    Cancel
                                </button>
                                <button type="button" className="btn btn-primary" onClick={handleAddClient}>
                                    Add Client
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {showEditModal && (
                <div className="modal fade show d-block" tabIndex={-1}>
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Edit Client</h5>
                                <button type="button" className="btn-close" onClick={() => setShowEditModal(false)}></button>
                            </div>
                            <div className="modal-body">
                                <label className="form-label">Client Name</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    value={editNewClientName}
                                    onChange={(e) => setEditNewClientName(e.target.value)}
                                />
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={() => setShowEditModal(false)}>
                                    Cancel
                                </button>
                                <button type="button" className="btn btn-success" onClick={handleUpdateClient}>
                                    Update Client
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            {showAddModal && <div className="modal-backdrop fade show"></div>}
        </div>
    );
}
