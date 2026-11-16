const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("text-cover",()=>{const w=page("<label>Cover letter<textarea></textarea></label>");w.SimpleEngine.fill({profile:{coverText:'Dear hiring team'}},rules);assert.equal(w.document.querySelector('textarea').value,'Dear hiring team');w.close();});
