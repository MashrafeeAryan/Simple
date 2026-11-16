const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("contact",()=>{const w=page("<label>Email<input type=\"email\"></label>");w.SimpleEngine.fill({profile:{email:'a@example.com'}},rules);assert.equal(w.document.querySelector('input').value,'a@example.com');w.close();});
