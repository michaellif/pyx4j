package com.birchwoodsoftwaregroup.callfiretest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class App {

    private static final String APIKEY = "690124bd5ad1c562d56ffbaadb7f94139ab82fb6";
    private static final String WEBSERVER_HOST = "www.callfire.com";
    private static final int WEBSERVER_PORT = 0;
    private static final String WEBSERVER_CONTEXT = "/cloud/1/";
    private static final String CAMPAIGN_XML = "<dialplan name=\"Root\"><play name=\"play\" type=\"tts\"  voice=\"male1\">Dear ${call.field.b} ${call.field.c}. This is a call from CallFire done automatically through REST API. The text of message itself is constant but your first and second names are substituted as parameters.</play></dialplan>";

    public static void main(String[] args) throws URISyntaxException, IOException, NoSuchAlgorithmException, KeyManagementException {

        HttpClient httpclient = createTrustingAllHttpClient();
        
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        URI uri;
        HttpResponse response;
        HttpPost request;

        String campaignid = null;

        // Create campaign
        uri = URIUtils.createURI("https", WEBSERVER_HOST, WEBSERVER_PORT, WEBSERVER_CONTEXT + "callfirexml/campaign", null, null);
        request = new HttpPost(uri);
        params.clear();
        params.add(new BasicNameValuePair("apikey", APIKEY));
        params.add(new BasicNameValuePair("campaignName", "test2"));
        params.add(new BasicNameValuePair("isOutbound", "true"));
        params.add(new BasicNameValuePair("outboundCallerid", "4166653159"));
        params.add(new BasicNameValuePair("callfireXml", CAMPAIGN_XML));
        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        response = httpclient.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            System.out.println("Campaign creation failed");
            System.exit(0);
        } else {
            campaignid = convertStreamToString(response.getEntity().getContent()).replaceFirst("^.*<campaignid>", "").replaceFirst("</campaignid>.*$", "");
            System.out.println("Campaign created with id: " + campaignid);
        }

        // Make calls
        uri = URIUtils.createURI("https", WEBSERVER_HOST, WEBSERVER_PORT, WEBSERVER_CONTEXT + "campaign/" + campaignid + "/call", null, null);
        request = new HttpPost(uri);
        params.clear();
        params.add(new BasicNameValuePair("apikey", APIKEY));
        //params.add(new BasicNameValuePair("numbers", "14167224482,Michael,Lifschitz"));
        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        response = httpclient.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            System.out.println("Call sending failed");
        } else {
            System.out.println("Calls sent");
            System.out.println(convertStreamToString(response.getEntity().getContent()));
        }
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

    private static HttpClient createTrustingAllHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        
        // Creating of HttpClient that trust any certificate and does not verify host name
        SSLContext sslcontext = SSLContext.getInstance("SSL");

        sslcontext.init(null, new TrustManager[]{new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new SecureRandom());

        SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
        sf.setHostnameVerifier(new X509HostnameVerifier() {

            public boolean verify(String hostname, SSLSession session) {
                return true;
            }

            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }

            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            public void verify(String host, SSLSocket ssl) throws IOException {
            }
        });

        Scheme httpsScheme = new Scheme("https", sf, 443);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);

        HttpParams params = new BasicHttpParams();
        ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);

        return new DefaultHttpClient(cm, params);
    }
}
