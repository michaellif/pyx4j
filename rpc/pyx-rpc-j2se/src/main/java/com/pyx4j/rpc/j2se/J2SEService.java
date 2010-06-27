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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.rpc.shared.Service;

public class J2SEService {

    public static final String SERVER_SYSTEM_PROPERTY = "pyx-server-url";

    private static final String SESSION_HEADER = "JSESSIONID";

    private static String sessionID;

    public static <I extends Serializable, O extends Serializable> O execute(final Class<? extends Service<I, O>> serviceInterface, I request)
            throws RuntimeException {

        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        try {

            String url = System.getProperty(SERVER_SYSTEM_PROPERTY);
            URL u = new URL(url + serviceInterface.getSimpleName().replace('$', '.'));
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            if (sessionID != null) {
                conn.setRequestProperty("Cookie", SESSION_HEADER + "=" + sessionID);
            }
            conn.setRequestProperty("Cache-Control", "no-cache, no-transform");
            conn.setRequestProperty("Content-Type", "application/binary");
            conn.setRequestProperty("User-agent", "pyx-j2se/1.0");

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
            getSessionCookie(conn.getHeaderFields());
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
                (conn).disconnect();
            }
        }
    }

    private static void getSessionCookie(Map<String, List<String>> headers) {
        List<String> values = headers.get("Set-Cookie");
        if (values == null) {
            return;
        }
        for (String v : values) {
            int i = v.indexOf(SESSION_HEADER);
            if (i == -1) {
                continue;
            }
            int eq = v.indexOf('=', i);
            if (eq == -1) {
                continue;
            }
            int t = v.indexOf(';', eq);
            if (t != -1) {
                sessionID = v.substring(eq + 1, t);
            }
        }
    }
}
