import {BrowserRouter,Routes,Route,Navigate,Link} from 'react-router-dom';
import {AuthProvider,useAuth,homeFor} from './context/AuthContext';
import ProtectedRoute from './components/auth/ProtectedRoute';
import Layout from './components/layout/Layout';
import {LoginPage,RegisterPage} from './pages/AuthPages';
import DashboardPage from './pages/DashboardPage';
import MembersPage from './pages/MembersPage';
import MembershipPage from './pages/MembershipPage';
import CheckInPage from './pages/CheckInPage';
import ReportsPage from './pages/ReportsPage';
import PaymentsPage from './pages/PaymentsPage';
import ProfilePage from './pages/ProfilePage';
import {StaffPage,SecurityPage} from './pages/AdminPages';
import ForbiddenPage from './pages/ForbiddenPage';
const staff=['ADMINISTRADOR','RECEPCIONISTA'];
function Home(){const {user,loading}=useAuth();if(loading)return <div className="full-loading">Verificando sesión…</div>;return <Navigate to={user?homeFor(user.role):'/login'} replace/>}
export default function App(){return <BrowserRouter><AuthProvider><Routes><Route path="/" element={<Home/>}/><Route path="/login" element={<LoginPage/>}/><Route path="/registro" element={<RegisterPage/>}/><Route element={<ProtectedRoute/>}><Route element={<Layout/>}><Route path="/403" element={<ForbiddenPage/>}/><Route element={<ProtectedRoute roles={['ADMINISTRADOR']}/>}><Route path="/dashboard" element={<DashboardPage/>}/><Route path="/reportes" element={<ReportsPage/>}/><Route path="/personal" element={<StaffPage/>}/><Route path="/seguridad" element={<SecurityPage/>}/></Route><Route element={<ProtectedRoute roles={staff}/>}><Route path="/socios" element={<MembersPage/>}/><Route path="/membresias" element={<MembershipPage/>}/><Route path="/check-in" element={<CheckInPage/>}/><Route path="/pagos" element={<PaymentsPage/>}/></Route><Route element={<ProtectedRoute roles={['SOCIO']}/>}><Route path="/mi-perfil" element={<ProfilePage/>}/></Route></Route></Route><Route path="*" element={<div className="forbidden"><h1>Página no encontrada</h1><Link className="primary" to="/">Volver al inicio</Link></div>}/></Routes></AuthProvider></BrowserRouter>}
