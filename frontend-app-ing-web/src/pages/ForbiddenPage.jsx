import {Link} from 'react-router-dom';
import {ShieldX} from 'lucide-react';
import {useAuth,homeFor} from '../context/AuthContext';
export default function ForbiddenPage(){const {user}=useAuth();return <div className="forbidden"><ShieldX/><div className="eyebrow">403 · ACCESO RESTRINGIDO</div><h1>Esta área requiere otro permiso.</h1><p>Tu rol es {user?.role.toLowerCase()}. Puedes continuar en tu espacio autorizado.</p><Link className="primary" to={homeFor(user?.role)}>Volver a mi espacio →</Link></div>}
