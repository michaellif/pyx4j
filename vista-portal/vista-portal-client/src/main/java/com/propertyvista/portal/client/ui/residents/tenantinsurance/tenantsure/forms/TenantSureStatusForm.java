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
import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers.TenantInsuranceStatusViewer;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceStatusDetailedDTO;

public class TenantSureStatusForm extends CEntityDecoratableForm<TenantSureTenantInsuranceStatusDetailedDTO> {

    private static final I18n i18n = I18n.get(TenantSureStatusForm.class);

    public static class TenantSureMessagesViewer extends CEntityViewer<IList<TenantSureMessageDTO>> {

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
        super(TenantSureTenantInsuranceStatusDetailedDTO.class);
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;

        panel.setH3(++row, 0, 1, i18n.tr("Coverage"));
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceCertificateNumber())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().quote().coverage().personalLiabilityCoverage())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().quote().coverage().contentsCoverage())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().quote().coverage().inceptionDate())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().expiryDate())).build());

        panel.setH3(++row, 0, 1, i18n.tr("Payment Details"));
        panel.setWidget(++row, 0, inject(proto().quote(), new TenantSureQuoteViewer()));
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nextPaymentDate())).build());

        panel.setWidget(++row, 0, inject(proto().messages(), new TenantSureMessagesViewer()));

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().quote().coverage().contentsCoverage()).setVisible(
                !getValue().quote().coverage().contentsCoverage().isNull()
                        && !getValue().quote().coverage().contentsCoverage().getValue().equals(new BigDecimal("0.00")));
        get(proto().expiryDate()).setVisible(!getValue().expiryDate().isNull());
        get(proto().nextPaymentDate()).setVisible(!getValue().nextPaymentDate().isNull());
    }
}
