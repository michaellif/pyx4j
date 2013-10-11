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
package com.propertyvista.portal.web.client.ui.services.insurance.tenantsurepaymentmethod;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsurancePaymentMethodDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityForm;

public class TenantSurePaymentMethodUpdateConfirmationForm extends CPortalEntityForm<InsurancePaymentMethodDTO> {

    private static final I18n i18n = I18n.get(TenantSurePaymentMethodUpdateConfirmationForm.class);

    public TenantSurePaymentMethodUpdateConfirmationForm(TenantSurePaymentMethodUpdateConfirmationView view) {
        super(InsurancePaymentMethodDTO.class, view, i18n.tr("New Payment Method Submitted Successfully!"), ThemeColor.contrast3);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel mainPanel = new BasicFlexFormPanel();
        mainPanel.setWidget(0, 0, new HTML(i18n.tr("Thank You!")));
        mainPanel.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        return mainPanel;

    }

}
