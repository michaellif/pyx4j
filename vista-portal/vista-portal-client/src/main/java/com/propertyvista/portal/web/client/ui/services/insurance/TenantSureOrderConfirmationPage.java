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
package com.propertyvista.portal.web.client.ui.services.insurance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;

public class TenantSureOrderConfirmationPage extends CPortalEntityForm<TenantSureInsurancePolicyDTO> {

    private static final I18n i18n = I18n.get(TenantSureOrderConfirmationPage.class);

    public TenantSureOrderConfirmationPage(TenantSureOrderConfirmationPageViewImpl view) {
        super(TenantSureInsurancePolicyDTO.class, view, i18n.tr("Insurance Request submitted Successfully!"), ThemeColor.contrast4);

        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel mainPanel = new TwoColumnFlexFormPanel();

        return mainPanel;
    }

}
