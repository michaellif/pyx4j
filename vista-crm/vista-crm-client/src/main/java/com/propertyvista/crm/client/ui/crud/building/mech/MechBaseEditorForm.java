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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.mech;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.editors.MaintenanceEditor;
import com.propertyvista.common.client.ui.components.editors.WarrantyEditor;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.vendor.LicensedWarrantedMaintained;

public abstract class MechBaseEditorForm<E extends LicensedWarrantedMaintained> extends CrmEntityForm<E> {

    private static final I18n i18n = I18n.get(MechBaseEditorForm.class);

    protected final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    protected MechBaseEditorForm(Class<E> entityClass) {
        this(entityClass, false);
    }

    protected MechBaseEditorForm(Class<E> entityClass, boolean viewMode) {
        super(entityClass, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createWarrantyTab(), i18n.tr("Warranty"));
        tabPanel.add(createMaintenantceTab(), i18n.tr("Maintenance"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    protected abstract Widget createGeneralTab();

    protected Widget createWarrantyTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().warranty(), new WarrantyEditor()));

        return new CrmScrollPanel(main);
    }

    protected Widget createMaintenantceTab() {
        FormFlexPanel main = new FormFlexPanel();

        main.setWidget(0, 0, inject(proto().maintenance(), new MaintenanceEditor()));

        return new CrmScrollPanel(main);
    }
}