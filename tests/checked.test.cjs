const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test('saved authorization never unchecks a user answer',()=>{const w=page('<label>Authorized to work<input type="checkbox" checked></label>');w.SimpleEngine.fill({profile:{authorized:'No'}},rules);assert.equal(w.document.querySelector('input').checked,true);w.close()});
