/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 23, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server;

import com.pyx4j.essentials.server.upload.AbstractUploadServlet;

import com.propertyvista.crm.server.services.MediaUploadServiceImpl;
import com.propertyvista.crm.server.services.PmcDocumentFileUploadServiceImpl;
import com.propertyvista.crm.server.services.UpdateUploadServiceImpl;
import com.propertyvista.crm.server.services.admin.SiteImageResourceUploadServiceImpl;
import com.propertyvista.crm.server.services.customer.CustomerPictureUploadServiceImpl;
import com.propertyvista.crm.server.services.customer.TenantPadFileUploadServiceImpl;
import com.propertyvista.operations.server.services.EncryptedStorageServicePrivateKeyUploadServiceImpl;
import com.propertyvista.operations.server.services.ImportUploadServiceImpl;
import com.propertyvista.operations.server.services.MerchantAccountFileUploadServiceImpl;
import com.propertyvista.portal.server.portal.web.services.ResidentPictureUploadServiceImpl;
import com.propertyvista.portal.server.ptapp.services.ApplicationDocumentUploadServiceImpl;

@SuppressWarnings("serial")
public class VistaUploadServlet extends AbstractUploadServlet {

    public VistaUploadServlet() {
        bind(ImportUploadServiceImpl.class);
        bind(UpdateUploadServiceImpl.class);
        bind(MediaUploadServiceImpl.class);
        bind(SiteImageResourceUploadServiceImpl.class);
        bind(CustomerPictureUploadServiceImpl.class);
        bind(ResidentPictureUploadServiceImpl.class);
        bind(ApplicationDocumentUploadServiceImpl.class);
        bind(PmcDocumentFileUploadServiceImpl.class);
        bind(EncryptedStorageServicePrivateKeyUploadServiceImpl.class);
        bind(TenantPadFileUploadServiceImpl.class);
        bind(MerchantAccountFileUploadServiceImpl.class);
    }

}
