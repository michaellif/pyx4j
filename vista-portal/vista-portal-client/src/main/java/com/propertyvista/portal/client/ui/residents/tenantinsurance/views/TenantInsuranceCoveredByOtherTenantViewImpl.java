/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.views;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers.TenantInsuranceStatusViewer;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsuranceStatusDTO;

public class TenantInsuranceCoveredByOtherTenantViewImpl extends Composite implements TenantInsuranceCoveredByOtherTenantView {

    private final SimplePanel viewPanel;

    TenantInsuranceStatusViewer statusViewer;

    public TenantInsuranceCoveredByOtherTenantViewImpl() {
        viewPanel = new SimplePanel();
        viewPanel.getElement().getStyle().setPadding(20, Unit.PX);
        statusViewer = new TenantInsuranceStatusViewer();
        viewPanel.setWidget(statusViewer);
        initWidget(viewPanel);
    }

    @Override
    public void populate(InsuranceStatusDTO status) {
        statusViewer.populate(status);
    }

}
