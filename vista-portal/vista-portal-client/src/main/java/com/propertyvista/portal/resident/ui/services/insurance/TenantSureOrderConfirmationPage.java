/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.portal.resident.themes.TenantSureTheme;
import com.propertyvista.portal.resident.ui.CPortalEntityForm;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;

public class TenantSureOrderConfirmationPage extends CPortalEntityForm<TenantSureInsurancePolicyDTO> {

    private static final I18n i18n = I18n.get(TenantSureOrderConfirmationPage.class);

    public TenantSureOrderConfirmationPage(TenantSureOrderConfirmationPageViewImpl view) {
        super(TenantSureInsurancePolicyDTO.class, view, i18n.tr("TenanSure Order has been processed successfully!"), ThemeColor.contrast3);

        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel mainPanel = new TwoColumnFlexFormPanel();
        int row = -1;

        Label label = new Label();
        label.addStyleName(TenantSureTheme.StyleName.TSPucrhaseViewMessageText.name());
        label.setText(i18n.tr("An email with your insurance policy has been sent to your email."));
        mainPanel.setWidget(++row, 0, label);
        return mainPanel;
    }

}
