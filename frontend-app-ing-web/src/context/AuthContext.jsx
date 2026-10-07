import {createContext,useContext,useEffect,useState} from 'react';
import {api,getSession,saveSession} from '../services/api';
const AuthContext=createContext(null);
export function AuthProvider({children}) {
  const [session,setSession]=useState(getSession),[loading,setLoading]=useState(true),[validationError,setValidationError]=useState('');
  useEffect(()=>{let active=true;const logout=()=>setSession(null);window.addEventListener('pulse-logout',logout);
    const current=getSession();
    if(!current || new Date(current.expiresAt)<=new Date()){saveSession(null);setSession(null);setLoading(false)}
    else api('/auth/me').then(user=>{if(active){const next={...current,user};saveSession(next);setSession(next)}}).catch(e=>{if(active){setSession(null);if(e.status!==401)setValidationError('No se pudo verificar la sesión. Comprueba la conexión con la API.')}}).finally(()=>{if(active)setLoading(false)});
    return ()=>{active=false;window.removeEventListener('pulse-logout',logout)};
  },[]);
  useEffect(()=>{if(!session)return;const remaining=new Date(session.expiresAt)-new Date();if(remaining<=0){saveSession(null);setSession(null);return}const timer=setTimeout(()=>{saveSession(null);setSession(null)},remaining);return ()=>clearTimeout(timer)},[session]);
  const accept=result=>{saveSession(result);setSession(result);setValidationError('');return result.user};
  const login=async(email,password)=>accept(await api('/auth/login',{method:'POST',body:{email,password}}));
  const register=async body=>accept(await api('/auth/register',{method:'POST',body}));
  const logout=()=>{saveSession(null);setSession(null)};
  return <AuthContext.Provider value={{user:session?.user,session,loading,login,register,logout,validationError}}>{children}</AuthContext.Provider>;
}
export const useAuth=()=>useContext(AuthContext);
export const homeFor=role=>role==='ADMINISTRADOR'?'/dashboard':role==='RECEPCIONISTA'?'/check-in':'/mi-perfil';
