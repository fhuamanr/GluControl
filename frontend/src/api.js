const BASE=(import.meta.env.VITE_API_URL||'/api').replace(/\/$/,'');
export async function api(path,options={}){
  const token=JSON.parse(localStorage.getItem('glucontrol-session')||'{}').token;
  const response=await fetch(`${BASE}${path}`,{...options,headers:{'Content-Type':'application/json',...(token?{Authorization:`Bearer ${token}`}:{ }),...(options.headers||{})}});
  if(!response.ok){let data={};try{data=await response.json()}catch{}throw new Error(data.message||`Error ${response.status}`)}
  return response.status===204?null:response.json();
}
export async function upload(path,file){
  const token=JSON.parse(localStorage.getItem('glucontrol-session')||'{}').token;
  const body=new FormData();body.append('file',file);
  const response=await fetch(`${BASE}${path}`,{method:'POST',body,headers:token?{Authorization:`Bearer ${token}`}:{}});
  if(!response.ok){let data={};try{data=await response.json()}catch{}const error=new Error(data.message||`Error ${response.status}`);error.status=response.status;throw error}
  return response.json();
}
export const patientId=()=>JSON.parse(localStorage.getItem('glucontrol-session')||'{}').patientId;
