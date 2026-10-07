import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import {
    draftToReport,
    emptyReportDraft,
    createFolio,
    type ReportDraft,
} from "../data/shared/createReport";
import { addReport } from "../data/shared/session";
import ReportForm from "../components/report/ReportForm";
import ReportHeader from "../components/report/ReportHeader";
import spinnaLogo from "../assets/spinna-logo.png";
import "./report.css";

function CrearReporte() {
    const navigate = useNavigate();
    const [draft, setDraft] = useState<ReportDraft>(emptyReportDraft);
    const [error, setError] = useState("");

    function updateDraft(field: keyof ReportDraft, value: string) {
        setDraft((current) => ({ ...current, [field]: value }));
        setError("");
    }

    function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();

        const missing: string[] = [];

        if (!draft.children.trim()) missing.push("número de niños");
        if (!draft.workType.trim()) missing.push("tipo de trabajo");
        if (!draft.details.trim()) missing.push("detalles");
        if (!draft.address.trim()) missing.push("dirección");
        if (!draft.neighborhood.trim()) missing.push("colonia");

        if (missing.length > 0) {
            setError(`Completa: ${missing.join(", ")}.`);
            return;
        }

        const folio = createFolio();
        addReport(draftToReport(draft, folio));
        navigate("/reporte-enviado", { state: { folio, draft } });
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
                    draft={draft}
                    error={error}
                    onChange={updateDraft}
                    onSubmit={handleSubmit}
                />
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
        </div>
    );
}

export default CrearReporte;
