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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.administration.financial.tax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.rpc.services.selections.SelectTaxListService;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxFolder extends VistaTableFolder<Tax> {

    private static final I18n i18n = I18n.get(TaxFolder.class);

    public TaxFolder(boolean modifyable) {
        super(Tax.class, modifyable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        List<EntityFolderColumnDescriptor> columns;
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().authority(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().rate(), "7em"));
        columns.add(new EntityFolderColumnDescriptor(proto().compound(), "7em"));
        return columns;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof Tax) {
            return new ChargeCodeTaxEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new TaxSelectorDialog().show();
    }

    private class ChargeCodeTaxEditor extends CEntityFolderRowEditor<Tax> {

        public ChargeCodeTaxEditor() {
            super(Tax.class, columns());
            setViewable(true);
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            return super.createCell(column);
        }
    }

    private class TaxSelectorDialog extends EntitySelectorTableDialog<Tax> {

        public TaxSelectorDialog() {
            super(Tax.class, true, getValue(), i18n.tr("Select Tax"));
            setDialogPixelWidth(700);
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (Tax selected : getSelectedItems()) {
                    addItem(selected);
                }
            }
            return true;
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().authority(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().name(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().rate(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().compound(), true).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().name(), false));
        }

        @Override
        protected AbstractListService<Tax> getSelectService() {
            return GWT.<AbstractListService<Tax>> create(SelectTaxListService.class);
        }

    }
}