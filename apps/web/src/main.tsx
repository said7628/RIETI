import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './indexMarco.css'
import AppMarco from './AppMarco.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AppMarco/>
  </StrictMode>,

)


