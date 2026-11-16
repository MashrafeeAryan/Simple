const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("hidden",()=>{const w=page("<label>Email<input hidden></label>");w.SimpleEngine.fill({profile:{email:'a@example.com'}},rules);assert.equal(w.document.querySelector('input').value,'');w.close();});
