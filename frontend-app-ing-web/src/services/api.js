const base = (import.meta.env.VITE_API_URL || '/api').replace(/\/$/, '');
export function getSession() {try {return JSON.parse(sessionStorage.getItem('pulse-session'))} catch {return null}}
export function saveSession(value) {if(value) sessionStorage.setItem('pulse-session',JSON.stringify(value)); else sessionStorage.removeItem('pulse-session');}
export async function api(path, options = {}) {
  const session=getSession();
  const response=await fetch(base+path,{...options,headers:{...(options.body?{'Content-Type':'application/json'}:{}),...(session?.token?{Authorization:`Bearer ${session.token}`} : {}),...options.headers},body:options.body?JSON.stringify(options.body):undefined});
  const data=await response.json().catch(()=>null);
  if(!response.ok) {
    if(response.status===401 && !path.startsWith('/auth/login')) {saveSession(null);window.dispatchEvent(new Event('pulse-logout'));}
    const error=new Error(data?.message || `Error de API (${response.status})`);error.status=response.status;throw error;
  }
  return data;
}
export const currency=value=>new Intl.NumberFormat('es-CO',{style:'currency',currency:'COP',maximumFractionDigits:0}).format(value||0);
export const formatDate=value=>value?new Intl.DateTimeFormat('es-CO',{day:'2-digit',month:'short',year:'numeric',timeZone:'America/Bogota'}).format(new Date(value.length===10?value+'T12:00:00-05:00':value)):'—';
export const initials=name=>name?.split(' ').map(s=>s[0]).slice(0,2).join('')||'P';
export function exportMembers(members) {
  const rows=[['Nombre','Cédula','Plan','Vencimiento','Estado'],...members.map(m=>[m.name,m.dni,m.planName,m.expiry,m.status])];
  const cell=value=>'"'+String(value??'').replace(/^[=+@-]/,"'"+'$&').replaceAll('"','""')+'"';
  const url=URL.createObjectURL(new Blob(['\uFEFF'+rows.map(r=>r.map(cell).join(';')).join('\r\n')],{type:'text/csv;charset=utf-8'}));
  const link=document.createElement('a');link.href=url;link.download='pulse-socios.csv';link.click();URL.revokeObjectURL(url);
}
