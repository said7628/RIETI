import heatmapIcon from '../../assets/heatmap.png'

function HeatMapSection() {
  return (
    <section className="heatmap-section">
      <div className="section-label">
        <img src={heatmapIcon} alt="" />
        <span>Mapa de calor</span>
      </div>

      {/*
        Yo dejo este espacio reservado porque el mapa de calor
        se va a implementar después cuando definamos cómo funcionará.
      */}
      <div className="heatmap-placeholder"></div>
    </section>
  )
}

export default HeatMapSection