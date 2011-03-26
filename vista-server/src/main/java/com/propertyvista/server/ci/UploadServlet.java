/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author sergei
 * @version $Id:
 */

package com.propertyvista.server.ci;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Sergei
 */
public class UploadServlet extends UploadAction {

    private static final long serialVersionUID = 1L;

    private final Map<String, String> receivedContentTypes = new HashMap<String, String>();

    /**
     * Maintain a list with received files and their content types.
     */
    private final Map<String, File> receivedFiles = new HashMap<String, File>();

    private final static Logger log = LoggerFactory.getLogger(UploadServlet.class);

    /**
     * Override executeAction to save the received files in a custom place
     * and delete this items from session.
     */
    @Override
    public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
        log.debug("UploadServlet.executeAction(): request=" + request + ", sessionFiles=" + sessionFiles);
        String response = "";
        int cont = 0;
        for (FileItem item : sessionFiles) {
            log.debug("UploadServlet.executeAction(): item=" + item);
            if (false == item.isFormField()) {
                cont++;
                try {
                    /// Create a new file based on the remote file name in the client
                    // String saveName = item.getName().replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+", "_");
                    // File file =new File("/tmp/" + saveName);

                    /// Create a temporary file placed in /tmp (only works in unix)
                    // File file = File.createTempFile("upload-", ".bin", new File("/tmp"));

                    /// Create a temporary file placed in the default system temp folder
                    File file = File.createTempFile("upload-", ".bin");
                    log.debug("UploadServlet.executeAction(): file=" + file.getAbsolutePath());
                    item.write(file);

                    /// Save a list with the received files
                    receivedFiles.put(item.getFieldName(), file);
                    String contentType = item.getContentType();
                    if (contentType != null && contentType.indexOf("image") != -1) {
                        Dimension d = getImageDimension(file);
                        log.debug("UploadServlet.getUploadedFile(): imgDim=" + d);
                    }
                    receivedContentTypes.put(item.getFieldName(), contentType);
                    log.debug("UploadServlet.executeAction(): receivedContentTypes=" + receivedContentTypes);

                    /// Compose a xml message with the full file information
                    response += "<file-" + cont + "-field>" + item.getFieldName() + "</file-" + cont + "-field>\n";
                    response += "<file-" + cont + "-name>" + item.getName() + "</file-" + cont + "-name>\n";
                    response += "<file-" + cont + "-size>" + item.getSize() + "</file-" + cont + "-size>\n";
                    response += "<file-" + cont + "-type>" + item.getContentType() + "</file-" + cont + "type>\n";
                } catch (Exception e) {
                    throw new UploadActionException(e);
                }
            }
        }

        /// Remove files from session because we have a copy of them
        removeSessionFileItems(request);

        /// Send information of the received files to the client.
        return "<response>\n" + response + "</response>\n";
    }

    /**
     * Get the content of an uploaded file.
     */
    @Override
    public void getUploadedFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fieldName = request.getParameter(PARAM_SHOW);
        log.debug("UploadServlet.getUploadedFile(): fileName=" + fieldName);
        File f = receivedFiles.get(fieldName);
        if (f != null) {
            String contentType = receivedContentTypes.get(fieldName);
            log.debug("UploadServlet.getUploadedFile(): file=" + f.getAbsolutePath() + ", contentType=" + contentType);
            response.setContentType(contentType);
            FileInputStream is = new FileInputStream(f);
            try {
                IOUtils.copy(is, response.getOutputStream());
            } finally {
                IOUtils.closeQuietly(is);
            }
        } else {
            renderXmlResponse(request, response, ERROR_ITEM_NOT_FOUND);
        }
    }

    private static Dimension getImageDimension(File f) throws IOException {
        int w = 0, h = 0;
        ImageInputStream iis = ImageIO.createImageInputStream(f);
        try {
            Iterator<ImageReader> ireader = ImageIO.getImageReaders(iis);
            ImageReader reader = ireader.next();
            try {
                reader.setInput(iis);
                w = reader.getWidth(0);
                h = reader.getHeight(0);
                return new Dimension(w, h);
            } finally {
                try {
                    reader.dispose();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        } finally {
            try {
                iis.close();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Remove a file when the user sends a delete request.
     */
    @Override
    public void removeItem(HttpServletRequest request, String fieldName) throws UploadActionException {
        log.debug("UploadServlet.removeItem(): fieldName=" + fieldName);
        File file = receivedFiles.get(fieldName);
        receivedFiles.remove(fieldName);
        receivedContentTypes.remove(fieldName);
        if (file != null) {
            file.delete();
        }
    }
}
