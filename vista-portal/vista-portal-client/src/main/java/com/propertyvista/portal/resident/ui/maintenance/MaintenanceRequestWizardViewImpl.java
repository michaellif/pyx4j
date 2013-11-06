/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.maintenance;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.portal.resident.ui.AbstractWizardView;
import com.propertyvista.portal.resident.ui.IWizardView.IWizardPresenter;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;

public class MaintenanceRequestWizardViewImpl extends AbstractWizardView<MaintenanceRequestDTO> implements MaintenanceRequestWizardView {

    public MaintenanceRequestWizardViewImpl() {
        setWizard(new MaintenanceRequestWizard(this));
    }

    @Override
    public void setPresenter(IWizardPresenter<MaintenanceRequestDTO> presenter) {
        super.setPresenter(presenter);
        if (presenter != null) {
            ((MaintenanceRequestWizardPresenter) presenter).getCategoryMeta(new DefaultAsyncCallback<MaintenanceRequestMetadata>() {
                @Override
                public void onSuccess(MaintenanceRequestMetadata meta) {
                    ((MaintenanceRequestWizard) getWizard()).setMaintenanceRequestCategoryMeta(meta);
                }
            });
        }
    }
}
