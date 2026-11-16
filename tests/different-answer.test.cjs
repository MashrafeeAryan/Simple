const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("different-answer",()=>{const w=page("<label>Why this company?<textarea></textarea></label>");w.SimpleEngine.fill({answers:{'why this role':'My saved answer'}},rules);assert.equal(w.document.querySelector('textarea').value,'');w.close();});
