function Waves() {
  return (
    <div className="waves-container">
      {/* Yo uso SVG para que las ondas se adapten al ancho de la laptop sin perder calidad. */}
      <svg
        className="waves"
        viewBox="0 0 1920 250"
        preserveAspectRatio="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        {/* Esta es la onda más oscura y queda detrás de las demás. */}
        <path
          className="wave wave-back"
          d="
            M0 45
            C250 -20, 430 10, 650 120
            C890 240, 1110 225, 1320 105
            C1510 0, 1710 -5, 1920 60
            L1920 250
            L0 250
            Z
          "
        />

        {/* Esta onda intermedia tiene una caída diferente para que no se vea tan simétrica. */}
        <path
          className="wave wave-middle"
          d="
            M0 90
            C240 15, 450 40, 680 155
            C900 265, 1130 245, 1350 130
            C1550 25, 1740 25, 1920 100
            L1920 250
            L0 250
            Z
          "
        />

        {/* Esta es la onda del frente y conecta con la siguiente sección. */}
        <path
          className="wave wave-front"
          d="
            M0 140
            C250 65, 470 75, 700 190
            C930 300, 1140 275, 1370 170
            C1580 70, 1750 70, 1920 145
            L1920 250
            L0 250
            Z
          "
        />
      </svg>
    </div>
  )
}

export default Waves