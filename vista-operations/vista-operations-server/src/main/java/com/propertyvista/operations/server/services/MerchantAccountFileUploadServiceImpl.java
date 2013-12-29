/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import java.util.Collection;
import java.util.EnumSet;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.operations.rpc.services.MerchantAccountFileUploadService;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceDeferredProcess;
import com.propertyvista.server.common.upload.AbstractUploadWithDownloadableResponceServiceImpl;

public class MerchantAccountFileUploadServiceImpl extends AbstractUploadWithDownloadableResponceServiceImpl<IEntity> implements
        MerchantAccountFileUploadService {

    private static final I18n i18n = I18n.get(MerchantAccountFileUploadServiceImpl.class);

    public static final Collection<DownloadFormat> supportedFormats = EnumSet.of(DownloadFormat.XLS, DownloadFormat.XLSX);

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
        return i18n.tr("Merchant Accounts File");
    }

    @Override
    protected AbstractUploadWithDownloadableResponceDeferredProcess<IEntity> createUploadDeferredProcess(IEntity data) {
        return new MerchantAccountFileUploadDeferredProcess(data);
    }

}
