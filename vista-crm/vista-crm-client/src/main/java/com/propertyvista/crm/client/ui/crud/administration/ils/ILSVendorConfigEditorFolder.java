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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.domain.settings.ILSVendorConfig;

public class ILSVendorConfigEditorFolder extends VistaBoxFolder<ILSVendorConfig> {

    private final static I18n i18n = I18n.get(ILSVendorConfigEditorFolder.class);

    public ILSVendorConfigEditorFolder() {
        super(ILSVendorConfig.class);
    }

    @Override
    protected void addItem() {
        // get unused providers
        EnumSet<ILSVendor> values = EnumSet.allOf(ILSVendor.class);
        List<ILSVendor> usedProviders = new ArrayList<ILSVendor>();
        for (ILSVendorConfig item : getValue()) {
            usedProviders.add(item.vendor().getValue());
        }
        values.removeAll(usedProviders);
        // show selection dialog
        new SelectEnumDialog<ILSVendor>(i18n.tr("Select ILS Vendor"), values) {
            @Override
            public boolean onClickOk() {
                ILSVendorConfig item = EntityFactory.create(ILSVendorConfig.class);
                item.vendor().setValue(getSelectedType());
                addItem(item);
                return true;
            }
        }.show();
    }

    @Override
    protected CForm<ILSVendorConfig> createItemForm(IObject<?> member) {
        return new ILSVendorConfigEditor();
    }

    class ILSVendorConfigEditor extends CForm<ILSVendorConfig> {

        public ILSVendorConfigEditor() {
            super(ILSVendorConfig.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().vendor(), new CEnumLabel()).decorate();
            formPanel.append(Location.Left, proto().maxDailyAds()).decorate().componentWidth(60);

            return formPanel;
        }
    }
}
