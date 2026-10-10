import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import type { ReportDraft } from "../data/shared/createReport";
import ReportForm from "../components/report/ReportForm";
import ReportHeader from "../components/report/ReportHeader";
import spinnaLogo from "../assets/spinna-logo.png";
import "./report.css";

interface SuccessState {
    folio: string;
    draft: ReportDraft;
    fileNames?: string[];
}

/**
 * Pantalla de reporte enviado.
 * Si no hay folio, regresa a crear reporte.
 */
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

                <ReportForm
                    draft={state.draft}
                    readOnly
                    fileNames={state.fileNames ?? []}
                />
            </main>

            <footer className="report-footer">
                <div className="report-spinna">
                    <div className="report-spinna-logo">
                        <img
                            src={spinnaLogo}
                            alt="SIPINNA"
                            className="report-spinna-image"
                        />
                    </div>

                    <span>SIPINNA</span>
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

                <h2 className="success-title success-title-second">con éxito</h2>

                <div className="folio-box">Folio: {state.folio}</div>
            </div>
        </div>
    );
}

export default ReporteEnviado;
