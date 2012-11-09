/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.statusviewers;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.OtherProviderTenantInsuranceStatusDTO;

public class OtherProviderTenantInsuranceStatusViewer extends CEntityViewer<OtherProviderTenantInsuranceStatusDTO> {

    private static final I18n i18n = I18n.get(OtherProviderTenantInsuranceStatusViewer.class);

    @Override
    public IsWidget createContent(OtherProviderTenantInsuranceStatusDTO insuranceStatus) {
        FlowPanel contentPanel = new FlowPanel();

        contentPanel.add(new Label(i18n.tr("Personal Liablity: ${0}", insuranceStatus.liabilityCoverage().getStringView())));
        if (!insuranceStatus.expirationDate().isNull()) {
            contentPanel.add(new Label(i18n.tr("Expiration Date: {0}", insuranceStatus.expirationDate().getStringView())));
        }
        return contentPanel;
    }
}
