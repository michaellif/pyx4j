/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.administration.financial.tax;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxForm extends CrmEntityForm<Tax> {

    private static final I18n i18n = I18n.get(TaxForm.class);

    public TaxForm(IPrimeFormView<Tax, ?> view) {
        super(Tax.class, view);

        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Left, proto().authority()).decorate();

        formPanel.append(Location.Right, proto().rate()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().compound()).decorate().componentWidth(80);

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));
    }
}