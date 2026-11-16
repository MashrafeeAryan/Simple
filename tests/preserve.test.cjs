const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("preserve",()=>{const w=page("<label>Email<input value=\"existing@example.com\"></label>");w.SimpleEngine.fill({profile:{email:'new@example.com'}},rules);assert.equal(w.document.querySelector('input').value,'existing@example.com');w.close();});
