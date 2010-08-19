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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.rpc.shared.Service;
import com.pyx4j.security.server.ThrottleConfig;

public class J2SEService {

    private static final Logger log = LoggerFactory.getLogger(J2SEService.class);

    public static final String SERVER_SYSTEM_PROPERTY = "pyx-server-url";

    private static final String URL_GOOGLE_LOGIN = "https://www.google.com/accounts/ClientLogin";

    protected static final String POST = "POST";

    protected static final String GET = "GET";

    protected String serverUrl;

    protected String forceDeveloperLoginPath;

    protected String userAgent = "pyx-j2se/1.0";

    protected Map<String, String> cookies = new HashMap<String, String>();

    protected String applicationId;

    protected String postCharsetName = "iso-8859-1";

    private long serviceCallsCount = 0;

    private ThrottleConfig throttleConfig;

    protected String sessionToken;

    public enum GoogleAccountType {

        GOOGLE,

        HOSTED,

        HOSTED_OR_GOOGLE
    }

    public J2SEService() {
        this(System.getProperty(SERVER_SYSTEM_PROPERTY));
    }

    public J2SEService(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getForceDeveloperLoginPath() {
        return forceDeveloperLoginPath;
    }

    public void setForceDeveloperLoginPath(String forceDeveloperLoginPath) {
        this.forceDeveloperLoginPath = forceDeveloperLoginPath;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public long getServiceCallsCount() {
        return serviceCallsCount;
    }

    public ThrottleConfig getThrottleConfig() {
        return throttleConfig;
    }

    public void setThrottleConfig(ThrottleConfig throttleConfig) {
        this.throttleConfig = throttleConfig;
    }

    /**
     * Short string identifying your application, for logging purposes. This string should
     * take the form: "companyName-applicationName-versionID".
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void googleLogin(String userName, String password) throws RuntimeException {
        googleLogin(userName, password, GoogleAccountType.HOSTED_OR_GOOGLE, false);
    }

    public void googleLogin(String userName, String password, GoogleAccountType googleAccountType, boolean developerAdmin) throws RuntimeException {
        HttpURLConnection conn = null;
        try {
            URL u = new URL(serverUrl);
            if (developerAdmin && getForceDeveloperLoginPath() != null) {
                u = new URL(u.getProtocol(), u.getHost(), u.getPort(), getForceDeveloperLoginPath());
                log.debug("login to {} as {}", u, userName);
            }
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod(GET);
            conn.setRequestProperty("User-agent", userAgent);
            conn.setInstanceFollowRedirects(false);
            conn.setUseCaches(false);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                printHeaders(conn.getHeaderFields());
                String redirect = conn.getHeaderField("Location");
                log.debug("redirect {}", redirect);
                conn.disconnect();
                conn = (HttpURLConnection) new URL(redirect).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-agent", userAgent);
                conn.setUseCaches(false);
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new RuntimeException("Failed to authenticate " + conn.getResponseCode() + ":" + conn.getResponseMessage());
                }
                conn.disconnect();
                if (redirect.contains("http://localhost")) {
                    gaeDevLoginPost(redirect, serverUrl, userName, developerAdmin);
                } else {
                    googleLoginPost(u.getHost(), userName, password, googleAccountType);
                }
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

    protected void gaeDevLoginPost(String loginUrl, String url, String userName, boolean developerAdmin) {
        URLPoster post = null;
        try {
            post = new URLPoster(loginUrl);
            post.setUserAgent(userAgent);

            post.param("email", userName);
            post.param("continue", url);
            if (developerAdmin) {
                post.param("isAdmin", "on");
            }
            post.param("action", "Log In");

            post.post();
            log.debug("login post responce {}", post.getResponseCode());

            getCookie(post.getHeaderFields());
        } catch (IOException e) {
            throw new RuntimeException("Failed to login", e);
        } finally {
            if (post != null) {
                post.disconnect();
            }
        }
    }

    /**
     * See http://code.google.com/apis/accounts/docs/AuthForInstalledApps.html
     * http://krasserm.blogspot.com/2010/01/accessing-security-enabled-google-app.html
     */
    protected void googleLoginPost(String appHost, String userName, String password, GoogleAccountType googleAccountType) {
        URLPoster post = null;
        String authToken;
        try {
            post = new URLPoster(URL_GOOGLE_LOGIN);
            post.setUserAgent(userAgent);
            post.param("accountType", googleAccountType.name());
            post.param("Email", userName);
            post.param("Passwd", password);
            post.param("service", "ah");
            if (getApplicationId() != null) {
                post.param("source", getApplicationId());
            }

            post.post();
            log.debug("login post responce {}", post.getResponseCode());
            if (post.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed to authenticate at Google " + post.getResponseCode() + ":" + post.getResponseMessage());
            }
            String body = post.getInputStreamAsString();
            log.debug("login response[{}]", body);
            String token = "Auth=";
            int p = body.indexOf(token);
            if (p == -1) {
                throw new RuntimeException("Failed to authenticate at Google " + body);
            }
            authToken = body.substring(p + token.length(), body.indexOf('\n', p));
        } catch (IOException e) {
            throw new RuntimeException("Google login failed ", e);
        } finally {
            if (post != null) {
                post.disconnect();
            }
        }

        // Login to GAE
        HttpURLConnection conn = null;
        try {
            URL u = new URL("http://" + appHost + "/_ah/login?auth=" + authToken);
            log.debug("GAE app login [{}]", u);
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod(GET);
            conn.setRequestProperty("User-agent", userAgent);
            conn.setInstanceFollowRedirects(false);
            int code = conn.getResponseCode();
            if ((code == HttpURLConnection.HTTP_OK) || (code == HttpURLConnection.HTTP_MOVED_TEMP)) {
                getCookie(conn.getHeaderFields());
            } else {
                throw new RuntimeException("Failed to authenticate in GAE " + code + ":" + conn.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException("GAE login failed", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void logout() {

    }

    private long throttleNextIntervalResetTime = 0;

    private int throttleRequestsCount = 0;

    protected void throttleObedience() {
        throttleRequestsCount++;
        long now = System.currentTimeMillis();
        if (now >= throttleNextIntervalResetTime) {
            throttleRequestsCount = 1;
            throttleNextIntervalResetTime = now + throttleConfig.getInterval() + (30 * Consts.SEC2MSEC);
        }
        if (throttleRequestsCount >= throttleConfig.getMaxRequests() - 5) {
            try {
                long sleep = throttleNextIntervalResetTime - now;
                log.debug("sleep for {} sec", sleep / 1000);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
    }

    protected String serviceInterfaceMarker(final Class<? extends Service> serviceInterface) {
        String simpleName = serviceInterface.getName();
        return simpleName.substring(simpleName.lastIndexOf(".") + 1).replace('$', '.');
    }

    public <I extends Serializable, O extends Serializable> O execute(final Class<? extends Service<I, O>> serviceInterface, I request) throws RuntimeException {
        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            serviceCallsCount++;
            if (throttleConfig != null) {
                throttleObedience();
            }
            URL u = new URL(serverUrl + serviceInterfaceMarker(serviceInterface));
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            setCookie(conn);
            if (sessionToken != null) {
                conn.setRequestProperty(RemoteService.SESSION_TOKEN_HEADER, sessionToken);
            }
            conn.setRequestProperty("Cache-Control", "no-cache, no-transform");
            conn.setRequestProperty("Content-Type", "application/binary");
            conn.setRequestProperty("User-agent", userAgent);

            //printHeaders(conn.getRequestProperties());

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
                    throw new RuntimeExceptionSerializable(conn.getResponseCode() + ":" + conn.getResponseMessage());
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
                String pValue = cookies.put(name, value);
                if (!CommonsStringUtils.equals(pValue, value)) {
                    log.debug("cookie {} = {}", name, value);
                }
            }
        }
    }

    public void setCookie(String cookieName, String value) {
        cookies.put(cookieName, value);
    }

    public String getCookie(String cookieName) {
        return cookies.get(cookieName);
    }

    public void removeCookie(String cookieName) {
        if (cookieName != null) {
            cookies.remove(cookieName);
        }
    }

    protected void setCookie(HttpURLConnection conn) {
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
    }

    protected static void printHeaders(Map<String, List<String>> headers) {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, List<String>> me : headers.entrySet()) {
            b.append(me.getKey()).append(" = ").append(me.getValue()).append("\n");
        }
        log.debug("{}", b.toString());
    }

}
