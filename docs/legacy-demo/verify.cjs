const fs = require('fs'), vm = require('vm'), assert = require('assert');
const elements = new Map();
const get = id => { if (!elements.has(id)) elements.set(id, {innerHTML:'',textContent:'',value:'',setAttribute(){},classList:{add(){},remove(){}}}); return elements.get(id); };
const ctx = {document:{getElementById:get,querySelectorAll:()=>[]},localStorage:{getItem:()=>null,setItem(){}},location:{hash:''},window:{addEventListener(){}},Intl,Date,setTimeout,clearTimeout,console,Blob,URL};
vm.createContext(ctx); vm.runInContext(fs.readFileSync('dist/app.js','utf8'),ctx);
for (const name of ['dashboard','socios','membresias','check-in','reportes']) { vm.runInContext(`page='${name}';render()`,ctx); assert(get('content').innerHTML.length>500); }
assert.equal(vm.runInContext('members.filter(m=>status(m)==="Activo").length',ctx),5);
assert.equal(vm.runInContext('status(members[2])',ctx),'Vencido');
assert.equal(vm.runInContext('status(members[4])',ctx),'Inactivo');
assert.equal(vm.runInContext('query="Santiago";filtered().length',ctx),1);
assert.equal(vm.runInContext('query="";filter="expiring";filtered().length',ctx),2);
vm.runInContext('page="check-in";render()',ctx);
const form=get('check-form');form.dni={value:''};form.reset=()=>{};
for(const [dni,result] of [['1023456789','Acceso Permitido'],['1045678901','Membresía Vencida'],['1067890123','Socio Inactivo'],['00000000','Socio no encontrado']]) {form.dni.value=dni;form.onsubmit({preventDefault(){}});assert(get('check-result').innerHTML.includes(result));}
assert.equal(vm.runInContext('entries.length',ctx),1);
console.log('OK: cinco vistas, búsqueda, estados, vencimientos y cuatro resultados de check-in');
