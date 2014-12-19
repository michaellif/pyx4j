/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 6, 2013
 * @author stanp
 */
package com.propertyvista.crm.client.ui.crud.administration.ils;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.settings.ILSEmailConfig;

public class ILSEmailConfigEditorFolder extends VistaBoxFolder<ILSEmailConfig> {

    public ILSEmailConfigEditorFolder() {
        super(ILSEmailConfig.class);
    }

    @Override
    protected CForm<ILSEmailConfig> createItemForm(IObject<?> member) {
        return new ILSEmailConfigEditor();
    }

    class ILSEmailConfigEditor extends CForm<ILSEmailConfig> {

        public ILSEmailConfigEditor() {
            super(ILSEmailConfig.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().frequency()).decorate().componentWidth(120);
            formPanel.append(Location.Left, proto().email()).decorate().componentWidth(200);
            formPanel.append(Location.Left, proto().maxDailyAds()).decorate().componentWidth(120);

            return formPanel;
        }
    }
}
