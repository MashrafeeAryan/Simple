const {JSDOM}=require('jsdom');const fs=require('node:fs');const path=require('node:path');
const dir=path.join(__dirname,'../app/src/main/assets');const rules=fs.readdirSync(dir+'/rules').map(f=>JSON.parse(fs.readFileSync(dir+'/rules/'+f)));
exports.page=(html,url='https://jobs.lever.co/example')=>{const dom=new JSDOM(html,{url,runScripts:'outside-only'});dom.window.HTMLElement.prototype.getClientRects=function(){return this.hidden?[]:[{}]};dom.window.eval(fs.readFileSync(dir+'/engine.js','utf8'));return dom.window;};exports.rules=rules;
