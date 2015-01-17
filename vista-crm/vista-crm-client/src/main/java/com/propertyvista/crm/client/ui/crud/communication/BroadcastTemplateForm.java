/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 16, 2015
 * @author michaellif
 */
package com.propertyvista.crm.client.ui.crud.communication;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.communication.BroadcastTemplate;

public class BroadcastTemplateForm extends CrmEntityForm<BroadcastTemplate> {

    private static final I18n i18n = I18n.get(BroadcastTemplateForm.class);

    public BroadcastTemplateForm(IPrimeFormView<BroadcastTemplate, ?> view) {
        super(BroadcastTemplate.class, view);

        setTabBarVisible(false);
        selectTab(addTab(createInfoTab(), i18n.tr("Broadcast Template")));

    }

    private IsWidget createInfoTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().category()).decorate();
        return formPanel;
    }

}
