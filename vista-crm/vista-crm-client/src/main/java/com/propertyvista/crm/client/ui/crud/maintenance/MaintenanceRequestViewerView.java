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

import com.pyx4j.site.client.ui.crud.form.IViewerView;

import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.dto.MaintenanceRequestDTO;

public interface MaintenanceRequestViewerView extends IViewerView<MaintenanceRequestDTO> {

    interface Presenter extends IViewerView.Presenter {

        void scheduleAction(ScheduleDataDTO data);

        void resolveAction();

        void rateAction(SurveyResponse rate);

        void cancelAction();
    }
}
