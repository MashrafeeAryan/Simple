package app.simple;
import android.content.*;import android.net.Uri;import android.graphics.*;import android.graphics.pdf.PdfRenderer;import android.os.ParcelFileDescriptor;
import com.tom_roush.pdfbox.pdmodel.PDDocument;import com.tom_roush.pdfbox.text.PDFTextStripper;import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.google.mlkit.vision.text.*;import com.google.mlkit.vision.text.latin.TextRecognizerOptions;import com.google.mlkit.vision.common.InputImage;import com.google.android.gms.tasks.Tasks;
import java.io.*;import java.util.zip.*;
final class Documents {
 static String extract(Context c,File f)throws Exception {
  if(f.getName().endsWith(".docx"))try(ZipFile zip=new ZipFile(f)){ZipEntry e=zip.getEntry("word/document.xml");if(e==null)throw new IOException("Invalid DOCX");try(InputStream in=zip.getInputStream(e)){org.xmlpull.v1.XmlPullParser p=android.util.Xml.newPullParser();p.setInput(in,"UTF-8");StringBuilder out=new StringBuilder();while(p.next()!=org.xmlpull.v1.XmlPullParser.END_DOCUMENT){if(p.getEventType()==org.xmlpull.v1.XmlPullParser.TEXT)out.append(p.getText()).append(' ');}return out.toString();}}
  PDFBoxResourceLoader.init(c);String text;try(PDDocument d=PDDocument.load(f)){text=new PDFTextStripper().getText(d);}if(text.trim().length()>40)return text;
  StringBuilder out=new StringBuilder();TextRecognizer recognizer=TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
  try(ParcelFileDescriptor fd=ParcelFileDescriptor.open(f,ParcelFileDescriptor.MODE_READ_ONLY);PdfRenderer renderer=new PdfRenderer(fd)){
   if(renderer.getPageCount()>50)throw new IOException("OCR supports up to 50 pages");
   for(int i=0;i<renderer.getPageCount();i++)try(PdfRenderer.Page page=renderer.openPage(i)){
    float scale=Math.min(2f,2000f/Math.max(page.getWidth(),page.getHeight()));Bitmap image=Bitmap.createBitmap((int)(page.getWidth()*scale),(int)(page.getHeight()*scale),Bitmap.Config.ARGB_8888);image.eraseColor(Color.WHITE);page.render(image,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);try{out.append(Tasks.await(recognizer.process(InputImage.fromBitmap(image,0))).getText()).append('\n');}finally{image.recycle();}
   }
  }finally{recognizer.close();}return out.toString();
 }
 static File copy(Context c,Uri uri,String kind)throws Exception {
  String mime=c.getContentResolver().getType(uri);String ext="application/pdf".equals(mime)?"pdf":"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mime)?"docx":null;if(ext==null)throw new IOException("Choose PDF or DOCX");
  File f=new File(c.getFilesDir(),kind+"."+ext),tmp=new File(c.getCacheDir(),"import.tmp");try(InputStream in=c.getContentResolver().openInputStream(uri);OutputStream out=new FileOutputStream(tmp)){byte[] b=new byte[8192];int n;long size=0;while((n=in.read(b))!=-1){size+=n;if(size>20*1024*1024)throw new IOException("Document exceeds 20 MB");out.write(b,0,n);}}java.nio.file.Files.move(tmp.toPath(),f.toPath(),java.nio.file.StandardCopyOption.REPLACE_EXISTING);return f;
 }
}
