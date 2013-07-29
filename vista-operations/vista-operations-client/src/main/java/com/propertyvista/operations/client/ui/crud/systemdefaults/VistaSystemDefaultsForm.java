/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.systemdefaults;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.components.EquifaxFeeQuoteForm;
import com.propertyvista.operations.client.ui.components.MerchantAccountForm;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.VistaSystemDefaultsDTO;

public class VistaSystemDefaultsForm extends OperationsEntityForm<VistaSystemDefaultsDTO> {

    public static I18n i18n = I18n.get(VistaSystemDefaultsForm.class);

    public VistaSystemDefaultsForm(IForm<VistaSystemDefaultsDTO> view) {
        super(VistaSystemDefaultsDTO.class, view);
        createTabs();
    }

    private void createTabs() {
        selectTab(addTab(makeEquifaxSettingsTab()));
        addTab(makeMistaMerchantAccountTab());
        addTab(makePaymentSettingsTab());
        addTab(makeTenantSureSettingsTab());
    }

    private TwoColumnFlexFormPanel makePaymentSettingsTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Funds Transfer"));
        int row = -1;
        panel.setH1(++row, 0, 1, i18n.tr("Default Fees"));
        // TODO inject fees form
        panel.setWidget(++row, 0, new HTML(i18n.tr("TODO: setupFees")));
        return panel;
    }

    private TwoColumnFlexFormPanel makeEquifaxSettingsTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Equifax"));
        int row = -1;
        panel.setH1(++row, 0, 1, i18n.tr("Default Fees"));
        panel.setWidget(++row, 0, inject(proto().equifaxFees(), new EquifaxFeeQuoteForm(true)));

        panel.setH1(++row, 0, 1, i18n.tr("Default  Usage Limits"));
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxLimit().dailyReports())).componentWidth(6).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifaxLimit().dailyRequests())).componentWidth(6).build());

        return panel;
    }

    private TwoColumnFlexFormPanel makeMistaMerchantAccountTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("Vista Accounts"));
        int row = -1;
        panel.setH1(++row, 0, 1, i18n.tr("Vista Merchant Account"));
        panel.setWidget(++row, 0, inject(proto().vistaMerchantAccount(), new MerchantAccountForm()));
        return panel;
    }

    private TwoColumnFlexFormPanel makeTenantSureSettingsTab() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel(i18n.tr("TenantSure"));
        int row = -1;
        panel.setH1(++row, 0, 1, i18n.tr("Merchant Account"));
        panel.setWidget(++row, 0, inject(proto().tenantSureMerchantAccount(), new MerchantAccountForm()));
        return panel;
    }

}
