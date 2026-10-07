import {Link} from 'react-router-dom';
import {Check} from 'lucide-react';
import {useResource} from '../hooks/useResource';
import {currency} from '../services/api';
import {PageHeading} from '../components/layout/Layout';
import {Loading,ErrorBox} from '../components/common/UI';
import MemberTable from '../components/members/MemberTable';
export default function MembershipPage(){const plans=useResource('/plans'),members=useResource('/members');return <><PageHeading title="Membresías" subtitle="Planes que se adaptan al ritmo de tus socios."/>{plans.loading&&!plans.data&&<Loading/>}{plans.error&&<ErrorBox message={plans.error} retry={plans.reload}/>}<div className="plans">{plans.data?.map(p=><article className={`card plan-card ${p.id==='full'?'featured-plan':''}`} key={p.id}><div className="eyebrow">MEMBRESÍA · {p.days} DÍAS</div><h2>{p.name}</h2><p>{p.description}</p><div className="plan-price">{currency(p.price)} <small>/ periodo</small></div><ul className="benefits">{p.benefits.split('|').map(b=><li key={b}><Check size={14}/>{b}</li>)}</ul><p>{members.data?.filter(m=>m.planId===p.id).length||0} socios en este plan</p><Link className="secondary" to="/socios">Gestionar socios →</Link></article>)}</div>{members.error&&<ErrorBox message={members.error} retry={members.reload}/>} {members.data&&plans.data&&<MemberTable members={members.data} plans={plans.data} onRefresh={members.reload}/>}</>}
