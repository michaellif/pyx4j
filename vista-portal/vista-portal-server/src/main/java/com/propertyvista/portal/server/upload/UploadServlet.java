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
 * @version $Id$
 */

package com.propertyvista.portal.server.upload;

import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationDocument.DocumentType;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentServletParameters;
import com.propertyvista.portal.server.pt.PtAppContext;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.download.MimeMap;
import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadServlet extends UploadAction {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory.getLogger(UploadServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // set maxumum file size in bytes allowed for upload
        maxSize = 5 * 1024 * 1024;

        // Useful in development mode to slow down the uploads in fast networks.
        // Put the number of milliseconds to sleep in each block received in the server.
        // false or 0, means don't use slow uploads
        //uploadDelay=200;
    }

    /**
     * Override executeAction to save the received files in a custom place
     * and delete this items from session.
     */
    @Override
    public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
        log.debug("UploadServlet.executeAction(): request={}, sessionFiles={}", request, sessionFiles);
        //StringBuilder response = new StringBuilder();
        //int cont = 0;
        FileItem fileItem = null;
        String tenantId = null;
        String documentType = null;

        for (FileItem item : sessionFiles) {
            log.debug("UploadServlet.executeAction(): item={}", item);
            if (item.isFormField()) {
                if (ApplicationDocumentServletParameters.TENANT_ID.equalsIgnoreCase(item.getFieldName())) {
                    tenantId = item.getString();
                    //log.debug("tenantId=" + tenantId);
                } else if (ApplicationDocumentServletParameters.DOCUMENT_TYPE.equalsIgnoreCase(item.getFieldName())) {
                    documentType = item.getString();
                    //log.debug("tenantId=" + tenantId);
                }
            } else {
                fileItem = item;
            }
        }

        log.debug("fileItem={}", fileItem);
        log.debug("tenantId={}", tenantId);
        log.debug("documentType={}", documentType);

        if (fileItem != null && tenantId != null) {
            String contentType = null;
            if (fileItem.getName() != null) {
                int t = fileItem.getName().lastIndexOf(".");
                if (t != -1) {
                    String extension = fileItem.getName().substring(t + 1).trim();
                    contentType = MimeMap.getContentType(extension);
                }
            }
            if (contentType == null)
                throw new UploadActionException("Unknown file extension in file name:" + fileItem.getName());
            if (fileItem.getContentType() != null && !fileItem.getContentType().trim().isEmpty() && !fileItem.getContentType().equalsIgnoreCase(contentType)) {
                throw new UploadActionException("Content type resolved by file name extension (" + contentType
                        + ") does not match to one passed with the upload request (" + fileItem.getContentType() + ")");
            }
            byte[] data = fileItem.get();//IOUtils.toByteArray(in);
            ApplicationDocument applicationDocument = createApplicationDocument(new Long(tenantId), fileItem.getName(), data,
                    ApplicationDocument.DocumentType.valueOf(documentType));
            if (DocumentType.income.equals(ApplicationDocument.DocumentType.valueOf(documentType))) {
                TenantIncome income = PersistenceServicesFactory.getPersistenceService().retrieve(TenantIncome.class, new Long(tenantId));
                income.documents().add(applicationDocument);
                PersistenceServicesFactory.getPersistenceService().merge(income);
            }
            /// Compose a xml message with the full file information
            //response.append("<file-field>").append(fileItem.getFieldName()).append("</file-field>\n");
            //response.append("<file-name>").append(fileItem.getName()).append("</file-name>\n");
            //response.append("<file-size>").append(fileItem.getSize()).append("</file-size>\n");
            //response.append("<file-type>").append(fileItem.getContentType()).append("</file-type>\n");
        } else {
            throw new UploadActionException("ERROR: fileItem, tenantId or documentType is missing: " + fileItem + ", tenantId=" + tenantId + ", documentType="
                    + documentType);
        }

        /// Remove files from session because we have a copy of them
        removeSessionFileItems(request);

        //response.insert(0, "<response>\n");
        //response.append("</response>\n");

        /// Send information of the received files to the client.
        //return response.toString();
        return null;
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
}
