/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms;

import java.math.BigDecimal;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.CViewer;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.tenantinsurance.MoneyComboBox;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers.TenantInsuranceStatusViewer;
import com.propertyvista.portal.rpc.portal.web.dto.insurance.TenantSureInsuranceStatusDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;

public class TenantSureStatusForm extends CEntityDecoratableForm<TenantSureInsuranceStatusDTO> {

    private static final I18n i18n = I18n.get(TenantSureStatusForm.class);

    public static class TenantSureMessagesViewer extends CViewer<IList<TenantSureMessageDTO>> {

        @Override
        public IsWidget createContent(IList<TenantSureMessageDTO> value) {
            FlowPanel panel = new FlowPanel();
            if (value.isEmpty()) {

            } else {
                for (TenantSureMessageDTO message : value) {
                    Label messageLabel = new Label(message.messageText().getValue());
                    messageLabel.addStyleName(TenantInsuranceStatusViewer.Styles.TenantInsuranceWarningText.name());
                    panel.add(messageLabel);
                }
            }

            return panel;
        }

    }

    public TenantSureStatusForm() {
        super(TenantSureInsuranceStatusDTO.class);
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        panel.setH3(++row, 0, 1, i18n.tr("Coverage"));
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().insuranceCertificateNumber()), 10).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().inceptionDate()), 10).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().expiryDate()), 10).build());

        // TODO maybe create a separate coverage viewer?
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().liabilityCoverage()), 10).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().contentsCoverage()), 10).build());

        // TODO investigate why format of annotated on the field doesn't work
        IFormat<BigDecimal> currencyFormat = new MoneyComboBox.MoneyComboBoxFormat();
        ((CTextFieldBase<BigDecimal, ?>) get(proto().liabilityCoverage())).setFormat(currencyFormat);
        ((CTextFieldBase<BigDecimal, ?>) get(proto().contentsCoverage())).setFormat(currencyFormat);

        panel.setH3(++row, 0, 1, i18n.tr("Annual Payment"));
        panel.setWidget(++row, 0, inject(proto().annualPaymentDetails(), new TenantSurePaymentViewer()));

        panel.setH3(++row, 0, 1, i18n.tr("Next Monthly Payment"));
        panel.setWidget(++row, 0, inject(proto().nextPaymentDetails(), new TenantSurePaymentViewer()));

        panel.setWidget(++row, 0, inject(proto().messages(), new TenantSureMessagesViewer()));

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        boolean hasContextCoverage = !getValue().contentsCoverage().isNull() && !getValue().contentsCoverage().getValue().equals(new BigDecimal("0.00"));
        get(proto().contentsCoverage()).setVisible(hasContextCoverage);
        get(proto().expiryDate()).setVisible(!getValue().expiryDate().isNull());
    }
}
