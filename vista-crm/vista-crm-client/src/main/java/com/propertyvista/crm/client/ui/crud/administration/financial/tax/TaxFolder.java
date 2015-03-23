/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.administration.financial.tax;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.selections.SelectTaxListService;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxFolder extends VistaBoxFolder<Tax> {

    private static final I18n i18n = I18n.get(TaxFolder.class);

    public TaxFolder(CrmEntityForm<?> parentForm) {
        super(Tax.class);
    }

    @Override
    protected CForm<Tax> createItemForm(IObject<?> member) {
        return new TaxInfoEditor();
    }

    @Override
    protected void addItem() {
        new TaxSelectorDialog().show();
    }

    private class TaxInfoEditor extends CForm<Tax> {

        public TaxInfoEditor() {
            super(Tax.class);
            setViewable(true);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().name()).decorate();
            formPanel.append(Location.Left, proto().authority()).decorate();

            formPanel.append(Location.Right, proto().rate()).decorate();
            formPanel.append(Location.Right, proto().compound()).decorate();

            return formPanel;
        }
    }

    private class TaxSelectorDialog extends EntitySelectorTableDialog<Tax> {

        public TaxSelectorDialog() {
            super(Tax.class, true, new HashSet<>(getValue()), i18n.tr("Select Tax"));
        }

        @Override
        public boolean onClickOk() {
            for (Tax selected : getSelectedItems()) {
                addItem(selected);
            }
            return true;
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new ColumnDescriptor.Builder(proto().authority()).filterAlwaysShown(true).build(),
                    new ColumnDescriptor.Builder(proto().name()).filterAlwaysShown(true).build(),
                    new ColumnDescriptor.Builder(proto().rate()).build(),
                    new ColumnDescriptor.Builder(proto().compound()).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        protected AbstractListCrudService<Tax> getSelectService() {
            return GWT.<AbstractListCrudService<Tax>> create(SelectTaxListService.class);
        }

    }
}