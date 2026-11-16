package app.simple;

import android.content.*;
import android.database.*;
import android.net.Uri;
import android.os.*;
import android.provider.OpenableColumns;
import java.io.*;

public class DocumentProvider extends ContentProvider {
  public boolean onCreate() {
    return true;
  }

  private File file(Uri u) throws FileNotFoundException {
    String n = u.getLastPathSegment();
    if (!"resume.pdf".equals(n)
        && !"resume.docx".equals(n)
        && !"cover.pdf".equals(n)
        && !"cover.docx".equals(n)) throw new FileNotFoundException();
    return new File(getContext().getFilesDir(), n);
  }

  public String getType(Uri u) {
    return u.toString().endsWith(".pdf")
        ? "application/pdf"
        : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
  }

  public Cursor query(Uri u, String[] p, String s, String[] a, String sort) {
    try {
      File f = file(u);
      MatrixCursor c =
          new MatrixCursor(new String[] {OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE});
      c.addRow(new Object[] {f.getName(), f.length()});
      return c;
    } catch (Exception e) {
      return null;
    }
  }

  public ParcelFileDescriptor openFile(Uri u, String mode) throws FileNotFoundException {
    if (!"r".equals(mode)) throw new FileNotFoundException();
    return ParcelFileDescriptor.open(file(u), ParcelFileDescriptor.MODE_READ_ONLY);
  }

  public Uri insert(Uri u, ContentValues v) {
    throw new UnsupportedOperationException();
  }

  public int delete(Uri u, String s, String[] a) {
    throw new UnsupportedOperationException();
  }

  public int update(Uri u, ContentValues v, String s, String[] a) {
    throw new UnsupportedOperationException();
  }
}
