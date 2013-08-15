/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.maintenance;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.domain.maintenance.MaintenanceRequestStatus.StatusPhase;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestMetadataDTO;
import com.propertyvista.portal.web.client.ui.IEntityView;
import com.propertyvista.portal.web.client.ui.residents.EditImpl;

public class EditMaintenanceRequestViewImpl extends EditImpl<MaintenanceRequestDTO> implements EditMaintenanceRequestView {

    public EditMaintenanceRequestViewImpl() {
        super(new MaintenanceRequestForm());
    }

    @Override
    public void setPresenter(IEntityView.EntityPresenter<MaintenanceRequestDTO> presenter) {
        super.setPresenter(presenter);
        ((EditMaintenanceRequestView.Presenter) presenter).getCategoryMeta(new DefaultAsyncCallback<MaintenanceRequestMetadataDTO>() {
            @Override
            public void onSuccess(MaintenanceRequestMetadataDTO meta) {
                ((MaintenanceRequestForm) getForm()).setMaintenanceRequestCategoryMeta(meta);
            }
        });
    }

    @Override
    public void populate(MaintenanceRequestDTO value) {
        boolean editable = (value.status().isNull() || value.status().phase().getValue() == StatusPhase.Submitted);

        getForm().setViewable(!editable);

        getSubmit().setVisible(editable);
        getCancel().setText(editable ? i18n.tr("Cancel") : i18n.tr("Back"));
        super.populate(value);
    }
}
