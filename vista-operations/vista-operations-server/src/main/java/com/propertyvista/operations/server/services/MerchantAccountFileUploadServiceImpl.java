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

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.interfaces.importer.processor.MerchantAccountProcessor;
import com.propertyvista.operations.rpc.services.MerchantAccountFileUploadService;

public class MerchantAccountFileUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, IEntity> implements MerchantAccountFileUploadService {

    private static final I18n i18n = I18n.get(MerchantAccountFileUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return 25 * 1024 * 1024;
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Merchant Accounts File");
    }

    @Override
    public com.pyx4j.essentials.server.upload.UploadReciver.ProcessingStatus onUploadReceived(UploadData data, UploadDeferredProcess<IEntity, IEntity> process,
            UploadResponse<IEntity> response) {
        response.message = new MerchantAccountProcessor().persistMerchantAccounts(data.data,
                DownloadFormat.valueByExtension(FilenameUtils.getExtension(response.fileName)));
        return ProcessingStatus.completed;
    }

}
