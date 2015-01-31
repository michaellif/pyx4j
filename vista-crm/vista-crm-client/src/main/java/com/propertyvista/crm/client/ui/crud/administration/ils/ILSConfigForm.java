/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2013
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.administration.ils;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.vista2pmc.ILSConfigDTO;

public class ILSConfigForm extends CrmEntityForm<ILSConfigDTO> {

    private final static I18n i18n = I18n.get(ILSConfigForm.class);

    public ILSConfigForm(IPrimeFormView<ILSConfigDTO, ?> view) {
        super(ILSConfigDTO.class, view);

        selectTab(addTab(createProvidersTab(), i18n.tr("ILS Settings")));
        setTabBarVisible(false);
    }

    private IsWidget createProvidersTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(proto().vendors().getMeta().getCaption());
        formPanel.append(Location.Dual, inject(proto().vendors(), new ILSVendorConfigEditorFolder()));

        formPanel.h1(proto().emailFeeds().getMeta().getCaption());
        formPanel.append(Location.Dual, inject(proto().emailFeeds(), new ILSEmailConfigEditorFolder()));

        return formPanel;
    }
}
