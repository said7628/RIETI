import { Link, useLocation } from "react-router-dom";
import spinnaLogo from "../../assets/spinna-logo.png";
import { useSessionUser } from "../../data/shared/session";
import UserMenu from "../shared/UserMenu";

function ReportHeader() {
    const user = useSessionUser();
    const location = useLocation();
    const isClosedCases =
        location.pathname === "/seguimiento" &&
        new URLSearchParams(location.search).get("vista") === "cerrados";
    const isTracking = location.pathname === "/seguimiento" && !isClosedCases;

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
                    to="/seguimiento"
                    className={`nav-option${isTracking ? " nav-option-active" : ""}`}
                >
                    Seguimiento
                </Link>

                {user?.role === "ADMINISTRADOR_FUNDACION" && (
                    <Link to="/cuentas" className="nav-option">
                        Cuentas
                    </Link>
                )}

            </nav>

            <UserMenu />
        </header>
    );
}

export default ReportHeader;

