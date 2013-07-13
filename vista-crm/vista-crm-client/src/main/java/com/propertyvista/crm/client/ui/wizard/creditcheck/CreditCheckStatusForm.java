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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.admin.CreditCheckStatusDTO;
import com.propertyvista.domain.pmc.PmcEquifaxStatus;

public class CreditCheckStatusForm extends CrmEntityForm<CreditCheckStatusDTO> {

    private static final I18n i18n = I18n.get(CreditCheckStatusForm.class);

    public CreditCheckStatusForm(IForm<CreditCheckStatusDTO> view) {
        super(CreditCheckStatusDTO.class, view);
        TwoColumnFlexFormPanel contentPanel = new TwoColumnFlexFormPanel(i18n.tr("Credit Check Status"));
        int row = -1;
        Label poweredByLabel = new Label();
        poweredByLabel.setText(i18n.tr("Powered By"));
        contentPanel.setWidget(++row, 0, poweredByLabel);
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.setWidget(++row, 0, new Image(CreditCheckWizardResources.INSTANCE.equifaxLogo()));
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        contentPanel.setWidget(++row, 0, new HTML("&nbsp;"));

        contentPanel.setWidget(++row, 0, inject(proto().status()));
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setFontWeight(FontWeight.BOLD);
        contentPanel.getFlexCellFormatter().getElement(row, 0).getStyle().setFontSize(1.3, Unit.EM);
        contentPanel.setWidget(++row, 0, new HTML("&nbsp;"));

        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().reportType())).build());
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().setupFee())).build());
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().perApplicantFee())).build());
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        selectTab(addTab(contentPanel));
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
