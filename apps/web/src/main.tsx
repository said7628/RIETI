import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import './indexLalo.css'
import AppLalo from './AppLalo.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <AppLalo />
    </BrowserRouter>
  </StrictMode>,
)

