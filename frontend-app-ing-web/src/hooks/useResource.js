import {useCallback,useEffect,useState} from 'react';
import {api} from '../services/api';
export function useResource(path) {
  const [data,setData]=useState(null),[error,setError]=useState(''),[loading,setLoading]=useState(true);
  const reload=useCallback(async()=>{setLoading(true);setError('');try {setData(await api(path))}catch(e){setError(e.message)}finally{setLoading(false)}},[path]);
  useEffect(()=>{reload()},[reload]);
  return {data,error,loading,reload};
}
export function useHealth(){const [health,setHealth]=useState(null);useEffect(()=>{let active=true;const update=()=>api('/health').then(r=>{if(active)setHealth(r)}).catch(()=>{if(active)setHealth({status:'OFFLINE'})});update();const id=setInterval(update,15000);return ()=>{active=false;clearInterval(id)}},[]);return health;}
