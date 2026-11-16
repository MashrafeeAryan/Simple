const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("unknown-select",()=>{const w=page("<label>Country<select><option></option><option>Canada</option></select></label>");w.SimpleEngine.fill({profile:{country:'United States'}},rules);assert.equal(w.document.querySelector('select').value,'');w.close();});
