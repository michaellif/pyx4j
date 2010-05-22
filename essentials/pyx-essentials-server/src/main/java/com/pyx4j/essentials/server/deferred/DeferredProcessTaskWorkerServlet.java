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
 * Created on May 22, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.deferred;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskHandle;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;

import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.gwt.server.IOUtils;

@SuppressWarnings("serial")
public class DeferredProcessTaskWorkerServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DeferredProcessTaskWorkerServlet.class);

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (request.getContentLength() == 0) {
            log.error("request content is empty");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        InputStream in = request.getInputStream();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            IOUtils.copyStream(in, buf, 1024);
        } finally {
            IOUtils.closeQuietly(in);
        }

        Object payload;
        try {
            payload = deserialize(buf.toByteArray());
        } catch (ClassNotFoundException e) {
            log.error("payload not found", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        if (payload instanceof IDeferredProcess) {
            IDeferredProcess process = (IDeferredProcess) payload;
            process.execute();
            DeferredProcessProgressResponse r = process.status();
            if (r.isCompleted()) {
                log.info("Process compleated; {}", r.getMessage());
            } else {
                defer(process);
            }
        } else {
            log.error("unsupported payload class {}", payload.getClass().getName());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    public static void defer(IDeferredProcess process) throws IOException {
        byte[] payload = serialize(process);

        Queue queue = QueueFactory.getQueue("internal");
        TaskHandle handle = queue.add(TaskOptions.Builder.url("/internal/worker").method(Method.POST).payload(payload,
                Downloadable.getContentType(DownloadFormat.JAVA_SERIALIZED)));

        log.debug("task {} deferred; eta {}", process.getClass().getName(), System.currentTimeMillis() - handle.getEtaMillis());
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(buf);
        try {
            out.writeObject(obj);
            return buf.toByteArray();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(b);
            return in.readObject();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(b);
        }
    }

}
