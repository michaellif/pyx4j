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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.operations.client.ui.components.EquifaxFeeQuoteForm;
import com.propertyvista.operations.client.ui.components.MerchantAccountForm;
import com.propertyvista.operations.client.ui.components.PaymentFeesForm;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.operations.rpc.dto.VistaSystemDefaultsDTO;

public class VistaSystemDefaultsForm extends OperationsEntityForm<VistaSystemDefaultsDTO> {

    public static I18n i18n = I18n.get(VistaSystemDefaultsForm.class);

    public VistaSystemDefaultsForm(IFormView<VistaSystemDefaultsDTO, ?> view) {
        super(VistaSystemDefaultsDTO.class, view);
        createTabs();
    }

    private void createTabs() {
        selectTab(addTab(makeEquifaxSettingsTab(), i18n.tr("Equifax")));
        addTab(makeMistaMerchantAccountTab(), i18n.tr("Vista Accounts"));
        addTab(makePaymentSettingsTab(), i18n.tr("Funds Transfer"));
        addTab(makeTenantSureSettingsTab(), i18n.tr("TenantSure"));
    }

    private FormPanel makeEquifaxSettingsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Default Fees"));
        formPanel.append(Location.Dual, proto().equifaxFees(), new EquifaxFeeQuoteForm(true, true));

        formPanel.h1(i18n.tr("Default  Usage Limits"));
        formPanel.append(Location.Dual, proto().equifaxLimit().dailyReports()).decorate().componentWidth(72);
        formPanel.append(Location.Dual, proto().equifaxLimit().dailyRequests()).decorate().componentWidth(72);

        return formPanel;
    }

    private FormPanel makeMistaMerchantAccountTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Vista Merchant Account, Payments BMO"));
        formPanel.append(Location.Dual, proto().vistaMerchantAccountPayments(), new MerchantAccountForm());

        formPanel.h1(i18n.tr("Vista Merchant Account, Equifax"));
        formPanel.append(Location.Dual, proto().vistaMerchantAccountEquifax(), new MerchantAccountForm());
        return formPanel;
    }

    private FormPanel makePaymentSettingsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Default Fees"));
        formPanel.append(Location.Dual, proto().paymentFees(), new PaymentFeesForm<DefaultPaymentFees>(DefaultPaymentFees.class));
        return formPanel;
    }

    private FormPanel makeTenantSureSettingsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Merchant Account"));
        formPanel.append(Location.Dual, proto().tenantSureMerchantAccount(), new MerchantAccountForm());
        return formPanel;
    }

}
