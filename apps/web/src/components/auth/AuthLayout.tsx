import type { ReactNode } from "react";
import spinnaLogo from "../../assets/spinna-logo.png";
import "../../pages/Login.css";

function AuthLayout({ children }: { children: ReactNode }) {
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

            <main className="login-content">{children}</main>

            <footer className="login-footer">
                <div className="spinna">
                    <div className="spinna-logo">
                        <img
                            src={spinnaLogo}
                            alt="SIPINNA"
                            className="spinna-image"
                        />
                    </div>
                    <span>SIPINNA</span>
                </div>

                <div className="world-vision">
                    <span>World Vision</span>
                    <div className="world-vision-icon">✦</div>
                </div>
            </footer>
        </div>
    );
}

export default AuthLayout;
