/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Sep 12, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.servlet;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletTextOutput implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(ServletTextOutput.class);

    private boolean consumePipeExceptios = false;

    private boolean pipeBroken = false;

    private boolean defaultHtmlBody = false;

    private long lastOutputTime;

    private OutputStream out;

    public ServletTextOutput(HttpServletResponse response, boolean consumePipeExceptios) throws IOException {
        this(response.getOutputStream(), consumePipeExceptios);
        response.setDateHeader("Expires", System.currentTimeMillis());
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
        response.setContentType("text/html");
        html("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head><body>");
        defaultHtmlBody = true;
    }

    public ServletTextOutput(OutputStream out, boolean consumePipeExceptios) {
        this.out = out;
        this.consumePipeExceptios = consumePipeExceptios;
    }

    public void text(String... messages) throws IOException {
        if (this.pipeBroken) {
            return;
        }

        try {
            out.write("<pre>".getBytes());
            for (String message : messages) {
                out.write(message.getBytes());
            }

            out.write("</pre>".getBytes());
            out.flush();
            lastOutputTime = System.currentTimeMillis();
        } catch (IOException e) {
            pipeBroken = true;
            if (consumePipeExceptios) {
                log.error("Servlet Output error", e);
            } else {
                throw e;
            }
        }
    }

    public void html(String... messages) throws IOException {
        if (pipeBroken) {
            return;
        }
        try {
            for (String message : messages) {
                out.write(message.getBytes());
            }
            out.flush();
            lastOutputTime = System.currentTimeMillis();
        } catch (IOException e) {
            pipeBroken = true;
            if (consumePipeExceptios) {
                log.error("Servlet Output error", e);
            } else {
                throw e;
            }
        }
    }

    public boolean isPipeBroken() {
        return pipeBroken;
    }

    public long getLastOutputTime() {
        return lastOutputTime;
    }

    @Override
    public void close() throws IOException {
        if (this.out != null) {
            if (defaultHtmlBody && !isPipeBroken()) {
                html("</body></html>");
            }
            this.out.close();
            this.out = null;
        }
    }

    public void htmlScrollToEnd() throws IOException {
        html("<script>window.scrollTo(0,document.body.scrollHeight);</script>");
    }

}
