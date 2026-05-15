import { useState } from "react";

import axios from "axios";

import "./App.css";

function Login({ setToken }) {

    const [username, setUsername] = useState("");

    const [password, setPassword] = useState("");

    const [message, setMessage] = useState("");

    const [loading, setLoading] = useState(false);

    const handleLogin = async () => {

        if (!username || !password) {

            setMessage("Please enter username and password");

            return;
        }

        try {

            setLoading(true);

            setMessage("");

            const response = await axios.post(

                "http://localhost:8080/api/auth/login",

                {
                    username: username,
                    password: password,
                },

                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );

            console.log(response.data);

            if (response.data.token) {

                const token = response.data.token;

                localStorage.setItem(
                    "token",
                    token
                );

                setToken(token);

                setMessage("Login success");

            } else {

                setMessage(
                    response.data.message ||
                    "Invalid credentials"
                );
            }

        } catch (error) {

            console.error(error);

            if (error.response) {

                setMessage(
                    error.response.data.message ||
                    "Login failed"
                );

            } else if (error.request) {

                setMessage(
                    "Cannot connect to server"
                );

            } else {

                setMessage(
                    "Unexpected error occurred"
                );
            }

        } finally {

            setLoading(false);
        }
    };

    return (

        <div className="login-page">

            <div className="login-card">

                <h1>AURA AI</h1>

                <p>
                    Retinal AI Diagnosis System
                </p>

                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) =>
                        setUsername(e.target.value)
                    }
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) =>
                        setPassword(e.target.value)
                    }
                />

                <button
                    onClick={handleLogin}
                    disabled={loading}
                >
                    {loading ? "Loading..." : "Login"}
                </button>

                {message && (

                    <p className="error-message">
                        {message}
                    </p>
                )}

            </div>

        </div>
    );
}

export default Login;