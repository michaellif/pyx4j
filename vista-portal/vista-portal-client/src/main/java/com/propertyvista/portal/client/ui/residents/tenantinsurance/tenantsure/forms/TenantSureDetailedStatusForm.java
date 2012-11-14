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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CEntityViewer;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.BillingTheme;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.dashboard.statusviewers.TenantInsuranceStatusViewer;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureMessageDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePremiumTaxDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureQuoteDetailedDTO;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSureTenantInsuranceDetailedStatusDTO;

public class TenantSureDetailedStatusForm extends CEntityDecoratableForm<TenantSureTenantInsuranceDetailedStatusDTO> {

    private static final I18n i18n = I18n.get(TenantSureDetailedStatusForm.class);

    public static class TenantSurePremiumTaxFolder extends VistaTableFolder<TenantSurePremiumTaxDTO> {

        public TenantSurePremiumTaxFolder() {
            super(TenantSurePremiumTaxDTO.class, false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                    new EntityFolderColumnDescriptor(proto().taxName(), "10em"),
                    new EntityFolderColumnDescriptor(proto().absoluteAmount(), "15em")
            );//@formatter:on
        }
    }

    public static class TenantSureMessagesViewer extends CEntityViewer<IList<TenantSureMessageDTO>> {

        @Override
        public IsWidget createContent(IList<TenantSureMessageDTO> value) {
            FlowPanel panel = new FlowPanel();
            if (value.isEmpty()) {

            } else {
                for (TenantSureMessageDTO message : value) {
                    Label messageLabel = new Label(message.message().getValue());
                    messageLabel.addStyleName(TenantInsuranceStatusViewer.Styles.TenantInsuranceWarningText.name());
                    panel.add(messageLabel);
                }
            }

            return panel;
        }

    }

    public static class TenantSureQuoteViewer extends CEntityViewer<TenantSureQuoteDetailedDTO> {

        @Override
        public IsWidget createContent(TenantSureQuoteDetailedDTO value) {
            FormFlexPanel content = new FormFlexPanel();
            int row = 0;
            addDetailRecord(content, ++row, value.grossPremium().getMeta().getCaption(), value.grossPremium().getStringView());
            addDetailRecord(content, ++row, value.underwriterFee().getMeta().getCaption(), value.underwriterFee().getStringView());
            for (TenantSurePremiumTaxDTO tax : value.taxBreakdown()) {
                addDetailRecord(content, ++row, tax.taxName().getValue(), tax.absoluteAmount().getStringView());
            }
            addTotalRecord(content, ++row, value.totalPayable().getMeta().getCaption(), value.totalPayable().getStringView());

            return content;
        }

        private void addDetailRecord(FlexTable table, int row, String description, String amount) {
            table.setHTML(row, 1, description);
            table.setHTML(row, 2, amount);
            // styling:
            table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailItem.name());
            table.getFlexCellFormatter().setStyleName(row, 0, BillingTheme.StyleName.BillingDetailItemDate.name());
            table.getFlexCellFormatter().setStyleName(row, 1, BillingTheme.StyleName.BillingDetailItemTitle.name());
            table.getFlexCellFormatter().setStyleName(row, 2, BillingTheme.StyleName.BillingDetailItemAmount.name());
        }

        private void addTotalRecord(FlexTable table, int row, String description, String amount) {
            table.setHTML(row, 1, description);
            table.setHTML(row, 2, amount);
            // styling:
            table.getRowFormatter().setStyleName(row, BillingTheme.StyleName.BillingDetailTotal.name());
            table.getFlexCellFormatter().setStyleName(row, 1, BillingTheme.StyleName.BillingDetailTotalTitle.name());
            table.getFlexCellFormatter().setStyleName(row, 2, BillingTheme.StyleName.BillingDetailTotalAmount.name());

        }

    }

    public TenantSureDetailedStatusForm() {
        super(TenantSureTenantInsuranceDetailedStatusDTO.class);
        setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;

        panel.setH3(++row, 0, 1, i18n.tr("Coverage"));
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().quote().policy().personalLiabilityCoverage())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().quote().policy().contentsCoverage())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().quote().policy().inceptionDate())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().quote().policy().expiryDate())).build());

        panel.setH3(++row, 0, 1, i18n.tr("Total Payment"));
        panel.setWidget(++row, 0, inject(proto().quote(), new TenantSureQuoteViewer()));

        panel.setH3(++row, 0, 1, i18n.tr("Monthly Payment"));
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().monthlyPremiumPayment())).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nextPaymentDate())).build());

        panel.setWidget(++row, 0, inject(proto().messages(), new TenantSureMessagesViewer()));

        return panel;
    }
}
