package app.simple;
import android.content.Context;import org.json.*;import java.net.*;import java.io.*;
final class CloudDocuments {
 static void sync(Context c,String base,String key,String token,String user,JSONObject data,boolean download)throws Exception {
  if(token.isEmpty()||user.isEmpty())throw new IOException("Sign in first");
  for(String kind:new String[]{"resume","cover"}){
   String name=data.optString(kind+"File");if(name.isEmpty())continue;
   if(!name.matches("(resume|cover)\\.(pdf|docx)"))throw new IOException("Invalid document name");
   if(!base.startsWith("https://"))throw new IOException("HTTPS required");
   HttpURLConnection conn=(HttpURLConnection)new URL(base+"/storage/v1/object/documents/"+user+"/"+name).openConnection();conn.setConnectTimeout(15000);conn.setReadTimeout(30000);conn.setRequestProperty("apikey",key);conn.setRequestProperty("Authorization","Bearer "+token);File file=new File(c.getFilesDir(),name);
   if(!download){if(!file.exists())continue;conn.setRequestMethod("POST");conn.setRequestProperty("x-upsert","true");conn.setRequestProperty("Content-Type",name.endsWith("pdf")?"application/pdf":"application/vnd.openxmlformats-officedocument.wordprocessingml.document");conn.setDoOutput(true);try(OutputStream out=conn.getOutputStream();InputStream in=new FileInputStream(file)){Io.copy(in,out);}}
   int code=conn.getResponseCode();if(code>=400){conn.disconnect();throw new IOException("Document sync failed ("+code+")");}
   if(download){File tmp=new File(c.getCacheDir(),"download.tmp");try(InputStream in=conn.getInputStream();OutputStream out=new FileOutputStream(tmp)){byte[] b=new byte[8192];long size=0;int n;while((n=in.read(b))!=-1){size+=n;if(size>20971520)throw new IOException("Cloud document too large");out.write(b,0,n);}}java.nio.file.Files.move(tmp.toPath(),file.toPath(),java.nio.file.StandardCopyOption.REPLACE_EXISTING);}
   conn.disconnect();
  }
 }
}
