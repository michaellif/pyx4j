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

package com.propertyvista.portal.server.upload;

import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.server.pt.PtAppContext;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Sergei
 */
public class UploadServlet extends UploadAction {

    private static final long serialVersionUID = 1L;

    //private final Map<String, String> receivedContentTypes = new HashMap<String, String>();

    /**
     * Maintain a list with received files and their content types.
     */
    //private final Map<String, File> receivedFiles = new HashMap<String, File>();

    private final static Logger log = LoggerFactory.getLogger(UploadServlet.class);

    /**
     * Override executeAction to save the received files in a custom place
     * and delete this items from session.
     */
    @Override
    public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
        log.debug("UploadServlet.executeAction(): request=" + request + ", sessionFiles=" + sessionFiles);
        StringBuilder response = new StringBuilder();
        //int cont = 0;
        FileItem fileItem = null;
        String tenantId = null;
        String documentType = null;

        for (FileItem item : sessionFiles) {
            log.debug("UploadServlet.executeAction(): item=" + item);
            if (item.isFormField()) {
                if ("tenantId".equalsIgnoreCase(item.getFieldName())) {
                    tenantId = item.getString();
                    //log.debug("tenantId=" + tenantId);
                } else if ("documentType".equalsIgnoreCase(item.getFieldName())) {
                    documentType = item.getString();
                    //log.debug("tenantId=" + tenantId);
                }
            } else {
                fileItem = item;
                //cont++;
                try {
                    /// Create a new file based on the remote file name in the client
                    // String saveName = item.getName().replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+", "_");
                    // File file =new File("/tmp/" + saveName);

                    /// Create a temporary file placed in /tmp (only works in unix)
                    // File file = File.createTempFile("upload-", ".bin", new File("/tmp"));

                    /// Create a temporary file placed in the default system temp folder
                    //File file = File.createTempFile("upload-", ".bin");
                    //log.debug("UploadServlet.executeAction(): file=" + file.getAbsolutePath());
                    //item.write(file);

                    /// Save a list with the received files
                    //receivedFiles.put(item.getFieldName(), file);
                    //String contentType = item.getContentType();
                    //if (contentType != null && contentType.indexOf("image") != -1) {
                    //    Dimension d = getImageDimension(file);
                    //    log.debug("UploadServlet.getUploadedFile(): imgDim=" + d);
                    //}
                    //receivedContentTypes.put(item.getFieldName(), contentType);
                    //log.debug("UploadServlet.executeAction(): receivedContentTypes=" + receivedContentTypes);

                    //InputStream in = new FileInputStream(file);
                    //try {
                    //byte[] data = item.get();//IOUtils.toByteArray(in);
                    //createApplicationDocument(null, item.getName(), data, ApplicationDocument.DocumentType.securityInfo);
                    //} finally {
                    //    in.close();
                    //}

                } catch (Exception e) {
                    throw new UploadActionException(e);
                }
            }
        }

        log.debug("fileItem=" + fileItem);
        log.debug("tenantId=" + tenantId);
        log.debug("documentType=" + documentType);

        if (fileItem != null && tenantId != null) {
            byte[] data = fileItem.get();//IOUtils.toByteArray(in);
            ApplicationDocument applicationDocument = createApplicationDocument(new Long(tenantId), fileItem.getName(), data,
                    "securityInfo".equalsIgnoreCase(documentType) ? ApplicationDocument.DocumentType.securityInfo : ApplicationDocument.DocumentType.income);
            if ("income".equalsIgnoreCase(documentType)) {
                TenantIncome income = PersistenceServicesFactory.getPersistenceService().retrieve(TenantIncome.class, new Long(tenantId));
                income.documents().add(applicationDocument);
                PersistenceServicesFactory.getPersistenceService().merge(income);
            }
            /// Compose a xml message with the full file information
            response.append("<file-field>").append(fileItem.getFieldName()).append("</file-field>\n");
            response.append("<file-name>").append(fileItem.getName()).append("</file-name>\n");
            response.append("<file-size>").append(fileItem.getSize()).append("</file-size>\n");
            response.append("<file-type>").append(fileItem.getContentType()).append("</file-type>\n");
        } else {
            log.error("ERROR: fileItem, tenantId or documentType is missing: " + fileItem + ", tenantId=" + tenantId + ", documentType=" + documentType);
        }

        /// Remove files from session because we have a copy of them
        removeSessionFileItems(request);

        response.insert(0, "<response>\n");
        response.append("</response>\n");

        /// Send information of the received files to the client.
        return response.toString();
    }

    private ApplicationDocument createApplicationDocument(Long tenantId, String fileName, byte[] data, ApplicationDocument.DocumentType documentType) {
        ApplicationDocument applicationDocument = EntityFactory.create(ApplicationDocument.class);
        applicationDocument.application().set(PtAppContext.getCurrentUserApplication());
        applicationDocument.tenant().setPrimaryKey(tenantId);
        applicationDocument.type().setValue(documentType);
        applicationDocument.filename().setValue(fileName);
        try {
            applicationDocument.fileSize().setValue((long) data.length);
            applicationDocument.data().setValue(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        PersistenceServicesFactory.getPersistenceService().persist(applicationDocument);
        return applicationDocument;
    }

    /**
     * Get the content of an uploaded file.
     */
    /*
     * @Override
     * public void getUploadedFile(HttpServletRequest request, HttpServletResponse
     * response) throws IOException {
     * String fieldName = request.getParameter(PARAM_SHOW);
     * log.debug("UploadServlet.getUploadedFile(): fileName=" + fieldName);
     * File f = receivedFiles.get(fieldName);
     * if (f != null) {
     * String contentType = receivedContentTypes.get(fieldName);
     * log.debug("UploadServlet.getUploadedFile(): file=" + f.getAbsolutePath() +
     * ", contentType=" + contentType);
     * response.setContentType(contentType);
     * FileInputStream is = new FileInputStream(f);
     * try {
     * IOUtils.copy(is, response.getOutputStream());
     * } finally {
     * IOUtils.closeQuietly(is);
     * }
     * } else {
     * renderXmlResponse(request, response, ERROR_ITEM_NOT_FOUND);
     * }
     * }
     * /*
     */

    /*
     * private static Dimension getImageDimension(File f) throws IOException {
     * int w = 0, h = 0;
     * ImageInputStream iis = ImageIO.createImageInputStream(f);
     * try {
     * Iterator<ImageReader> ireader = ImageIO.getImageReaders(iis);
     * ImageReader reader = ireader.next();
     * try {
     * reader.setInput(iis);
     * w = reader.getWidth(0);
     * h = reader.getHeight(0);
     * return new Dimension(w, h);
     * } finally {
     * try {
     * reader.dispose();
     * } catch (Exception e) {
     * log.error(e.getMessage());
     * }
     * }
     * } finally {
     * try {
     * iis.close();
     * } catch (Exception e) {
     * log.error(e.getMessage());
     * }
     * }
     * }
     */

    /**
     * Remove a file when the user sends a delete request.
     */
    /*
     * @Override
     * public void removeItem(HttpServletRequest request, String fieldName) throws
     * UploadActionException {
     * log.debug("UploadServlet.removeItem(): fieldName=" + fieldName);
     * File file = receivedFiles.get(fieldName);
     * receivedFiles.remove(fieldName);
     * receivedContentTypes.remove(fieldName);
     * if (file != null) {
     * file.delete();
     * }
     * }
     */
}
