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

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentServletParameters;
import com.propertyvista.portal.server.pt.PtAppContext;
import com.propertyvista.portal.server.pt.services.ApplicationEntityServiceImpl;
import com.propertyvista.server.domain.ApplicationDocumentData;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.MimeMap;

public class UploadServlet extends UploadAction {

    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory.getLogger(UploadServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // set maximum file size in bytes allowed for upload
        maxSize = EntityFactory.getEntityPrototype(ApplicationDocumentData.class).data().getMeta().getLength();

        // Useful in development mode to slow down the uploads in fast networks.
        // Put the number of milliseconds to sleep in each block received in the server.
        // false or 0, means don't use slow uploads
        //uploadDelay=200;
    }

    /**
     * Override executeAction to save the received files in a custom place and delete this
     * items from session.
     */
    @Override
    public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
        log.debug("UploadServlet.executeAction(): request={}, sessionFiles={}", request, sessionFiles);
        FileItem fileItem = null;
        String tenantId = null;
        //String incomeId = null;
        //String documentType = null;

        for (FileItem item : sessionFiles) {
            log.debug("UploadServlet.executeAction(): item={}", item);
            if (item.isFormField()) {
                if (ApplicationDocumentServletParameters.TENANT_ID.equalsIgnoreCase(item.getFieldName())) {
                    tenantId = item.getString();
                    //log.debug("tenantId=" + tenantId);
                } else if (ApplicationDocumentServletParameters.INCOME_ID.equalsIgnoreCase(item.getFieldName())) {
                    //incomeId = item.getString();
                    //log.debug("tenantId=" + tenantId);
                } else if (ApplicationDocumentServletParameters.DOCUMENT_TYPE.equalsIgnoreCase(item.getFieldName())) {
                    //documentType = item.getString();
                    //log.debug("tenantId=" + tenantId);
                }
            } else {
                fileItem = item;
            }
        }

        log.debug("fileItem={}", fileItem);
        log.debug("tenantId={}", tenantId);

        if (fileItem != null && tenantId != null) {
            String contentType = null;
            if (fileItem.getName() != null) {
                int t = fileItem.getName().lastIndexOf(".");
                if (t != -1) {
                    String extension = fileItem.getName().substring(t + 1).trim();
                    try {
                        if (!ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS.contains(DownloadFormat.valueByExtension(extension))) {
                            throw new Exception();
                        }
                    } catch (Exception e) {
                        throw new UploadActionException("Unsupported file extension in file name:" + fileItem.getName() + ". List of supported extensions: "
                                + ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS);
                    }
                    contentType = MimeMap.getContentType(extension);
                } else {
                    throw new UploadActionException("There's no extension in file name:" + fileItem.getName());
                }
            }
            if (contentType == null)
                throw new UploadActionException("Unknown file extension in file name:" + fileItem.getName());
            if (fileItem.getContentType() != null && !fileItem.getContentType().trim().isEmpty() && !fileItem.getContentType().equalsIgnoreCase(contentType)) {
                throw new UploadActionException("Content type resolved by file name extension (" + contentType
                        + ") does not match to one passed with the upload request (" + fileItem.getContentType() + ")");
            }
            PotentialTenantInfo tenant = PersistenceServicesFactory.getPersistenceService().retrieve(PotentialTenantInfo.class, new Long(tenantId));
            if (tenant == null) {
                throw new UploadActionException("Unknown tenantId: " + tenantId);
            }
            if (!tenant.application().id().getValue().equals(PtAppContext.getCurrentUserApplication().id().getValue())) {
                throw new UploadActionException("Wrong TenantId: " + tenantId);
            }

            byte[] data = fileItem.get();//IOUtils.toByteArray(in);
            ApplicationDocumentData applicationDocumentData = createApplicationDocumentData(data, tenant);

            //StringBuilder response = new StringBuilder();
            //response.append("<fileField>").append(fileItem.getFieldName()).append("</fileField>\n");
            //response.append("<fileName>").append(fileItem.getName()).append("</fileName>");
            //response.append("<fileSize>").append(fileItem.getSize()).append("</fileSize>");
            //response.append("<fileType>").append(contentType).append("</fileType>");
            //response.append("<dataId>").append(applicationDocumentData.id().getValue()).append("</dataId>");
            //return response.toString();
            return applicationDocumentData.id().getValue().toString();
        } else {
            throw new UploadActionException("ERROR: fileItem or tenantId is missing");
        }

        /// Remove files from session because we have a copy of them
        //removeSessionFileItems(request);

        //response.insert(0, "<response>\n");
        //response.append("</response>\n");

        /// Send information of the received files to the client.
        //return response.toString();
        //return null;
    }

    private ApplicationDocumentData createApplicationDocumentData(byte[] data, PotentialTenantInfo tenant) {
        ApplicationDocumentData applicationDocumentData = EntityFactory.create(ApplicationDocumentData.class);
        applicationDocumentData.data().setValue(data);
        applicationDocumentData.tenant().set(tenant);
        applicationDocumentData.application().set(tenant.application());
        ApplicationEntityServiceImpl.saveApplicationEntity(applicationDocumentData);
        return applicationDocumentData;
    }
}
