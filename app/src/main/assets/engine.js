(() => {
'use strict';
const norm = s => String(s || '').normalize('NFKC').toLowerCase().replace(/[^\p{L}\p{N}]+/gu,' ').trim();
const exact = s => String(s||'').normalize('NFKC').toLowerCase().replace(/\s+/g,' ').trim();
if(!window.__simpleEditedFields) {
 window.__simpleEditedFields=new WeakSet();
 const remember=e=>{if(e.isTrusted && e.target.matches('input,select,textarea'))window.__simpleEditedFields.add(e.target);};
 document.addEventListener('input',remember,true);document.addEventListener('change',remember,true);
}
const visible = e => !window.__simpleEditedFields.has(e) && !e.disabled && !e.readOnly && e.getClientRects().length > 0;
const labelText = x => {const copy=x.cloneNode(true);copy.querySelectorAll('input,select,textarea').forEach(e=>e.remove());return copy.textContent;};
const radioQuestion = e => {const f=e.type==='radio' && e.closest('fieldset');return f && f.querySelector('legend') ? f.querySelector('legend').textContent : '';};
const label = e => [radioQuestion(e),e.labels && [...e.labels].map(labelText).join(' '), e.getAttribute('aria-label'),(e.getAttribute('aria-labelledby')||'').split(/\s+/).map(id=>(document.getElementById(id)||{}).textContent).join(' '),e.placeholder,e.name,e.id].filter(Boolean);
const entrySelector='fieldset,[data-simple-entry],[data-automation-id^="workExperience-"],[data-automation-id^="education-"]';
const section = e => {
 const wd=e.closest('[data-automation-id^="workExperience-"],[data-automation-id^="education-"]');
 if(wd) return norm(wd.getAttribute('data-automation-id'));
 const p = e.closest('fieldset,[data-simple-section],section,[data-automation-id="workExperience"],[data-automation-id="education"]');
 return norm(p ? (p.getAttribute('data-simple-section') || (p.querySelector('legend,h2,h3,h4')||{}).textContent || p.getAttribute('data-automation-id')) : '');
};
const group = e => e.closest(entrySelector) || e.form || document.body;
function value(e,v) {
 if(v === undefined || v === null || v === '') return false;
 if(e.type==='file' || e.type==='password' || e.type==='hidden' || e.type==='submit') return false;
 if(e.type==='checkbox' || e.type==='radio') {
  if(e.type==='checkbox' && e.checked) return false;
  if(e.type==='radio' && e.name && [...document.querySelectorAll('input[type="radio"]')].some(r=>r.name===e.name && r.form===e.form && r.checked)) return false;
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
  if(key && key.includes('.')) {
   const [k,field]=key.split('.'),g=group(e); let idx=groups[k].indexOf(g);if(idx<0){groups[k].push(g);idx=groups[k].length-1;}
   v=((data[k]||[])[idx]||{})[field];
  } else if(key) v=(data.profile||{})[key];
  if(v===undefined) {
   const q=radioQuestion(e) || ((e.labels||[])[0]?labelText(e.labels[0]):null) || e.getAttribute('aria-label');
   if(q) v=(data.answers||{})[exact(q)];
  }
  if(value(e,v)) count++;
 }
 if(!window.__simpleLastExpand || Date.now()-window.__simpleLastExpand>3000) {
  for(const kind of ['work','education']) {
   if(groups[kind].length && ((data[kind]||[]).length||0)>groups[kind].length) {
    const names=kind==='work'?['add work experience','add another work experience']:['add education','add another education'];
    const buttons=[...document.querySelectorAll('button')].filter(b=>visible(b)&&names.includes(norm(b.textContent)));
    if(buttons.length===1){window.__simpleLastExpand=Date.now();buttons[0].click();break;}
   }
  }
 }
 return count;
}
const platform = host => /(^|\.)myworkdayjobs\.com$/.test(host)?'workday':/(^|\.)greenhouse\.io$/.test(host)?'greenhouse':/(^|\.)ashbyhq\.com$/.test(host)?'ashby':/(^|\.)lever\.co$/.test(host)?'lever':'general';
if(!window.__simpleSubmissionObserver) {
 window.__simpleSubmissionObserver=true;
 const mark=()=>sessionStorage.setItem('simple-submit-intent',String(Date.now()));
 document.addEventListener('submit',mark,true);
 document.addEventListener('click',e=>{const b=e.target.closest('button,input[type="submit"]');if(b && /^(submit|submit application|send application|apply)$/.test(norm(b.textContent||b.value))) mark();},true);
}
const confirmed = () => {
 const intent=Number(sessionStorage.getItem('simple-submit-intent')||0);
 if(!intent || Date.now()-intent>600000) return false;
 const p=platform(location.hostname); const t=norm(document.body.textContent);
 const messages={workday:['application submitted','thank you for applying'],greenhouse:['your application has been submitted','thank you for applying'],ashby:['application submitted','your application has been submitted'],lever:['application submitted','thank you for applying']};
 return (messages[p]||[]).some(m=>t.includes(m)) && !document.querySelector('input[type="email"],input[type="file"]');
};
window.SimpleEngine={fill,norm,platform,confirmed};
})();
