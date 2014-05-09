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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.ils;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.vista2pmc.ILSConfigDTO;

public class ILSConfigForm extends CrmEntityForm<ILSConfigDTO> {

    private final static I18n i18n = I18n.get(ILSConfigForm.class);

    public ILSConfigForm(IForm<ILSConfigDTO> view) {
        super(ILSConfigDTO.class, view);

        Tab tab = addTab(createProvidersTab(), i18n.tr("ILS Settings"));
        selectTab(tab);
    }

    private IsWidget createProvidersTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.h1(proto().vendors().getMeta().getCaption());
        formPanel.append(Location.Dual, inject(proto().vendors(), new ILSVendorConfigEditorFolder()));

        formPanel.h1(proto().emailFeeds().getMeta().getCaption());
        formPanel.append(Location.Dual, inject(proto().emailFeeds(), new ILSEmailConfigEditorFolder()));

        return formPanel;
    }
}
