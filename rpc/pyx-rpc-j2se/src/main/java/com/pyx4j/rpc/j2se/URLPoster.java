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
 * Created on 2010-07-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.j2se;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLPoster {

    public static final String POST = "POST";

    public static final String GET = "GET";

    protected String charsetName = "iso-8859-1";

    protected HttpURLConnection conn = null;

    Map<String, String> params = new HashMap<String, String>();

    public URLPoster(String url) throws IOException {
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(POST);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        //conn.setRequestProperty("Host", "localhost:8888");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
    }

    public void setUserAgent(String userAgent) {
        conn.setRequestProperty("User-agent", userAgent);
    }

    public void param(String name, String value) {
        params.put(name, value);
    }

    public int post() throws IOException {
        StringBuffer form = new StringBuffer();
        for (Map.Entry<String, String> me : params.entrySet()) {
            if (form.length() > 0) {
                form.append('&');
            }
            form.append(me.getKey()).append('=').append(URLEncoder.encode(me.getValue(), charsetName));
        }
        byte[] data = form.toString().getBytes(charsetName);
        conn.setRequestProperty("Content-Length", Integer.toString(data.length));
        OutputStream out = conn.getOutputStream();
        out.write(data);
        out.flush();
        out.close();
        out = null;

        return conn.getResponseCode();
    }

    public int getResponseCode() throws IOException {
        return conn.getResponseCode();
    }

    public String getResponseMessage() throws IOException {
        return conn.getResponseMessage();
    }

    public InputStream getInputStream() throws IOException {
        return conn.getInputStream();
    }

    public String getInputStreamAsString() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()), 512);
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } finally {
            reader.close();
        }
    }

    public Map<String, List<String>> getHeaderFields() {
        return conn.getHeaderFields();
    }

    public void disconnect() {
        conn.disconnect();
    }
}
