package app.simple;
import android.content.*;
import org.json.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
final class Store {
 final Context context;
 Store(Context c){context=c;}
 synchronized JSONObject read(){try{return new JSONObject(Files.readString(new java.io.File(context.getFilesDir(),"data.json").toPath()));}catch(Exception e){return new JSONObject();}}
 synchronized void save(JSONObject data)throws Exception {
  Path tmp=new java.io.File(context.getFilesDir(),"data.tmp").toPath();
  Files.writeString(tmp,data.toString(),StandardCharsets.UTF_8);
  Files.move(tmp,new java.io.File(context.getFilesDir(),"data.json").toPath(),StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);
 }
 synchronized void track(String url,String title)throws Exception {
  JSONObject data=read(); JSONArray jobs=data.optJSONArray("jobs");if(jobs==null)jobs=new JSONArray();
  for(int i=0;i<jobs.length();i++)if(jobs.getJSONObject(i).optString("url").equals(url))return;
  jobs.put(new JSONObject().put("url",url).put("title",title).put("confirmedAt",System.currentTimeMillis()));data.put("jobs",jobs);save(data);
 }
}
