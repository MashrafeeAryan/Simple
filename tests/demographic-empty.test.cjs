const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("demographic-empty",()=>{const w=page("<label>Gender<input></label>");w.SimpleEngine.fill({profile:{firstName:'Alex'}},rules);assert.equal(w.document.querySelector('input').value,'');w.close();});
