(() => {
'use strict';
const norm = s => String(s || '').normalize('NFKC').toLowerCase().replace(/[^\p{L}\p{N}]+/gu,' ').trim();
const exact = s => norm(s).replace(/\s+/g,' ');
const visible = e => !e.disabled && !e.readOnly && e.getClientRects().length > 0;
const labelText = x => {const copy=x.cloneNode(true);copy.querySelectorAll('input,select,textarea').forEach(e=>e.remove());return copy.textContent;};
const label = e => [e.labels && [...e.labels].map(labelText).join(' '), e.getAttribute('aria-label'),(e.getAttribute('aria-labelledby')||'').split(/\s+/).map(id=>document.getElementById(id)?.textContent).join(' '),e.placeholder,e.name,e.id].filter(Boolean);
const section = e => {
 const p = e.closest('fieldset,[data-simple-section],section,[data-automation-id="workExperience"],[data-automation-id="education"]');
 return norm(p ? (p.getAttribute('data-simple-section') || p.querySelector('legend,h2,h3,h4')?.textContent || p.getAttribute('data-automation-id')) : '');
};
const group = e => e.closest('fieldset,[data-simple-entry],[data-automation-id="workExperience"],[data-automation-id="education"]') || e.form || document.body;
function value(e,v) {
 if(v === undefined || v === null || v === '') return false;
 if(e.type==='file' || e.type==='password' || e.type==='hidden' || e.type==='submit') return false;
 if(e.type==='checkbox' || e.type==='radio') {
  const desired = typeof v==='boolean' ? v : /^(yes|true)$/i.test(String(v));
  if(e.type==='radio' && norm(e.value)!==norm(v) && norm(e.getAttribute('aria-label'))!==norm(v)) return false;
  if(e.checked!==desired && e.type==='checkbox') e.click();
  else if(e.type==='radio' && !e.checked) e.click();
 } else if(e.tagName==='SELECT') {
  const options=[...e.options].filter(o=>norm(o.textContent)===norm(v)||norm(o.value)===norm(v));
  if(options.length!==1) return false;
  e.value=options[0].value;
 } else {
  const proto=e.tagName==='TEXTAREA'?HTMLTextAreaElement.prototype:HTMLInputElement.prototype;
  Object.getOwnPropertyDescriptor(proto,'value').set.call(e,String(v));
 }
 e.dispatchEvent(new Event('input',{bubbles:true}));e.dispatchEvent(new Event('change',{bubbles:true})); return true;
}
function fill(data,rules) {
 let count=0; const groups={work:[],education:[]};
 for(const e of document.querySelectorAll('input,select,textarea')) {
  const sec=section(e),kind=/education|school|academic/.test(sec)?'education':/work|employment|experience/.test(sec)?'work':null;
  if(kind && !groups[kind].includes(group(e))) groups[kind].push(group(e));
 }
 for(const e of document.querySelectorAll('input,select,textarea')) {
  if(!visible(e) || (e.value && !['checkbox','radio'].includes(e.type))) continue;
  const labels=label(e).map(norm); const sec=section(e);let key;
  const kind=/education|school|academic/.test(sec)?'education':/work|employment|experience/.test(sec)?'work':null;
  const candidates=rules.filter(r=>(r.key.includes('.')?r.key.startsWith(kind+'.'):true)&&r.aliases.some(a=>labels.includes(norm(a))));
  if(candidates.length===1) key=candidates[0].key;
  let v;
  if(key?.includes('.')) {
   const [k,field]=key.split('.'),g=group(e); let idx=groups[k].indexOf(g);if(idx<0){groups[k].push(g);idx=groups[k].length-1;}
   v=data[k]?.[idx]?.[field];
  } else if(key) v=data.profile?.[key];
  if(v===undefined) {
   const q=e.labels?.[0]?.textContent || e.getAttribute('aria-label');
   if(q) v=data.answers?.[exact(q)];
  }
  if(value(e,v)) count++;
 }
 return count;
}
const platform = host => /(^|\.)myworkdayjobs\.com$/.test(host)?'workday':/(^|\.)greenhouse\.io$/.test(host)?'greenhouse':/(^|\.)ashbyhq\.com$/.test(host)?'ashby':/(^|\.)lever\.co$/.test(host)?'lever':'general';
const confirmed = () => {
 const p=platform(location.hostname); const t=norm(document.body.textContent);
 const messages={workday:['application submitted','thank you for applying'],greenhouse:['your application has been submitted','thank you for applying'],ashby:['application submitted','your application has been submitted'],lever:['application submitted','thank you for applying']};
 return messages[p]?.some(m=>t.includes(m)) && !document.querySelector('input[type="email"],input[type="file"]');
};
window.SimpleEngine={fill,norm,platform,confirmed};
})();
