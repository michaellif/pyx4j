/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Aug 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.server.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.essentials.server.deferred.DeferredProcessServicesImpl;
import com.pyx4j.essentials.server.deferred.IDeferredProcess;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

@SuppressWarnings("serial")
public abstract class AbstractUploadServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(AbstractUploadServlet.class);

    private final Map<String, Class<UploadReciver>> mappedUploads = new HashMap<String, Class<UploadReciver>>();

    /**
     * Used for development tests
     */
    protected int slowUploadSeconds = 0;

    @SuppressWarnings("unchecked")
    protected <T extends UploadReciver & UploadService> void bind(Class<T> serviceImpClass) {
        for (Class<?> itf : serviceImpClass.getInterfaces()) {
            if (UploadService.class.isAssignableFrom(itf)) {
                //TODO use "pyx.ServicePolicy", @see com.pyx4j.rpc.serve.RemoteServiceServlet
                String serviceClassId = itf.getName();
                mappedUploads.put(serviceClassId, (Class<UploadReciver>) serviceImpClass);
                return;
            }
        }
        throw new Error("Can't find inteface UploadService");
    }

    private static class UploadCanceled extends RuntimeException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.print(UploadService.ResponsePrefix);
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn())) {
            log.trace("no session");
            out.println("no session");
            return;
        }
        try {
            if (!ServletFileUpload.isMultipartContent(request)) {
                out.println("Invalid request type");
                return;
            }
            //TODO use "pyx.ServicePolicy", @see com.pyx4j.rpc.serve.RemoteServiceServlet
            String serviceClassId = request.getPathInfo().substring(1);
            Class<UploadReciver> reciverClass = mappedUploads.get(serviceClassId);
            if (reciverClass == null) {
                log.error("unmapped upload {}", serviceClassId);
                out.println("Invalid request");
                return;
            }
            UploadReciver reciver = reciverClass.newInstance();

            ServletFileUpload fileUpload = new ServletFileUpload();
            long maxSize = reciver.getMaxSize(request);
            fileUpload.setFileSizeMax(maxSize);

            ProgressListenerImpl progressListener = new ProgressListenerImpl();
            fileUpload.setProgressListener(progressListener);

            FileItemStream fileItem = null;
            UploadData data = new UploadData();
            try {
                FileItemIterator iterator = fileUpload.getItemIterator(request);
                while (iterator.hasNext()) {
                    FileItemStream item = iterator.next();
                    if (item.isFormField()) {
                        log.debug(" form field {}", item.getFieldName());
                        if (UploadService.PostCorrelationID.equals(item.getFieldName())) {
                            data.deferredCorrelationId = Streams.asString(item.openStream());
                            IDeferredProcess process = DeferredProcessServicesImpl.getDeferredProcess(data.deferredCorrelationId);
                            if (process != null) {
                                progressListener.processInfo = process.status();
                            }
                        } else if (UploadService.PostUploadKey.equals(item.getFieldName())) {
                            data.uploadKey = new Key(Streams.asString(item.openStream()));
                        } else if (UploadService.PostUploadDescription.equals(item.getFieldName())) {
                            data.description = Streams.asString(item.openStream());
                        }
                    } else if (fileItem == null) {
                        fileItem = item;
                        InputStream in = fileItem.openStream();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        try {
                            IOUtils.copyStream(in, os, 1024);
                            data.data = os.toByteArray();
                            if (data.data.length >= maxSize) {
                                out.println("Upload size exceed " + (maxSize / (1024 * 1024)) + " megabyte");
                                return;
                            }
                        } catch (Throwable e) {
                            log.error("Upload error", e);
                            out.println("Failed to receive upload " + CommonsStringUtils.nvl(e.getMessage()));
                            return;
                        } finally {
                            IOUtils.closeQuietly(in);
                            IOUtils.closeQuietly(os);
                        }
                    }
                }
            } catch (FileUploadException e) {
                log.error("File Upload error", e);
                out.println("File Upload error " + CommonsStringUtils.nvl(e.getMessage()));
                return;
            }
            if ((fileItem == null) || (data == null)) {
                out.println("File not uploaded");
                return;
            }

            data.fileName = fileItem.getName();
            if (data.fileName != null) {
                data.fileName = FilenameUtils.getName(data.fileName);
            }

            log.debug("Got file {}", fileItem.getName());

            Key id = reciver.onUploadRecived(data);
            if (id != null) {
                //TODO serialize id
                out.println("OK " + id);
            } else {
                out.println("OK ");
            }
        } catch (Throwable t) {
            log.error("upload error", t);
            if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                out.println("Error " + t.getMessage());
            } else {
                out.println("upload error");
            }
        } finally {
            out.flush();
            out.close();
        }
    }

    private class ProgressListenerImpl implements ProgressListener {

        DeferredProcessProgressResponse processInfo = null;

        private long nextDellay = 0;

        @Override
        public void update(long bytesRead, long contentLength, int items) {
            if (processInfo != null) {
                processInfo.setProgress((int) (bytesRead / 1024));
                processInfo.setProgressMaximum((int) (contentLength / 1024));
                if (processInfo.isCanceled()) {
                    throw new UploadCanceled();
                }
            }
            if (slowUploadSeconds != 0) {
                if (nextDellay < bytesRead) {
                    nextDellay += contentLength / 100;
                    try {
                        Thread.sleep((slowUploadSeconds * Consts.SEC2MILLISECONDS) / 100);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }

    }
}
