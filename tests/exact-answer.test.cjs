const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("exact-answer",()=>{const w=page("<label>Why this role?<textarea></textarea></label>");w.SimpleEngine.fill({answers:{'why this role':'My saved answer'}},rules);assert.equal(w.document.querySelector('textarea').value,'My saved answer');w.close();});
