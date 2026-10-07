import {useState} from 'react';
import {useSearchParams} from 'react-router-dom';
import {Plus} from 'lucide-react';
import {useResource,useHealth} from '../hooks/useResource';
import {PageHeading} from '../components/layout/Layout';
import {Loading,ErrorBox} from '../components/common/UI';
import MemberTable,{MemberEditor} from '../components/members/MemberTable';
export default function MembersPage(){const members=useResource('/members'),plans=useResource('/plans'),health=useHealth(),[search]=useSearchParams(),[editor,setEditor]=useState(false);return <><PageHeading title="Socios" subtitle="Cada socio cuenta. Gestiona tu comunidad desde aquí."><button className="primary" onClick={()=>setEditor(true)} disabled={!plans.data}><Plus/> Nuevo socio</button></PageHeading>{(members.loading||plans.loading)&&!members.data&&<Loading/>}{members.error&&<ErrorBox message={members.error} retry={members.reload}/>} {plans.error&&<ErrorBox message={plans.error} retry={plans.reload}/>} {members.data&&plans.data&&<MemberTable key={search.get('estado')} members={members.data} plans={plans.data} today={health?.date} initialFilter={search.get('estado')||''} onRefresh={members.reload}/>} {editor&&plans.data&&<MemberEditor plans={plans.data} onClose={()=>setEditor(false)} onSaved={members.reload}/>}</>}
