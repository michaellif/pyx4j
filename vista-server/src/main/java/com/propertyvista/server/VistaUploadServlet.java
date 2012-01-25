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

import com.propertyvista.admin.server.services.ImportUploadServiceImpl;
import com.propertyvista.crm.server.services.MediaUploadServiceImpl;
import com.propertyvista.crm.server.services.SiteImageResourcesUploadServiceImpl;
import com.propertyvista.crm.server.services.UpdateUploadServiceImpl;
import com.propertyvista.portal.server.ptapp.services.ApplicationDocumentUploadServiceImpl;

@SuppressWarnings("serial")
public class VistaUploadServlet extends AbstractUploadServlet {

    public VistaUploadServlet() {
        bind(ImportUploadServiceImpl.class);
        bind(UpdateUploadServiceImpl.class);
        bind(MediaUploadServiceImpl.class);
        bind(SiteImageResourcesUploadServiceImpl.class);
        bind(ApplicationDocumentUploadServiceImpl.class);
    }
}
