/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.pmc;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.rpc.upload.UploadService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.AbstractListerActivity;

import com.propertyvista.common.client.ui.components.UploadDialogBase;
import com.propertyvista.common.client.ui.components.UploadResponseDownloadableReciver;
import com.propertyvista.dto.DownloadableUploadResponseDTO;
import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.ui.crud.pmc.PmcListerView;
import com.propertyvista.operations.rpc.dto.PmcDTO;
import com.propertyvista.operations.rpc.services.MerchantAccountFileUploadService;
import com.propertyvista.operations.rpc.services.PmcCrudService;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class PmcListerActivity extends AbstractListerActivity<PmcDTO> implements PmcListerView.Presenter {

    private static final I18n i18n = I18n.get(PmcListerActivity.class);

    @SuppressWarnings("unchecked")
    public PmcListerActivity(Place place) {
        super(place, OperationsSite.getViewFactory().instantiate(PmcListerView.class), (AbstractCrudService<PmcDTO>) GWT.create(PmcCrudService.class),
                PmcDTO.class);

    }

    @Override
    public void uploadMerchantAccounts() {
        UploadDialogBase<IEntity, DownloadableUploadResponseDTO> dialog = new UploadDialogBase<IEntity, DownloadableUploadResponseDTO>(
                i18n.tr("Upload Merchant Accounts"),
                GWT.<UploadService<IEntity, DownloadableUploadResponseDTO>> create(MerchantAccountFileUploadService.class),
                MerchantAccountFileUploadService.SUPPORTED_FORMATS);

        UploadResponseDownloadableReciver<DownloadableUploadResponseDTO> r = new UploadResponseDownloadableReciver<DownloadableUploadResponseDTO>(
                i18n.tr("Merchant Accounts Upload"));
        r.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
        dialog.setUploadReciver(r);
        dialog.show();
    }
}
