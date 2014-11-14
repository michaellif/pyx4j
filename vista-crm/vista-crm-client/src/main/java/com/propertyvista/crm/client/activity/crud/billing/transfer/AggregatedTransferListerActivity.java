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
package com.propertyvista.crm.client.activity.crud.billing.transfer;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.essentials.rpc.report.ReportService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.billing.transfer.AggregatedTransferListerView;
import com.propertyvista.crm.rpc.services.financial.AggregatedTransferDownloadService;
import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class AggregatedTransferListerActivity extends AbstractPrimeListerActivity<AggregatedTransfer> implements AggregatedTransferListerView.Presenter {

    private static final I18n i18n = I18n.get(AggregatedTransferListerActivity.class);

    public AggregatedTransferListerActivity(AppPlace place) {
        super(AggregatedTransfer.class, place, CrmSite.getViewFactory().getView(AggregatedTransferListerView.class));
    }

    @Override
    public void downloadAggregatedTransferFile() {
        EntityListCriteria<AggregatedTransfer> criteria = EntityListCriteria.create(AggregatedTransfer.class);
        criteria.setSorts(getView().getSortCriteria());

        if (getView().getFilters() != null) {
            for (Criterion fd : getView().getFilters()) {
                if (fd instanceof PropertyCriterion) {
                    if (((PropertyCriterion) fd).isValid()) {
                        criteria.add(fd);
                    }
                } else {
                    criteria.add(fd);
                }
            }
        }

        ReportDialog d = new ReportDialog(i18n.tr(""), i18n.tr("Generating file..."));
        d.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);
        d.start(GWT.<ReportService<?>> create(AggregatedTransferDownloadService.class), criteria, null);
    }
}
