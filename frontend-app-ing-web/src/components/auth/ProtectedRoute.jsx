import {Navigate,Outlet} from 'react-router-dom';
import {useAuth} from '../../context/AuthContext';
export default function ProtectedRoute({roles}) {const {user,loading}=useAuth();if(loading)return <div className="full-loading">Verificando tu sesión…</div>;if(!user)return <Navigate to="/login" replace/>;if(roles&&!roles.includes(user.role))return <Navigate to="/403" replace/>;return <Outlet/>;}
