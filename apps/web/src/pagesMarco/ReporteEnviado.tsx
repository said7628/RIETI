import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import type { ReportDraft } from "../data/shared/createReport";
import "./ReporteEnviado.css";
import ReportForm from "./ReportForm";
import ReportHeader from "./ReportHeader";
import spinnaLogo from "./spinna logo.png";

interface SuccessState {
    folio: string;
    draft: ReportDraft;
}

function ReporteEnviado() {
    const navigate = useNavigate();
    const location = useLocation();
    const state = location.state as SuccessState | null;

    useEffect(() => {
        if (!state?.folio || !state.draft) {
            navigate("/crear-reporte", { replace: true });
        }
    }, [navigate, state]);

    if (!state?.folio || !state.draft) {
        return null;
    }

    return (
        <div className="report-page">
            <div className="report-decoration">
                <div className="report-circle circle-1">
                    <div className="circle-middle">
                        <div className="circle-inner"></div>
                    </div>
                </div>

                <div className="report-circle circle-2">
                    <div className="circle-middle">
                        <div className="circle-inner"></div>
                    </div>
                </div>

                <div className="report-circle circle-3">
                    <div className="circle-middle">
                        <div className="circle-inner"></div>
                    </div>
                </div>

                <div className="report-circle circle-4">
                    <div className="circle-middle">
                        <div className="circle-inner"></div>
                    </div>
                </div>

                <div className="report-circle circle-5">
                    <div className="circle-middle">
                        <div className="circle-inner"></div>
                    </div>
                </div>

                <div className="report-circle circle-6">
                    <div className="circle-middle">
                        <div className="circle-inner"></div>
                    </div>
                </div>

                <div className="report-circle circle-7">
                    <div className="circle-middle">
                        <div className="circle-inner"></div>
                    </div>
                </div>
            </div>

            <ReportHeader />

            <main className="report-content">
                <div className="report-title">
                    <h1>Crear reporte nuevo</h1>
                    <div className="report-title-line"></div>
                </div>

                <ReportForm draft={state.draft} readOnly />
            </main>

            <footer className="report-footer">
                <div className="report-spinna">
                    <div className="report-spinna-logo">
                        <img
                            src={spinnaLogo}
                            alt="Spinna Logo"
                            className="report-spinna-image"
                        />
                    </div>

                    <span>Spinna</span>
                </div>

                <div className="report-world-vision">
                    <span>World Vision</span>

                    <div className="report-world-vision-icon">✦</div>
                </div>
            </footer>

            <div className="report-success-backdrop"></div>

            <div className="report-success-modal">
                <button
                    type="button"
                    className="success-close-button"
                    onClick={() => navigate("/crear-reporte")}
                    aria-label="Cerrar"
                >
                    ×
                </button>

                <h2 className="success-title">Reporte enviado</h2>

                <h2 className="success-title success-title-second">con Exito</h2>

                <div className="folio-box">Folio: {state.folio}</div>
            </div>
        </div>
    );
}

export default ReporteEnviado;
