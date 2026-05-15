import { useState, useEffect, useCallback } from "react";

import axios from "axios";

import "./App.css";

import Login from "./Login";

import UploadRetina from "./UploadRetina";

function App() {

    // TOKEN

    const [token, setToken] =
        useState(
            localStorage.getItem("token")
        );

    // HISTORY

    const [history, setHistory] =
        useState([]);

    // LOAD HISTORY

    const loadHistory = useCallback(async () => {

        try {

            const response =
                await axios.get(

                    "http://localhost:8080/api/analysis",

                    {
                        headers: {
                            Authorization:
                                `Bearer ${token}`,
                        },
                    }
                );

            console.log(
                response.data
            );

            setHistory(
                response.data
            );

        } catch (error) {

            console.log(error);
        }
    }, [token]);

    // USE EFFECT

    useEffect(() => {

        const fetchData =
            async () => {

                if (token) {

                    await loadHistory();
                }
            };

        fetchData();

    }, [token, loadHistory]);

    // LOGIN CHECK

    if (!token) {

        return (
            <Login
                setToken={setToken}
            />
        );
    }

    return (

        <div className="app-container">

            {/* SIDEBAR */}

            <div className="sidebar">

                <h1>
                    AURA AI
                </h1>

                <button
                    className="logout-btn"
                    onClick={() => {

                        localStorage.removeItem(
                            "token"
                        );

                        setToken(null);
                    }}
                >
                    Logout
                </button>

            </div>

            {/* MAIN CONTENT */}

            <div className="main-content">

                {/* UPLOAD SECTION */}

                <div className="upload-section">

                    <UploadRetina
                        token={token}
                        loadHistory={loadHistory}
                    />

                </div>

                {/* HISTORY SECTION */}

                <div className="history-section">

                    <h2>
                        Analysis History
                    </h2>

                    {history.length === 0 ? (

                        <p>
                            No analysis history
                        </p>

                    ) : (

                        <div className="history-grid">

                            {history.map(

                                (item, index) => (

                                    <div
                                        key={index}
                                        className="history-card"
                                    >

                                        {/* RETINA IMAGE */}

                                        {item.imagePath && (

                                            <img
                                                src={`http://localhost:8080/uploads/${item.imagePath}`}
                                                alt="retina"
                                                style={{
                                                    width: "100%",
                                                    maxWidth: "250px",
                                                    borderRadius: "12px",
                                                    marginBottom: "15px",
                                                }}
                                            />
                                        )}

                                        {/* ID */}

                                        <p>

                                            <strong>
                                                ID:
                                            </strong>

                                            {" "}

                                            {item.id}

                                        </p>

                                        {/* RESULT */}

                                        <p>

                                            <strong>
                                                Result:
                                            </strong>

                                            {" "}

                                            {item.result}

                                        </p>

                                    </div>
                                )
                            )}

                        </div>
                    )}

                </div>

            </div>

        </div>
    );
}

export default App;