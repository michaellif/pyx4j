/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-06
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.customer.TenantPadFileUploadService;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceDeferredProcess;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceServiceImpl;

public class TenantPadFileUploadServiceImpl extends AbstractUploadWithDownloadableResponceServiceImpl<IEntity> implements TenantPadFileUploadService {

    private static final I18n i18n = I18n.get(TenantPadFileUploadServiceImpl.class);

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.XLS, DownloadFormat.XLSX, DownloadFormat.CSV);

    @Override
    public long getMaxSize() {
        return 25 * 1024 * 1024;
    }

    @Override
    public Collection<String> getSupportedExtensions() {
        return DownloadFormat.getExtensions(supportedFormats);
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Tenant PAD File");
    }

    @Override
    protected AbstractUploadWithDownloadableResponceDeferredProcess<IEntity> createUploadDeferredProcess(IEntity data) {
        return new TenantPadFileUploadDeferredProcess(data);
    }

}
