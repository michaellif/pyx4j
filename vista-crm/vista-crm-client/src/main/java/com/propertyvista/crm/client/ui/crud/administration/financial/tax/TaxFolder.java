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
import java.util.HashSet;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.selections.SelectTaxListService;
import com.propertyvista.domain.financial.tax.Tax;

public class TaxFolder extends VistaTableFolder<Tax> {

    private static final I18n i18n = I18n.get(TaxFolder.class);

    private final CrmEntityForm<?> parentForm;

    public TaxFolder(CrmEntityForm<?> parentForm) {
        super(Tax.class, parentForm.isEditable());
        this.parentForm = parentForm;
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        List<FolderColumnDescriptor> columns;
        columns = new ArrayList<FolderColumnDescriptor>();
        columns.add(new FolderColumnDescriptor(proto().authority(), "10em"));
        columns.add(new FolderColumnDescriptor(proto().name(), "20em"));
        columns.add(new FolderColumnDescriptor(proto().rate(), "7em"));
        columns.add(new FolderColumnDescriptor(proto().compound(), "7em"));
        return columns;
    }

    @Override
    protected CForm<Tax> createItemForm(IObject<?> member) {
        return new ChargeCodeTaxEditor();
    }

    @Override
    protected void addItem() {
        new TaxSelectorDialog(parentForm.getParentView()).show();
    }

    private class ChargeCodeTaxEditor extends CFolderRowEditor<Tax> {

        public ChargeCodeTaxEditor() {
            super(Tax.class, columns());
            setViewable(true);
        }

    }

    private class TaxSelectorDialog extends EntitySelectorTableVisorController<Tax> {

        public TaxSelectorDialog(IPane parentView) {
            super(parentView, Tax.class, true, new HashSet<>(getValue()), i18n.tr("Select Tax"));
        }

        @Override
        public void onClickOk() {
            for (Tax selected : getSelectedItems()) {
                addItem(selected);
            }
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
        protected AbstractListCrudService<Tax> getSelectService() {
            return GWT.<AbstractListCrudService<Tax>> create(SelectTaxListService.class);
        }

    }
}