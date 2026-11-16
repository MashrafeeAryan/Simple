package app.simple;

import static org.junit.Assert.*;

import android.content.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.*;
import java.util.zip.*;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DeviceTest {
  Context c;

  @Before
  public void init() {
    c = InstrumentationRegistry.getInstrumentation().getTargetContext();
  }

  @Test
  public void trackerDeduplicatesConfirmedApplications() throws Exception {
    Store s = new Store(c);
    JSONObject before = s.read();
    try {
      s.save(new JSONObject());
      s.track("https://example.org/job", "Role");
      s.track("https://example.org/job", "Role");
      assertEquals(1, s.read().getJSONArray("jobs").length());
    } finally {
      s.save(before);
    }
  }

  @Test
  public void docxExtraction() throws Exception {
    File f = new File(c.getCacheDir(), "test.docx");
    try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(f))) {
      zip.putNextEntry(new ZipEntry("word/document.xml"));
      zip.write(
          "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"><w:body><w:p><w:r><w:t>Java SQL engineer</w:t></w:r></w:p></w:body></w:document>"
              .getBytes());
      zip.closeEntry();
    }
    assertTrue(Documents.extract(c, f).contains("Java SQL engineer"));
    f.delete();
  }

  @Test
  public void scannedPdfOcr() throws Exception {
    File f = new File(c.getCacheDir(), "scan.pdf");
    PdfDocument pdf = new PdfDocument();
    PdfDocument.Page p = pdf.startPage(new PdfDocument.PageInfo.Builder(600, 800, 1).create());
    p.getCanvas().drawColor(Color.WHITE);
    Paint paint = new Paint();
    paint.setColor(Color.BLACK);
    paint.setTextSize(32);
    p.getCanvas().drawText("Java Python Engineer", 30, 100, paint);
    pdf.finishPage(p);
    try (FileOutputStream out = new FileOutputStream(f)) {
      pdf.writeTo(out);
    }
    pdf.close();
    assertTrue(Documents.extract(c, f).toLowerCase().contains("engineer"));
    f.delete();
  }

  @Test
  public void selectedFileProvider() throws Exception {
    File f = new File(c.getFilesDir(), "resume.pdf");
    byte[] before = f.exists() ? java.nio.file.Files.readAllBytes(f.toPath()) : null;
    try {
      java.nio.file.Files.write(f.toPath(), "%PDF-test".getBytes());
      try (InputStream in =
          c.getContentResolver()
              .openInputStream(Uri.parse("content://app.simple.documents/resume.pdf"))) {
        assertEquals("%PDF-test", new String(Io.read(in)));
      }
    } finally {
      if (before == null) f.delete();
      else java.nio.file.Files.write(f.toPath(), before);
    }
  }

  @Test
  public void sessionEncryptionRoundtrip() throws Exception {
    String before = Secrets.read(c);
    try {
      Secrets.save(c, "test-session");
      assertEquals("test-session", Secrets.read(c));
    } finally {
      if (before.isEmpty()) Secrets.clear(c);
      else Secrets.save(c, before);
    }
  }

  @Test
  public void sharedLinkOpensWebViewAndFills() throws Exception {
    android.app.Instrumentation inst = InstrumentationRegistry.getInstrumentation();
    Intent intent =
        new Intent(c, MainActivity.class)
            .setAction(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, "Application https://jobs.lever.co/example/123")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    MainActivity activity = (MainActivity) inst.startActivitySync(intent);
    try {
      assertEquals("https://jobs.lever.co/example/123", activity.activeUrl);
      assertNotNull(activity.web);
      java.util.concurrent.CountDownLatch ready = new java.util.concurrent.CountDownLatch(1);
      java.util.concurrent.atomic.AtomicReference<String> result =
          new java.util.concurrent.atomic.AtomicReference<>();
      inst.runOnMainSync(
          () -> {
            activity.web.stopLoading();
            activity.web.setWebViewClient(
                new android.webkit.WebViewClient() {
                  public void onPageFinished(android.webkit.WebView v, String u) {
                    if (!u.equals("https://jobs.lever.co/example")) return;
                    v.evaluateJavascript(
                        activity.engine
                            + ";SimpleEngine.fill({profile:{email:'alex@example.org'}},"
                            + activity.rules
                            + ");document.querySelector('#fixture').value",
                        value -> {
                          result.set(value);
                          ready.countDown();
                        });
                  }
                });
            activity.web.loadDataWithBaseURL(
                "https://jobs.lever.co/example",
                "<label>Email<input id='fixture' type='email'></label>",
                "text/html",
                "UTF-8",
                null);
          });
      assertTrue(
          "WebView fixture did not finish", ready.await(60, java.util.concurrent.TimeUnit.SECONDS));
      assertEquals("\"alex@example.org\"", result.get());
    } finally {
      inst.runOnMainSync(activity::finish);
    }
  }
}
