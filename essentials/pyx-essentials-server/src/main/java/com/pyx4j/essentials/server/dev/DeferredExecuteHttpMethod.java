/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-02-26
 * @author vlads
 */
package com.pyx4j.essentials.server.dev;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;

public class DeferredExecuteHttpMethod implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DeferredExecuteHttpMethod.class);

    private static Map<String, DeferredExecuteHttpMethod> requests = new HashMap<String, DeferredExecuteHttpMethod>();

    private static long correlationNumber = 1;

    private static synchronized long nextCorrelationNumber() {
        return correlationNumber++;
    }

    private final DevDumpProxyServlet handler;

    private final HttpClient client;

    private HttpMethodBase method;

    final long startTime = System.currentTimeMillis();

    private final String correlation;

    private final int id;

    private int status;

    private Throwable executeThrowable;

    private boolean compleated;

    private final Object notify;

    private int retryCount;

    public DeferredExecuteHttpMethod(HttpClient client, HttpMethodBase method, DevDumpProxyServlet handler, int id) {
        this.handler = handler;
        this.client = client;
        this.method = method;
        this.id = id;
        this.correlation = String.valueOf(nextCorrelationNumber());
        this.notify = new Object();
    }

    public void keep() {
        requests.put(this.correlation, this);
    }

    static DeferredExecuteHttpMethod getDeferredExecute(String correlation) {
        return requests.get(correlation);
    }

    public void start() {
        Thread t = new Thread(this, "DeferredExecuteHttpMethod" + correlation);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void run() {
        try {
            status = client.executeMethod(method);
            log.debug("proxy service " + method.getName() + " + response status and duration", status, TimeUtils.secSince(startTime));
        } catch (Throwable e) {
            executeThrowable = e;
        } finally {
            compleated = true;
            if (notify != null) {
                synchronized (notify) {
                    notify.notifyAll();
                }
            }
        }

    }

    public void copyResponce(HttpServletResponse response) throws IOException {
        try {
            if (executeThrowable != null) {
//                if (handler.wrapSoapException()) {
//                    DevDumpProxyServlet.replyWithSoapException(response, method.getURI().toString(), executeThrowable);
//                } else {
                response.sendError(HttpURLConnection.HTTP_INTERNAL_ERROR, method.getURI().toString() + " " + executeThrowable.getMessage());
//                }
                return;
            }
            response.setStatus(status);
            for (Header header : method.getResponseHeaders()) {
                String name = header.getName();
                if (name.equalsIgnoreCase("Transfer-Encoding")) {
                    log.debug("skip proxy service response header", name, header.getValue());
                    continue;
                }
                response.setHeader(name, header.getValue());
                log.debug("proxy service response header", name, header.getValue());
            }
            final OutputStream out = response.getOutputStream();
            try {
                handler.copyStream(method.getResponseBodyAsStream(), out, "response", id);
            } finally {
                IOUtils.closeQuietly(out);
            }
        } finally {
            method.releaseConnection();
            method = null;
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public String getCorrelation() {
        return correlation;
    }

    public boolean isCompleated() {
        return compleated;
    }

    public void waitNotCompleated(int seconds) {
        if (isCompleated()) {
            return;
        }
        synchronized (getNotify()) {
            try {
                getNotify().wait(seconds * 1000);
            } catch (InterruptedException e) {
                throw new Error(e.toString());
            }
        }
    }

    public void clenup() {
        if (method != null) {
            method.releaseConnection();
            method = null;
        }
        requests.remove(this.correlation);
    }

    public int getRetryCount() {
        return retryCount++;
    }

    public Object getNotify() {
        return notify;
    }

}
