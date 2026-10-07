import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthLayout from "../components/auth/AuthLayout";
import { requestPasswordCode } from "../data/shared/accounts";

function RecuperarContrasena() {
    const navigate = useNavigate();
    const [email, setEmail] = useState("");
    const [message, setMessage] = useState("");

    function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        if (!email.trim()) {
            setMessage("Escribe el correo de tu cuenta.");
            return;
        }

        const code = requestPasswordCode(email);

        if (!code) {
            setMessage("No hay una cuenta activa con ese correo.");
            return;
        }

        navigate("/recuperar/codigo", {
            state: { email: email.trim().toLowerCase(), code },
        });
    }

    return (
        <AuthLayout>
            <form className="login-form" onSubmit={handleSubmit}>
                <div className="login-title">
                    <h1>Recuperar contraseña</h1>
                    <div className="title-line"></div>
                </div>

                <div className="form-group">
                    <label htmlFor="recovery-email">Correo electrónico</label>
                    <input
                        id="recovery-email"
                        type="email"
                        autoComplete="username"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                    />
                </div>

                {message && <p className="login-message">{message}</p>}

                <button type="submit" className="login-button">
                    Enviar código
                </button>

                <Link to="/login" className="login-back-button">
                    Iniciar sesión
                </Link>
            </form>
        </AuthLayout>
    );
}

export default RecuperarContrasena;
