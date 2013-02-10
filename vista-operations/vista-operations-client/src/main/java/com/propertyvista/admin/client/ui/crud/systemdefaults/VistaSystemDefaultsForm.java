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
package com.propertyvista.admin.client.ui.crud.systemdefaults;

import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.admin.client.ui.components.EquifaxFeeQuoteForm;
import com.propertyvista.admin.client.ui.components.MerchantAccountForm;
import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.rpc.VistaSystemDefaultsDTO;

public class VistaSystemDefaultsForm extends AdminEntityForm<VistaSystemDefaultsDTO> {

    public static I18n i18n = I18n.get(VistaSystemDefaultsForm.class);

    public VistaSystemDefaultsForm(IFormView<VistaSystemDefaultsDTO> view) {
        super(VistaSystemDefaultsDTO.class, view);
        createTabs();
    }

    private void createTabs() {
        selectTab(addTab(makeEquifaxSettingsTab()));
        addTab(makeCaledonSettingsTab());
        addTab(makeTenantSureSettingsTab());
    }

    private FormFlexPanel makeCaledonSettingsTab() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Caledon"));
        int row = -1;
        panel.setH1(++row, 0, 1, i18n.tr("Default Fees"));
        // TODO inject fees form
        panel.setWidget(++row, 0, new HTML(i18n.tr("TODO: setupFees")));
        return panel;
    }

    private FormFlexPanel makeEquifaxSettingsTab() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("Equifax"));
        int row = -1;
        panel.setH1(++row, 0, 1, i18n.tr("Default Fees"));
        panel.setWidget(++row, 0, inject(proto().equifaxFees(), new EquifaxFeeQuoteForm(true)));

        panel.setH1(++row, 0, 1, i18n.tr("Merchant Account"));
        panel.setWidget(++row, 0, inject(proto().vistaMerchantAccount(), new MerchantAccountForm()));
        return panel;
    }

    private FormFlexPanel makeTenantSureSettingsTab() {
        FormFlexPanel panel = new FormFlexPanel(i18n.tr("TenantSure"));
        int row = -1;
        panel.setH1(++row, 0, 1, i18n.tr("Merchant Account"));
        panel.setWidget(++row, 0, inject(proto().tenantSureMerchantAccount(), new MerchantAccountForm()));
        return panel;
    }

}
