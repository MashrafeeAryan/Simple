package app.simple;
import java.net.*;import java.io.*;import org.json.*;import java.nio.charset.StandardCharsets;
final class Supabase {
 static JSONObject request(String base,String key,String token,String path,String method,JSONObject body)throws Exception {
  URI uri=URI.create(base);if(!"https".equals(uri.getScheme())||uri.getHost()==null)throw new IOException("Enter an HTTPS Supabase project URL");
  HttpURLConnection c=(HttpURLConnection)new URL(base+path).openConnection();c.setInstanceFollowRedirects(false);c.setConnectTimeout(15000);c.setReadTimeout(20000);c.setRequestMethod(method);c.setRequestProperty("apikey",key);c.setRequestProperty("Authorization","Bearer "+(token.isEmpty()?key:token));c.setRequestProperty("Content-Type","application/json");c.setRequestProperty("Prefer","resolution=merge-duplicates,return=minimal");
  if(body!=null){c.setDoOutput(true);try(OutputStream o=c.getOutputStream()){o.write(body.toString().getBytes(StandardCharsets.UTF_8));}}
  int code=c.getResponseCode();InputStream stream=code<400?c.getInputStream():c.getErrorStream();String text=stream==null?"":new String(Io.read(stream),StandardCharsets.UTF_8);c.disconnect();if(code>=300)throw new IOException("Supabase request failed ("+code+"). Check configuration, sign-in, and project policies.");return text.isBlank()?new JSONObject():text.startsWith("[")?new JSONObject().put("rows",new JSONArray(text)):new JSONObject(text);
 }
}
