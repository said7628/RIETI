import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import AuthLayout from "../components/auth/AuthLayout";
import { login } from "../data/shared/session";

function Login() {
    const navigate = useNavigate();
    const location = useLocation();
    const notice = (location.state as { notice?: string } | null)?.notice ?? "";
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState(notice);

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
        <AuthLayout>
            <form className="login-form" onSubmit={handleSubmit}>
                <div className="login-title">
                    <h1>Iniciar sesión</h1>
                    <div className="title-line"></div>
                </div>

                <div className="form-group">
                    <label htmlFor="email">Correo electrónico</label>
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

                <Link to="/recuperar" className="forgot-button">
                    Olvidé mi contraseña
                </Link>

                {message && (
                    <p
                        className={
                            message.startsWith("Contraseña actualizada")
                                ? "login-message login-message-success"
                                : "login-message"
                        }
                    >
                        {message}
                    </p>
                )}

                <button type="submit" className="login-button">
                    Iniciar sesión
                </button>
            </form>
        </AuthLayout>
    );
}

export default Login;
