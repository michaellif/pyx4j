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
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.essentials.rpc.upload.UploadResponse;
import com.pyx4j.essentials.rpc.upload.UploadService;
import com.pyx4j.essentials.server.deferred.DeferredProcessRegistry;
import com.pyx4j.essentials.server.upload.UploadReciver.ProcessingStatus;
import com.pyx4j.gwt.server.IOUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.server.ServicePolicy;
import com.pyx4j.rpc.shared.IServiceExecutePermission;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

@SuppressWarnings("serial")
public abstract class AbstractUploadServlet extends HttpServlet {

    private static final I18n i18n = I18n.get(AbstractUploadServlet.class);

    private final static Logger log = LoggerFactory.getLogger(AbstractUploadServlet.class);

    private final Map<Class<UploadService<?, ?>>, Class<? extends UploadReciver<?, ?>>> mappedUploads = new HashMap<Class<UploadService<?, ?>>, Class<? extends UploadReciver<?, ?>>>();

    /**
     * Used for development tests
     */
    protected int slowUploadSeconds = 0;

    @SuppressWarnings("unchecked")
    protected <T extends UploadReciver<?, ?> & UploadService<?, ?>> void bind(Class<T> serviceImpClass) {
        for (Class<?> itf : serviceImpClass.getInterfaces()) {
            if (UploadService.class.isAssignableFrom(itf)) {
                mappedUploads.put((Class<UploadService<?, ?>>) itf, (Class<? extends UploadReciver<?, ?>>) serviceImpClass);
                return;
            }
        }
        throw new Error("Can't find interface UploadService");
    }

    private static class UploadCanceled extends RuntimeException {

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.print(UploadService.ResponsePrefix);
        Visit v = Context.getVisit();
        if ((v == null) || (!v.isUserLoggedIn())) {
            log.trace("no session");
            out.println(i18n.tr("No Session"));
            return;
        }
        @SuppressWarnings("rawtypes")
        UploadDeferredProcess process = null;
        try {
            if (!ServletFileUpload.isMultipartContent(request)) {
                out.println(i18n.tr("Invalid Request Type"));
                return;
            }
            String uploadPath = request.getPathInfo();
            int cIdEnd = uploadPath.lastIndexOf('/');
            String moduleBaseURL = uploadPath.substring(0, cIdEnd + 1);
            String serviceInterfaceId = uploadPath.substring(cIdEnd + 1);
            ServicePolicy.loadServicePolicyToRequest(this.getServletContext(), moduleBaseURL);
            String serviceClassName = ServicePolicy.decodeServiceInterfaceClassName(serviceInterfaceId);
            Class<UploadService<?, ?>> serviceClass = (Class<UploadService<?, ?>>) Class.forName(serviceClassName);
            SecurityController.assertPermission(new IServiceExecutePermission(serviceClass));
            Class<? extends UploadReciver<?, ?>> reciverClass = mappedUploads.get(serviceClass);
            if (reciverClass == null) {
                log.error("unmapped upload {}", serviceClassName);
                out.println("Invalid request");
                return;
            }
            UploadReciver<?, ?> reciver = reciverClass.newInstance();

            ServletFileUpload fileUpload = new ServletFileUpload();
            long maxSize = reciver.getMaxSize();
            fileUpload.setFileSizeMax(maxSize);

            ProgressListenerImpl progressListener = new ProgressListenerImpl();
            fileUpload.setProgressListener(progressListener);

            UploadData uploadData = new UploadData();
            @SuppressWarnings("rawtypes")
            UploadResponse uploadResponse = new UploadResponse();
            try {
                FileItemIterator iterator = fileUpload.getItemIterator(request);
                while (iterator.hasNext()) {
                    FileItemStream item = iterator.next();
                    if (item.isFormField()) {
                        log.debug(" form field {}", item.getFieldName());
                        if (UploadService.PostCorrelationID.equals(item.getFieldName())) {
                            uploadData.deferredCorrelationId = Streams.asString(item.openStream());
                            process = (UploadDeferredProcess<?, ?>) DeferredProcessRegistry.get(uploadData.deferredCorrelationId);
                            if (process != null) {
                                progressListener.processInfo = process.status();
                                process.setResponse(uploadResponse);
                            }
                            log.debug("form field {}={}", item.getFieldName(), uploadData.deferredCorrelationId);
                        } else if (UploadService.PostUploadKey.equals(item.getFieldName())) {
                            uploadData.uploadKey = new Key(Streams.asString(item.openStream()));
                            log.debug("form field {}={}", item.getFieldName(), uploadData.uploadKey);
                        } else if (UploadService.PostUploadDescription.equals(item.getFieldName())) {
                            uploadData.description = Streams.asString(item.openStream());
                            log.debug("form field {}={}", item.getFieldName(), uploadData.description);
                        } else {
                            log.debug("unknown form field {}", item.getFieldName());
                        }
                    } else if (uploadData.data == null) {
                        uploadResponse.fileName = item.getName();
                        if (uploadResponse.fileName != null) {
                            uploadResponse.fileName = FilenameUtils.getName(uploadResponse.fileName);
                        }
                        reciver.onUploadStart(uploadResponse.fileName);
                        InputStream in = item.openStream();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        try {
                            IOUtils.copyStream(in, os, 1024);
                            uploadData.data = os.toByteArray();
                            if (uploadData.data.length >= maxSize) {
                                out.println(i18n.tr("File upload size exceeds maximum of {0} megabytes", (maxSize / (1024 * 1024))));
                                return;
                            }
                        } finally {
                            IOUtils.closeQuietly(in);
                            IOUtils.closeQuietly(os);
                        }
                    }
                }
            } catch (UploadCanceled e) {
                out.println(i18n.tr("File upload cancelled"));
                return;
            } catch (UserRuntimeException e) {
                if (process != null) {
                    process.status().setErrorStatusMessage(e.getMessage());
                }
                out.println(e.getMessage());
                return;
            } catch (Throwable e) {
                log.error("File Upload error", e);
                if (e.getCause() instanceof FileUploadBase.FileSizeLimitExceededException) {
                    out.println(i18n.tr("File upload size exceeds maximum of {0} megabytes", (maxSize / (1024 * 1024))));
                } else if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                    out.println("Error " + e.getMessage());
                } else {
                    out.println(i18n.tr("File upload error"));
                }
                if (process != null) {
                    process.status().setError();
                }
                return;
            }
            if (uploadData.data == null) {
                out.println(i18n.tr("File not uploaded"));
                return;
            }
            uploadResponse.fileSize = uploadData.data.length;
            uploadResponse.timestamp = System.currentTimeMillis();
            log.debug("Got uploaded file {}", uploadResponse.fileName);
            ProcessingStatus status = reciver.onUploadRecived(uploadData, process, uploadResponse);
            switch (status) {
            case completed:
                if (process != null) {
                    process.status().setCompleted();
                }
                log.debug("Upload processing completed");
                out.println(UploadService.ResponseOk);
                break;
            case processWillContinue:
                log.debug("Upload processing will continue");
                out.println(UploadService.ResponseProcessWillContinue);
                break;
            }
        } catch (UserRuntimeException e) {
            log.error("upload error", e);
            if (process != null) {
                process.status().setErrorStatusMessage(e.getMessage());
            }
            out.println(e.getMessage());
            return;
        } catch (Throwable t) {
            log.error("upload error", t);
            if (ServerSideConfiguration.instance().isDevelopmentBehavior()) {
                out.println("Error " + t.getMessage());
            } else {
                out.println(i18n.tr("File upload error"));
            }
            if (process != null) {
                process.status().setError();
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
