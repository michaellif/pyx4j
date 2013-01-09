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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.admin.CreditCheckStatusDTO;

public class CreditCheckStatusForm extends CrmEntityForm<CreditCheckStatusDTO> {

    private static final I18n i18n = I18n.get(CreditCheckStatusForm.class);

    public CreditCheckStatusForm(IFormView<CreditCheckStatusDTO> view) {
        super(CreditCheckStatusDTO.class, view);
        FormFlexPanel contentPanel = new FormFlexPanel();
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
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().setupFee())).build());
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().perApplicantFee())).build());
        contentPanel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        selectTab(addTab(contentPanel));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        BigDecimal setupFee = getValue().setupFee().getValue();
        get(proto().setupFee()).setVisible(!(setupFee == null || setupFee.equals(new BigDecimal("0.00")) || setupFee.equals(BigDecimal.ZERO)));
    }

}
