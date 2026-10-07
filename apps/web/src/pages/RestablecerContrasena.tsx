import { useState } from "react";
import type { FormEvent } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import AuthLayout from "../components/auth/AuthLayout";
import { resetPassword } from "../data/shared/accounts";

interface RecoveryState {
    email?: string;
    code?: string;
}

function RestablecerContrasena() {
    const navigate = useNavigate();
    const location = useLocation();
    const recovery = (location.state as RecoveryState | null) ?? {};
    const [code, setCode] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [message, setMessage] = useState("");

    function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        if (!recovery.email) {
            navigate("/recuperar", { replace: true });
            return;
        }

        if (!code.trim() || !password || !confirmPassword) {
            setMessage("Escribe el código y la nueva contraseña dos veces.");
            return;
        }

        if (password.length < 4) {
            setMessage("La contraseña debe tener al menos 4 caracteres.");
            return;
        }

        if (password !== confirmPassword) {
            setMessage("Las contraseñas no coinciden.");
            return;
        }

        const error = resetPassword(recovery.email, code, password);

        if (error) {
            setMessage(error);
            return;
        }

        navigate("/login", {
            replace: true,
            state: { notice: "Contraseña actualizada. Ya puedes iniciar sesión." },
        });
    }

    if (!recovery.email || !recovery.code) {
        return (
            <AuthLayout>
                <form className="login-form">
                    <div className="login-title">
                        <h1>Nueva contraseña</h1>
                        <div className="title-line"></div>
                    </div>
                    <p className="login-message">
                        Primero pide un código con el correo de tu cuenta.
                    </p>
                    <Link to="/recuperar" className="login-button">
                        Recuperar contraseña
                    </Link>
                </form>
            </AuthLayout>
        );
    }

    return (
        <AuthLayout>
            <form className="login-form" onSubmit={handleSubmit}>
                <div className="login-title">
                    <h1>Nueva contraseña</h1>
                    <div className="title-line"></div>
                </div>

                <p className="login-message">
                    Esta versión no envía correos. Tu código es {recovery.code}.
                </p>

                <div className="form-group">
                    <label htmlFor="recovery-code">Código</label>
                    <input
                        id="recovery-code"
                        inputMode="numeric"
                        autoComplete="one-time-code"
                        value={code}
                        onChange={(event) => setCode(event.target.value)}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="new-password">Nueva contraseña</label>
                    <input
                        id="new-password"
                        type="password"
                        autoComplete="new-password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                    />
                </div>

                <div className="form-group password-group">
                    <label htmlFor="confirm-password">Confirmar contraseña</label>
                    <input
                        id="confirm-password"
                        type="password"
                        autoComplete="new-password"
                        value={confirmPassword}
                        onChange={(event) => setConfirmPassword(event.target.value)}
                    />
                </div>

                {message && <p className="login-message">{message}</p>}

                <button type="submit" className="login-button">
                    Guardar contraseña
                </button>

                <Link to="/login" className="login-back-button">
                    Iniciar sesión
                </Link>
            </form>
        </AuthLayout>
    );
}

export default RestablecerContrasena;
