const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test('C++ question cannot reuse a C question answer',()=>{const w=page('<label>Do you use C++?<textarea></textarea></label>');w.SimpleEngine.fill({answers:{'do you use c?':'Yes'}},rules);assert.equal(w.document.querySelector('textarea').value,'');w.close()});
