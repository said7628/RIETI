import type { Report } from '../../data/shared/reportsData'

const AGE_HEADING = /^\s*Edades aproximadas observadas:\s*(.*)$/im
const AGE_ITEM = /(0-5|6-11|12-14|15-17)\s*años\s*:\s*(\d+)/gi

/** Lee el desglose escrito por Android sin modificar el esquema anterior de reportes. */
export function extractReportAges(description: string | null | undefined) {
  const line = (description ?? '').match(AGE_HEADING)?.[1] ?? ''
  const counts: Array<{ range: string; count: number }> = []
  for (const match of line.matchAll(AGE_ITEM)) {
    const count = Number(match[2])
    if (Number.isSafeInteger(count) && count > 0) {
      counts.push({ range: match[1], count })
    }
  }
  return counts
}

export function descriptionWithoutAgeHeader(description: string | null | undefined) {
  return (description ?? '').replace(/^\s*Edades aproximadas observadas:[^\r\n]*(?:\r?\n)?/im, '').trim()
}

export default function ReportAgeBreakdown({ report }: { report: Report }) {
  const ages = extractReportAges(report.description)
  if (!ages.length) return <span>{report.ageRange} años</span>

  return (
    <span className="report-age-breakdown" aria-label="Desglose aproximado de edades">
      {ages.map(({ range, count }) => (
        <span key={range} className="report-age-item">
          {range} años: <strong>{count}</strong>
        </span>
      ))}
    </span>
  )
}
