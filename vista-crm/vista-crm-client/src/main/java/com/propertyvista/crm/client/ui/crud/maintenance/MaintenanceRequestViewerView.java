/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.maintenance;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeViewerView;

import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestWorkOrder;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestWorkOrderDTO;

public interface MaintenanceRequestViewerView extends IPrimeViewerView<MaintenanceRequestDTO> {

    interface Presenter extends IPrimeViewerView.IPrimeViewerPresenter {

        void scheduleAction(MaintenanceRequestWorkOrderDTO schedule);

        void updateProgressAction(MaintenanceRequestWorkOrder schedule);

        void resolveAction(MaintenanceRequestDTO mr);

        void rateAction(SurveyResponse rate);

        void cancelAction();

        void getCategoryMeta(AsyncCallback<MaintenanceRequestMetadata> callback, Key buildingId);
    }
}
