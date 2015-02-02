/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.admin.CreditCheckStatusDTO;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;

public class CreditCheckStatusForm extends CrmEntityForm<CreditCheckStatusDTO> {

    private static final I18n i18n = I18n.get(CreditCheckStatusForm.class);

    public CreditCheckStatusForm(IPrimeFormView<CreditCheckStatusDTO, ?> view) {
        super(CreditCheckStatusDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Powered By"));
        formPanel.append(Location.Dual, new Image(CreditCheckWizardResources.INSTANCE.equifaxLogo()));
        formPanel.append(Location.Dual, new HTML("&nbsp;"));

        formPanel.append(Location.Dual, proto().status());
        get(proto().status()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLD);
        get(proto().status()).asWidget().getElement().getStyle().setFontSize(1.3, Unit.EM);
        formPanel.append(Location.Dual, new HTML("&nbsp;"));

        formPanel.append(Location.Dual, proto().reportType()).decorate();
        formPanel.append(Location.Dual, proto().setupFee()).decorate();
        formPanel.append(Location.Dual, proto().perApplicantFee()).decorate();

        selectTab(addTab(formPanel, i18n.tr("Credit Check Status")));
        setTabBarVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        BigDecimal setupFee = getValue().setupFee().getValue();
        get(proto().setupFee())
                .setVisible(
                        ((getValue().status().getValue() == PmcEquifaxStatus.PendingEquifaxApproval) | (getValue().status().getValue() == PmcEquifaxStatus.PendingVistaApproval))
                                & !(setupFee == null || setupFee.compareTo(BigDecimal.ZERO) == 0));
    }
}
