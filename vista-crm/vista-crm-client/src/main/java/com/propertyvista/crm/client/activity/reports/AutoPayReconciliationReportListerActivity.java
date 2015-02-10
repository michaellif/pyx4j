/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2015
 * @author vlads
 */
package com.propertyvista.crm.client.activity.reports;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.reports.autopayreconciliation.AutoPayReconciliationReportListerView;
import com.propertyvista.crm.rpc.dto.reports.AutoPayReconciliationDTO;

public class AutoPayReconciliationReportListerActivity extends AbstractPrimeListerActivity<AutoPayReconciliationDTO> {

    public AutoPayReconciliationReportListerActivity(AppPlace place) {
        super(AutoPayReconciliationDTO.class, place, CrmSite.getViewFactory().getView(AutoPayReconciliationReportListerView.class));
    }

}
