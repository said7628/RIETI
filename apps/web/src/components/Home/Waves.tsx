function Waves() {
  return (
    <div className="waves-container">
      {/* Yo uso SVG para que las ondas se adapten al ancho de la laptop sin perder calidad. */}
      <svg
        className="waves"
        viewBox="0 0 1920 360"
        preserveAspectRatio="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        {/* Esta es la onda más oscura y queda detrás de las demás. */}
        <path
          className="wave wave-back"
          d="
            M0 85
            C250 -5, 430 10, 650 135
            C880 265, 1090 255, 1320 130
            C1530 15, 1720 0, 1920 95
            L1920 380
            L0 380
            Z
          "
        />

        {/* Dejo más espacio entre esta onda y la de atrás para hacerla más gruesa. */}
        <path
          className="wave wave-middle"
          d="
            M0 155
            C250 65, 450 75, 680 200
            C900 325, 1120 310, 1350 190
            C1550 80, 1740 70, 1920 165
            L1920 380
            L0 380
            Z
          "
        />

        {/* Esta es la onda frontal y también tiene más separación para que se vea más gordita. */}
        <path
          className="wave wave-front"
          d="
            M0 230
            C250 140, 470 145, 700 270
            C920 390, 1140 375, 1370 260
            C1580 150, 1760 145, 1920 235
            L1920 380
            L0 380
            Z
          "
        />
      </svg>
    </div>
  )
}

export default Waves