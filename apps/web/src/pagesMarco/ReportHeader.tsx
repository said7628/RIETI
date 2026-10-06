import { Link, useLocation } from "react-router-dom";
import { useSessionUser } from "../data/shared/session";
import spinnaLogo from "./spinna logo.png";

function ReportHeader() {
    const user = useSessionUser();
    const location = useLocation();
    const isClosedCases =
        location.pathname === "/seguimiento" &&
        new URLSearchParams(location.search).get("vista") === "cerrados";
    const isTracking = location.pathname === "/seguimiento" && !isClosedCases;
    const isCreate =
        location.pathname === "/crear-reporte" ||
        location.pathname === "/reporte-enviado";

    return (
        <header className="report-header">
            <Link to="/" className="report-brand">
                <img
                    src={spinnaLogo}
                    alt="RIETI"
                    className="report-brand-logo"
                />

                <span>RIETI</span>
            </Link>

            <nav className="report-navigation">
                <Link
                    to="/crear-reporte"
                    className={`nav-option${isCreate ? " nav-option-active" : ""}`}
                >
                    Crear reporte
                </Link>

                <Link
                    to="/seguimiento"
                    className={`nav-option${isTracking ? " nav-option-active" : ""}`}
                >
                    Seguimiento
                </Link>

            </nav>

            <Link to="/login" className="user-button">
                {user ? user.name : "Iniciar sesión"}
            </Link>
        </header>
    );
}

export default ReportHeader;
