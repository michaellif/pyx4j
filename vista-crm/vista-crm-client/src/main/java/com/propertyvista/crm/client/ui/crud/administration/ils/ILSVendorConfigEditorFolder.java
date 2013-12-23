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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.ils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
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
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof ILSVendorConfig) {
            return new ILSVendorConfigEditor();
        } else {
            return super.create(member);
        }
    }

    class ILSVendorConfigEditor extends CEntityForm<ILSVendorConfig> {

        public ILSVendorConfigEditor() {
            super(ILSVendorConfig.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
            int row = -1;

            content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().vendor(), new CEnumLabel()), true).build());
            content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().maxDailyAds()), 10, true).build());

            return content;
        }
    }
}
