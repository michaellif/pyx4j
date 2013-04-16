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

import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.essentials.server.upload.AbstractUploadServiceImpl;
import com.pyx4j.essentials.server.upload.UploadData;
import com.pyx4j.essentials.server.upload.UploadDeferredProcess;
import com.pyx4j.gwt.rpc.upload.UploadResponse;
import com.pyx4j.gwt.shared.DownloadFormat;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.customer.TenantPadFileUploadService;
import com.propertyvista.interfaces.importer.model.PadFileModel;
import com.propertyvista.interfaces.importer.pad.TenantPadParser;
import com.propertyvista.interfaces.importer.pad.TenantPadProcessor;

public class TenantPadFileUploadServiceImpl extends AbstractUploadServiceImpl<IEntity, IEntity> implements TenantPadFileUploadService {

    private static final I18n i18n = I18n.get(TenantPadFileUploadServiceImpl.class);

    @Override
    public long getMaxSize() {
        return 25 * 1024 * 1024;
    }

    @Override
    public String getUploadFileTypeName() {
        return i18n.tr("Tenant PAD File");
    }

    @Override
    public com.pyx4j.essentials.server.upload.UploadReciver.ProcessingStatus onUploadReceived(UploadData data, UploadDeferredProcess<IEntity, IEntity> process,
            UploadResponse<IEntity> response) {

        List<PadFileModel> model = new TenantPadParser().parsePads(data.data, DownloadFormat.valueByExtension(FilenameUtils.getExtension(response.fileName)));
        response.message = new TenantPadProcessor().process(model);

        return ProcessingStatus.completed;
    }

}
