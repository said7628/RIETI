import rietiLogo from '../../assets/rieti-logo.png'
import worldVisionLogo from '../../assets/world-vision.png'

/** Pie de Seguimiento, con los logos de SIPINNA y World Vision. */
function Footer() {
  return (
    <footer className="footer">
      <div className="footer-brand">
        <img src={rietiLogo} alt="" />
        <span>SIPINNA</span>
      </div>

      <div className="footer-brand">
        <span>World Vision</span>
        <img src={worldVisionLogo} alt="" />
      </div>
    </footer>
  )
}

export default Footer
