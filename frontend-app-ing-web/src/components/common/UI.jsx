import {useEffect,useRef} from 'react';
import {AlertTriangle,CheckCircle2,X,RefreshCw} from 'lucide-react';
export function ErrorBox({message,retry}){return <div className="alert error" role="alert"><AlertTriangle/><div><strong>{message}</strong>{retry&&<button className="text-button" onClick={retry}>Reintentar</button>}</div></div>}
export function Loading(){return <div className="loading"><RefreshCw size={18}/> Cargando información…</div>}
export function Badge({status}){return <span className={`badge ${status}`}><i/>{status}</span>}
export function Modal({title,children,onClose,wide=false}) {const ref=useRef(null);useEffect(()=>{ref.current.showModal()},[]);return <dialog className={wide?'wide-dialog':''} ref={ref} onCancel={onClose}><div className="dialog-heading"><h2>{title}</h2><button className="icon-button" onClick={onClose} aria-label="Cerrar ventana"><X/></button></div>{children}</dialog>}
export function Alert({allowed,title,message}) {const Icon=allowed?CheckCircle2:AlertTriangle;return <div className={`alert ${allowed?'':'error'}`} role="status"><Icon/><div><strong>{title}</strong><small>{message}</small></div></div>}
