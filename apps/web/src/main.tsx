import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './indexLalo.css'
import AppLalo from './AppLalo.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AppLalo/>
  </StrictMode>,

)


