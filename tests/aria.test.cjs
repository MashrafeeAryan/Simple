const {test}=require('node:test');const assert=require('node:assert/strict');const {page,rules}=require('./harness.cjs');
test("aria",()=>{const w=page("<input aria-label=\"LinkedIn profile\">");w.SimpleEngine.fill({profile:{linkedin:'https://linkedin.com/in/example'}},rules);assert.equal(w.document.querySelector('input').value,'https://linkedin.com/in/example');w.close();});
