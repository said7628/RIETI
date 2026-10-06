import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../data/shared/session";
import "./Login.css";
import spinnaLogo from "./spinna logo.png";

function Login() {
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");

    function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        if (!email.trim() || !password) {
            setMessage("Escribe tu correo y tu contraseña.");
            return;
        }

        if (!login(email, password)) {
            setMessage(
                "Correo o contraseña incorrectos. Prueba manolo@rieti.mx, joaquin@rieti.mx o maria@rieti.mx con la contraseña rieti.",
            );
            return;
        }

        navigate("/");
    }

    return (
        <div className="login-page">
            <div className="login-background">
                <svg
                    className="waves"
                    viewBox="0 0 800 1000"
                    preserveAspectRatio="none"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        className="wave-dark"
                        d="
                            M 120 -50
                            C 140 120, 330 190, 330 450
                            C 330 690, 130 790, 100 1050
                            L 800 1050
                            L 800 -50
                            Z
                        "
                    />

                    <path
                        className="wave-medium"
                        d="
                            M 290 -50
                            C 310 120, 490 210, 500 450
                            C 510 680, 330 790, 290 1050
                            L 800 1050
                            L 800 -50
                            Z
                        "
                    />

                    <path
                        className="wave-light"
                        d="
                            M 470 -50
                            C 490 120, 650 220, 660 450
                            C 670 670, 510 800, 470 1050
                            L 800 1050
                            L 800 -50
                            Z
                        "
                    />
                </svg>
            </div>

            <main className="login-content">
                <form className="login-form" onSubmit={handleSubmit}>
                    <div className="login-title">
                        <h1>Iniciar Sesión</h1>
                        <div className="title-line"></div>
                    </div>

                    <div className="form-group">
                        <label htmlFor="email">Correo electronico</label>

                        <input
                            id="email"
                            type="email"
                            autoComplete="username"
                            value={email}
                            onChange={(event) => setEmail(event.target.value)}
                        />
                    </div>

                    <div className="form-group password-group">
                        <label htmlFor="password">Contraseña</label>

                        <input
                            id="password"
                            type="password"
                            autoComplete="current-password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                        />
                    </div>

                    <button
                        type="button"
                        className="forgot-button"
                        onClick={() =>
                            setMessage(
                                "La recuperación de contraseña todavía no está disponible. La contraseña de demostración es rieti.",
                            )
                        }
                    >
                        Olvidé mi contraseña
                    </button>

                    {message && <p className="login-message">{message}</p>}

                    <button type="submit" className="login-button">
                        Iniciar Sesión
                    </button>
                </form>
            </main>

            <footer className="login-footer">
                <div className="spinna">
                    <div className="spinna-logo">
                        <img
                            src={spinnaLogo}
                            alt="Spinna Logo"
                            className="spinna-image"
                        />
                    </div>

                    <span>Spinna</span>
                </div>

                <div className="world-vision">
                    <span>World Vision</span>

                    <div className="world-vision-icon">✦</div>
                </div>
            </footer>
        </div>
    );
}

export default Login;
