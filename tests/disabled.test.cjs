const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("disabled",()=>{const w=page("<label>Email<input disabled></label>");w.SimpleEngine.fill({profile:{email:'a@example.com'}},rules);assert.equal(w.document.querySelector('input').value,'');w.close();});
