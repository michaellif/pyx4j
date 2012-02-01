/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.report;

import java.net.ProtocolException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.client.ReportDialog;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.activity.board.CrmBoardViewActivity;
import com.propertyvista.crm.client.ui.report.ReportView;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;
import com.propertyvista.crm.rpc.services.dashboard.BoardMetadataServiceBase;
import com.propertyvista.crm.rpc.services.dashboard.ReportMetadataService;
import com.propertyvista.crm.rpc.services.reports.DashboardReportService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class ReportViewActivity extends CrmBoardViewActivity<ReportView> implements ReportView.Presenter {

    private final static I18n i18n = I18n.get(ReportViewActivity.class);

    private final ReportMetadataService service = GWT.create(ReportMetadataService.class);

    public ReportViewActivity(Place place) {
        this(DashboardViewFactory.instance(ReportView.class), place);
    }

    public ReportViewActivity(ReportView view, Place place) {
        super(view, place);
        view.setPresenter(this);
    }

    @Override
    protected BoardMetadataServiceBase getService() {
        return service;
    }

    @Override
    public void print() {
        EntityQueryCriteria<DashboardMetadata> criteria = new EntityQueryCriteria<DashboardMetadata>(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), view.getData().getPrimaryKey()));
        ReportDialog.start(GWT.<DashboardReportService> create(DashboardReportService.class), criteria);
    }
}