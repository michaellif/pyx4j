/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.crud.administration.availablereport;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.ui.crud.administration.availablereport.AvailableCrmReportEditorView;
import com.propertyvista.crm.rpc.services.admin.AvailableCrmReportAdminCrudService;
import com.propertyvista.domain.reports.AvailableCrmReport;

public class AvailableCrmReportEditorActivity extends CrmEditorActivity<AvailableCrmReport> {

    public AvailableCrmReportEditorActivity(CrudAppPlace place) {
        super(AvailableCrmReport.class, place, CrmSite.getViewFactory().getView(AvailableCrmReportEditorView.class), GWT
                .<AbstractCrudService<AvailableCrmReport>> create(AvailableCrmReportAdminCrudService.class));
    }

    @Override
    protected void onSaveSuccess(Key result) {
        super.onSaveSuccess(result);
        AppSite.getEventBus().fireEvent(new BoardUpdateEvent(AvailableCrmReport.class));
    }
}
