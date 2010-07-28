/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2010-06-25
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.j2se;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.rpc.shared.Service;

public class J2SEService {

    private static final Logger log = LoggerFactory.getLogger(J2SEService.class);

    public static final String SERVER_SYSTEM_PROPERTY = "pyx-server-url";

    protected static final String POST = "POST";

    protected static final String GET = "GET";

    protected String serverUrl;

    protected String userAgent = "pyx-j2se/1.0";

    protected Map<String, String> cookies = new HashMap<String, String>();

    public J2SEService() {
        this(System.getProperty(SERVER_SYSTEM_PROPERTY));
    }

    public J2SEService(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void googleLogin(String userName, String password) throws RuntimeException {
        HttpURLConnection conn = null;
        try {
            URL u = new URL(serverUrl);
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod(GET);
            conn.setRequestProperty("User-agent", userAgent);
            conn.setInstanceFollowRedirects(false);
            conn.setUseCaches(false);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                String redirect = conn.getHeaderField("Location");
                log.debug("redirect {}", redirect);
                conn.disconnect();
                conn = (HttpURLConnection) new URL(redirect).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-agent", userAgent);
                conn.setUseCaches(false);
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new RuntimeException("Failed to authenticate " + conn.getResponseMessage());
                }
                getCookie(conn.getHeaderFields());
                conn.disconnect();
                gaeDevLoginPost(redirect, serverUrl, userName);
            } else {
                log.debug("{} no redirect ", conn.getResponseCode());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to login", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    protected void gaeDevLoginPost(String loginUrl, String url, String userName) {
        HttpURLConnection conn = null;
        OutputStream out = null;
        try {
            conn = (HttpURLConnection) new URL(loginUrl).openConnection();
            conn.setRequestMethod(POST);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-agent", userAgent);
            //conn.setRequestProperty("Host", "localhost:8888");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            //            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            //            conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            //            conn.setRequestProperty("Accept-Language", "en-us,en;q=0.7,ru;q=0.3");
            //            conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            //            conn.setRequestProperty("User-agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.8) Gecko/20100722 Firefox/3.6.8");
            //            conn.setRequestProperty("Referer", loginUrl);

            String charsetName = "iso-8859-1";
            StringBuffer form = new StringBuffer();

            form.append("email=");
            form.append(URLEncoder.encode(userName, charsetName));
            form.append("&");

            form.append("continue=");
            form.append(URLEncoder.encode(url, charsetName));
            form.append("&");

            form.append("action=");
            form.append(URLEncoder.encode("Log In", charsetName));

            //form.append("\r\n");

            System.out.println(form.toString());

            byte[] data = form.toString().getBytes(charsetName);
            conn.setRequestProperty("Content-Length", Integer.toString(data.length));
            printHeaders(conn.getRequestProperties());
            out = conn.getOutputStream();
            out.write(data);
            //out.write('\r');
            //out.write('\n');
            out.flush();
            out.close();
            out = null;

            log.debug("login post responce {}", conn.getResponseCode());
            printHeaders(conn.getHeaderFields());
            getCookie(conn.getHeaderFields());
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            int ch;
            while ((ch = dis.read()) != -1) {
                System.out.print((char) ch);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to login", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public <I extends Serializable, O extends Serializable> O execute(final Class<? extends Service<I, O>> serviceInterface, I request) throws RuntimeException {
        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            URL u = new URL(serverUrl + serviceInterface.getSimpleName().replace('$', '.'));
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            StringBuilder cookieBuilder = new StringBuilder();
            for (Map.Entry<String, String> me : cookies.entrySet()) {
                if (cookieBuilder.length() > 0) {
                    cookieBuilder.append(';');
                }
                cookieBuilder.append(me.getKey()).append('=').append(me.getValue());
            }
            if (cookieBuilder.length() > 0) {
                conn.setRequestProperty("Cookie", cookieBuilder.toString());
            }
            conn.setRequestProperty("Cache-Control", "no-cache, no-transform");
            conn.setRequestProperty("Content-Type", "application/binary");
            conn.setRequestProperty("User-agent", userAgent);

            out = conn.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeUTF(serviceInterface.getName());
            oos.writeObject(request);
            oos.flush();
            out.flush();
            out.close();
            out = null;

            Serializable reply = null;
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(in);
                reply = (Serializable) ois.readObject();
                ois.close();
            } else if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                reply = null;
            } else {
                String msg = conn.getHeaderField("message");
                if (msg != null) {
                    throw new RuntimeExceptionSerializable(msg);
                } else {
                    throw new RuntimeExceptionSerializable(conn.getResponseMessage());
                }
            }
            getCookie(conn.getHeaderFields());
            return (O) reply;
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute " + serviceInterface.getName(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to execute " + serviceInterface.getName(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    protected void getCookie(Map<String, List<String>> headers) {
        List<String> values = headers.get("Set-Cookie");
        if (values == null) {
            return;
        }
        for (String v : values) {
            int eq = v.indexOf('=');
            if (eq == -1) {
                continue;
            }
            int t = v.indexOf(';', eq);
            if (t != -1) {
                String name = v.substring(0, eq);
                String value = v.substring(eq + 1, t);
                cookies.put(name, value);
                log.debug("cookie {} = {}", name, value);
            }
        }
    }

    protected static void printHeaders(Map<String, List<String>> headers) {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, List<String>> me : headers.entrySet()) {
            b.append(me.getKey()).append(" = ").append(me.getValue()).append("\n");
        }
        log.debug("{}", b.toString());
    }
}
