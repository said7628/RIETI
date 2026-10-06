import type { ChangeEvent, FormEvent } from "react";
import type { ReportDraft } from "../data/shared/createReport";

interface ReportFormProps {
    draft: ReportDraft;
    readOnly?: boolean;
    error?: string;
    onChange?: (field: keyof ReportDraft, value: string) => void;
    onSubmit?: (event: FormEvent<HTMLFormElement>) => void;
}

function ReportForm({
    draft,
    readOnly = false,
    error,
    onChange,
    onSubmit,
}: ReportFormProps) {
    function update(field: keyof ReportDraft) {
        return (
            event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
        ) => {
            onChange?.(field, event.target.value);
        };
    }

    return (
        <form className="report-form" onSubmit={onSubmit}>
            <div className="report-top-fields">
                <div className="report-field">
                    <label htmlFor="children">Número de niños</label>

                    <input
                        id="children"
                        type="number"
                        min="1"
                        placeholder="1"
                        value={draft.children}
                        readOnly={readOnly}
                        onChange={update("children")}
                    />
                </div>

                <div className="report-field">
                    <label htmlFor="ages">Edades de los niños</label>

                    <input
                        id="ages"
                        type="text"
                        placeholder="Ej: 6, 12, 16 años"
                        value={draft.ages}
                        readOnly={readOnly}
                        onChange={update("ages")}
                    />
                </div>

                <div className="report-field">
                    <label htmlFor="workType">Tipo de Trabajo</label>

                    <input
                        id="workType"
                        type="text"
                        value={draft.workType}
                        readOnly={readOnly}
                        onChange={update("workType")}
                    />
                </div>

                <div className="report-field">
                    <label htmlFor="schedule">Horario de Trabajo</label>

                    <input
                        id="schedule"
                        type="text"
                        placeholder="Ej: 8:00 am - 6:00 pm"
                        value={draft.schedule}
                        readOnly={readOnly}
                        onChange={update("schedule")}
                    />
                </div>
            </div>

            <div className="report-information">
                <div className="report-left-column">
                    <div className="report-field details-field">
                        <label htmlFor="details">Detalles:</label>

                        <textarea
                            id="details"
                            placeholder="Describa detalladamente lo que observó ..."
                            value={draft.details}
                            readOnly={readOnly}
                            onChange={update("details")}
                        ></textarea>
                    </div>

                    <div className="file-field">
                        <input id="reportFiles" type="file" multiple disabled={readOnly} />
                    </div>
                </div>

                <div className="report-address">
                    <div className="report-field full-address">
                        <label htmlFor="address">Dirección completa</label>

                        <input
                            id="address"
                            type="text"
                            value={draft.address}
                            readOnly={readOnly}
                            onChange={update("address")}
                        />
                    </div>

                    <div className="address-row">
                        <div className="report-field">
                            <label htmlFor="municipality">Municipio</label>

                            <input
                                id="municipality"
                                type="text"
                                value={draft.municipality}
                                readOnly={readOnly}
                                onChange={update("municipality")}
                            />
                        </div>

                        <div className="report-field">
                            <label htmlFor="neighborhood">Colonia</label>

                            <input
                                id="neighborhood"
                                type="text"
                                value={draft.neighborhood}
                                readOnly={readOnly}
                                onChange={update("neighborhood")}
                            />
                        </div>
                    </div>

                    <div className="postal-row">
                        <div className="report-field postal-field">
                            <label htmlFor="postalCode">Código Postal</label>

                            <input
                                id="postalCode"
                                type="text"
                                inputMode="numeric"
                                pattern="[0-9]*"
                                maxLength={5}
                                value={draft.postalCode}
                                readOnly={readOnly}
                                onChange={update("postalCode")}
                            />
                        </div>
                    </div>
                </div>
            </div>

            {error && <p className="report-form-message">{error}</p>}

            <button
                type={readOnly ? "button" : "submit"}
                className="send-report-button"
                disabled={readOnly}
            >
                Enviar Reporte
            </button>
        </form>
    );
}

export default ReportForm;
