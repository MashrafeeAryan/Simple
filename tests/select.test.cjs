const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("select",()=>{const w=page("<label>Country<select><option></option><option value=\"US\">United States</option></select></label>");w.SimpleEngine.fill({profile:{country:'United States'}},rules);assert.equal(w.document.querySelector('select').value,'US');w.close();});
