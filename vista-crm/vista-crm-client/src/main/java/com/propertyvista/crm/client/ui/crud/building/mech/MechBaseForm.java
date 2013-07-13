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

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.MaintenanceEditor;
import com.propertyvista.common.client.ui.components.editors.WarrantyEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.vendor.LicensedWarrantedMaintained;

public abstract class MechBaseForm<E extends LicensedWarrantedMaintained> extends CrmEntityForm<E> {

    private static final I18n i18n = I18n.get(MechBaseForm.class);

    protected MechBaseForm(Class<E> entityClass, IForm<E> view) {
        super(entityClass, view);

        Tab tab = addTab(createGeneralTab());
        selectTab(tab);

        addTab(createWarrantyTab(), i18n.tr("Warranty"));
        addTab(createMaintenantceTab(), i18n.tr("Maintenance"));

    }

    protected abstract TwoColumnFlexFormPanel createGeneralTab();

    protected Widget createWarrantyTab() {

        return inject(proto().warranty(), new WarrantyEditor()).asWidget();
    }

    protected Widget createMaintenantceTab() {

        return inject(proto().maintenance(), new MaintenanceEditor()).asWidget();
    }
}