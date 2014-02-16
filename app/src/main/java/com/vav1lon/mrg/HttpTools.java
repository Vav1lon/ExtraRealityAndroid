package com.vav1lon.mrg;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.vav1lon.AppContext;
import com.vav1lon.mrg.downloader.DownloadRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public final class HttpTools {

    public static String getPageContent(DownloadRequest request, ContentResolver cr) throws Exception {
        String pageContent;
        InputStream is = null;
        if (!request.getSource().getUrl().startsWith("file://")) {
            is = HttpTools.getHttpGETInputStream(request.getSource().getUrl() + request.getParams(), cr);
        } else {
            is = HttpTools.getHttpGETInputStream(request.getSource().getUrl(), cr);
        }
        pageContent = HttpTools.getHttpInputString(is);
        HttpTools.returnHttpInputStream(is);
        return pageContent;
    }


    public static String getHttpInputString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8 * 1024);
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Deprecated
    public static InputStream getHttpGETInputStream(String urlStr, ContentResolver cr) throws Exception {
        InputStream is = null;
        URLConnection conn = null;

        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }

        if (urlStr.startsWith("file://"))
            return new FileInputStream(urlStr.replace("file://", ""));

        if (urlStr.startsWith("content://"))
            return getContentInputStream(urlStr, null, cr);

        if (urlStr.startsWith("https://")) {
            HttpsURLConnection
                    .setDefaultHostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String hostname,
                                              SSLSession session) {
                            return true;
                        }
                    });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context
                    .getSocketFactory());
        }

        try {
            URL url = new URL(urlStr);
            conn = url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);

            is = conn.getInputStream();

            return is;
        } catch (Exception ex) {
            try {
                is.close();
            } catch (Exception ignore) {
                Log.w(AppContext.TAG, "Error on url " + urlStr, ignore);
            }
            try {
                if (conn instanceof HttpURLConnection)
                    ((HttpURLConnection) conn).disconnect();
            } catch (Exception ignore) {

            }
            throw ex;
        }
    }

    @Deprecated
    public static InputStream getContentInputStream(String urlStr, String params, ContentResolver cr)
            throws Exception {
        //ContentResolver cr = mixView.getContentResolver();
        Cursor cur = cr.query(Uri.parse(urlStr), null, params, null, null);

        cur.moveToFirst();
        int mode = cur.getInt(cur.getColumnIndex("MODE"));

        if (mode == 1) {
            String result = cur.getString(cur.getColumnIndex("RESULT"));
            cur.deactivate();

            return new ByteArrayInputStream(result.getBytes());
        } else {
            cur.deactivate();

            throw new Exception("Invalid content:// mode " + mode);
        }
    }

    @Deprecated
    public static void returnHttpInputStream(InputStream is) throws Exception {
        if (is != null) {
            is.close();
        }
    }

}
