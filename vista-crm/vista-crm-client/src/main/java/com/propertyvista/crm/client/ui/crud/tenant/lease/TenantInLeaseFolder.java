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
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DataTable.CheckSelectionHandler;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.site.client.ui.crud.lister.ListerDataSource;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.tenant.SelectTenantLister;
import com.propertyvista.crm.rpc.services.SelectTenantCrudService;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Relationship;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.ValidationUtils;

class TenantInLeaseFolder extends VistaTableFolder<TenantInLease> {

    private final CEntityEditor<? extends Lease> parent;

    private final LeaseEditorView view;

    public TenantInLeaseFolder(CEntityEditor<? extends Lease> parent) {
        this(parent, null); // view mode constructor
    }

    public TenantInLeaseFolder(CEntityEditor<? extends Lease> parent, LeaseEditorView view) {
        super(TenantInLease.class, parent.isEditable());
        this.parent = parent;
        this.view = view;
        setOrderable(false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().tenant(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().birthDate(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().email(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().role(), "9em"));
        return columns;
    }

    @Override
    protected void addItem() {
        new SelectTenantBox(getValue()) {
            @Override
            public boolean onClickOk() {
                for (Tenant tenant : getSelectedItem()) {
                    TenantInLease newTenantInLease = EntityFactory.create(TenantInLease.class);

                    newTenantInLease.lease().setPrimaryKey(parent.getValue().getPrimaryKey());
                    newTenantInLease.tenant().set(tenant);
                    if (!isApplicantPresent()) {
                        newTenantInLease.role().setValue(Role.Applicant);
                        newTenantInLease.relationship().setValue(Relationship.Other); // just not leave it empty - it's mandatory field!
                    }
                    boolean isNewAlreadySelected = false;
                    for (TenantInLease alreadySelected : getValue()) {
                        if (alreadySelected.tenant().equals(tenant)) {
                            isNewAlreadySelected = true;
                            break;
                        }
                    }
                    if (!isNewAlreadySelected) {
                        addItem(newTenantInLease);
                    }
                }

                return true;
            }
        }.show();
    }

    private boolean isApplicantPresent() {
        for (TenantInLease til : getValue()) {
            if (Role.Applicant == til.role().getValue()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void removeItem(CEntityFolderItem<TenantInLease> item) {
        ((LeaseEditorView.Presenter) view.getPresenter()).removeTenat(item.getValue());
        super.removeItem(item);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof TenantInLease) {
            return new TenantInLeaseEditor();
        }
        return super.create(member);
    }

    private class TenantInLeaseEditor extends CEntityFolderRowEditor<TenantInLease> {

        private boolean applicant;

        public TenantInLeaseEditor() {
            super(TenantInLease.class, columns());
            setViewable(true);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?, ?> comp = super.createCell(column);
            if (proto().role() == column.getObject() || proto().relationship() == column.getObject()) {
                comp.inheritContainerAccessRules(false);
                comp.setEditable(true);
            }
            return comp;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPopulate() {
            super.onPopulate();

            applicant = (getValue().role().getValue() == Role.Applicant);
            if (applicant) {
                get(proto().role()).setViewable(true);
                get(proto().relationship()).setVisible(false);
            } else if (get(proto().role()) instanceof CComboBox) {
                Collection<TenantInLease.Role> roles = EnumSet.allOf(TenantInLease.Role.class);
                roles.remove(TenantInLease.Role.Applicant);
                ((CComboBox<Role>) get(proto().role())).setOptions(roles);
            }

            if (!applicant && !getValue().tenant().person().birthDate().isNull()) {
                if (!ValidationUtils.isOlderThen18(getValue().tenant().person().birthDate().getValue())) {
                    setMandatoryDependant();
                }
            }
        }

        private void setMandatoryDependant() {
            get(proto().role()).setValue(TenantInLease.Role.Dependent);
            get(proto().role()).setEditable(false);
        }
    }

    private abstract class SelectTenantBox extends OkCancelDialog {

        private final SelectTenantLister tenantLister;

        @SuppressWarnings("unchecked")
        public SelectTenantBox(IList<TenantInLease> currentTenants) {
            super(i18n.tr("Select Tenant"));

            tenantLister = new SelectTenantLister();

            ListerDataSource<Tenant> listerDataSource = new ListerDataSource<Tenant>(Tenant.class,
                    (AbstractListService<Tenant>) GWT.create(SelectTenantCrudService.class));

            filterOutAlreadySelectedTenants(listerDataSource, currentTenants);

            tenantLister.setDataSource(listerDataSource);
            tenantLister.obtain(0);

            setBody(createBody());
            setSize("700px", "400px");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(!tenantLister.getCheckedItems().isEmpty());
            tenantLister.getDataTablePanel().getDataTable().addCheckSelectionHandler(new CheckSelectionHandler() {

                @Override
                public void onCheck(boolean isAnyChecked) {
                    getOkButton().setEnabled(isAnyChecked);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(tenantLister.asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        protected List<Tenant> getSelectedItem() {
            return tenantLister.getCheckedItems();
        }

        private void filterOutAlreadySelectedTenants(ListerDataSource<Tenant> listerDataSource, IList<TenantInLease> currentTenants) {

            List<DataTableFilterData> restrictAlreadySelectedTenants = new ArrayList<DataTableFilterData>(getValue().size());
            Tenant tenantProto = EntityFactory.getEntityPrototype(Tenant.class);
            for (TenantInLease alreadySelected : currentTenants) {
                restrictAlreadySelectedTenants.add(new DataTableFilterData(tenantProto.id().getPath(), Operators.isNot, alreadySelected.tenant().id()
                        .getValue()));
            }

            listerDataSource.setPreDefinedFilters(restrictAlreadySelectedTenants);
        }
    }
}