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
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.crm.rpc.services.selections.SelectTenantListService;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.ValidationUtils;

class TenantInLeaseFolder extends VistaTableFolder<TenantInLease> {

    private static final I18n i18n = I18n.get(TenantInLeaseFolder.class);

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
        new TenantSelectorDialog().show();
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
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof Tenant && !parent.isEditable()) {
                return new CEntityCrudHyperlink<Tenant>(MainActivityMapper.getCrudAppPlace(Tenant.class));
            }
            return super.create(member);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?, ?> comp = super.createCell(column);
            if (proto().role() == column.getObject() || proto().relationship() == column.getObject()) {
                if (parent.isEditable()) { // allow editing of these fields...
                    comp.inheritViewable(false);
                }
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

    private static List<Tenant> extractTenantFromTenantInLeaseList(List<TenantInLease> list) {
        List<Tenant> tenants = new ArrayList<Tenant>(list.size());
        for (TenantInLease wrapper : list) {
            tenants.add(wrapper.tenant());
        }
        return tenants;
    }

    private class TenantSelectorDialog extends EntitySelectorDialog<Tenant> {

        public TenantSelectorDialog() {
            super(Tenant.class, true, extractTenantFromTenantInLeaseList(getValue()), i18n.tr("Select Tenant"));
            setWidth("700px");
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (Tenant tenant : getSelectedItems()) {
                    TenantInLease newTenantInLease = EntityFactory.create(TenantInLease.class);
                    newTenantInLease.lease().setPrimaryKey(parent.getValue().version().getPrimaryKey());
                    newTenantInLease.tenant().set(tenant);
                    if (!isApplicantPresent()) {
                        newTenantInLease.role().setValue(Role.Applicant);
                        newTenantInLease.relationship().setValue(PersonRelationship.Other); // just not leave it empty - it's mandatory field!
                    }
                    addItem(newTenantInLease);
                }
                return true;
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().type()).build(),
                    new MemberColumnDescriptor.Builder(proto().person().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().person().birthDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().person().email()).build(),
                    new MemberColumnDescriptor.Builder(proto().person().homePhone()).build()
            );//@formatter:on
        }

        @Override
        protected AbstractListService<Tenant> getSelectService() {
            return GWT.<AbstractListService<Tenant>> create(SelectTenantListService.class);
        }

    }
}