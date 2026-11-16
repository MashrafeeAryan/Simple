const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("demographic-explicit",()=>{const w=page("<label>Gender<input></label>");w.SimpleEngine.fill({profile:{gender:'Prefer not to say'}},rules);assert.equal(w.document.querySelector('input').value,'Prefer not to say');w.close();});
