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
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.c.CEmailLabel;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.OldAgeValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.ValidationUtils;

class TenantInLeaseFolder extends VistaTableFolder<TenantInLease> {

    private final CEntityEditor<? extends Lease> parent;

    private final IListerView<Tenant> tenantListerView;

    private final LeaseEditorView.Presenter presenter;

    public TenantInLeaseFolder(CEntityEditor<? extends Lease> parent) {
        this(parent, null, null); // view mode constructor
    }

    public TenantInLeaseFolder(CEntityEditor<? extends Lease> parent, IListerView<Tenant> tenantListerView, LeaseEditorView.Presenter presenter) {
        super(TenantInLease.class, parent.isEditable());
        this.parent = parent;
        this.tenantListerView = tenantListerView;
        this.presenter = presenter;
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
        new ShowPopUpBox<SelectTenantBox>(new SelectTenantBox(tenantListerView)) {
            @Override
            protected void onClose(SelectTenantBox box) {
                if (box.getSelectedTenant() != null) {
                    TenantInLease newTenantInLease = EntityFactory.create(TenantInLease.class);
                    newTenantInLease.lease().setPrimaryKey(parent.getValue().getPrimaryKey());
                    newTenantInLease.tenant().set(box.getSelectedTenant());
                    addItem(newTenantInLease);
                }
            }
        };
    }

    @Override
    protected void removeItem(CEntityFolderItem<TenantInLease> item) {
        presenter.removeTenat(item.getValue());
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

        private Widget relationship;

        private CComboBox<Role> role;

        public TenantInLeaseEditor() {
            super(TenantInLease.class, columns());
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?, ?> comp = null;
            if (proto().tenant() == column.getObject()) {
                comp = inject(column.getObject(), new CEntityLabel());
            } else if (proto().tenant().person().birthDate() == column.getObject()) {
                comp = inject(column.getObject(), new CLabel());
            } else if (proto().tenant().person().email() == column.getObject()) {
                comp = inject(column.getObject(), new CEmailLabel());
            } else {
                comp = super.createCell(column);

                if (proto().role() == column.getObject() && comp instanceof CComboBox) {
                    role = ((CComboBox) comp);
                } else if (proto().relationship() == column.getObject()) {
                    relationship = comp.asWidget();
                }
            }
            return comp;
        }

        @Override
        public void populate(TenantInLease value) {
            super.populate(value);

            applicant = (value.role().getValue() == Role.Applicant);
            if (applicant) {
                relationship.setVisible(false);
                if (role != null) {
                    role.setEditable(false);
                }
            } else if (role != null) {
                Collection<TenantInLease.Role> roles = EnumSet.allOf(TenantInLease.Role.class);
                roles.remove(TenantInLease.Role.Applicant);
                role.setOptions(roles);
            }

            if (!applicant && !value.tenant().person().birthDate().isNull()) {
                if (ValidationUtils.isOlderThen18(value.tenant().person().birthDate().getValue())) {
                    enableStatusAndOwnership();
                } else {
                    setMandatoryDependant();
                }
            }
        }

        @Override
        public void addValidations() {

            get(proto().tenant().person().birthDate()).addValueValidator(new OldAgeValidator());
            get(proto().tenant().person().birthDate()).addValueValidator(new BirthdayDateValidator());
            get(proto().tenant().person().birthDate()).addValueValidator(new EditableValueValidator<Date>() {
                @Override
                public boolean isValid(CComponent<Date, ?> component, Date value) {
                    TenantInLease.Role status = getValue().role().getValue();
                    if ((status == TenantInLease.Role.Applicant) || (status == TenantInLease.Role.CoApplicant)) {
                        // TODO I Believe that this is not correct, this logic has to be applied to Dependents as well, as per VISTA-273
                        return ValidationUtils.isOlderThen18(value);
                    } else {
                        return true;
                    }
                }

                @Override
                public String getValidationMessage(CComponent<Date, ?> component, Date value) {
                    return i18n.tr("Applicant and Co-applicant must be at least 18 years old");
                }
            });

            if (!applicant) { // all this stuff isn't for primary applicant:
                get(proto().tenant().person().birthDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                        TenantInLease.Role status = getValue().role().getValue();
                        if ((status == null) || (status == TenantInLease.Role.Dependent)) {
                            if (ValidationUtils.isOlderThen18(event.getValue())) {
                                boolean currentEditableState = get(proto().role()).isEditable();
                                enableStatusAndOwnership();
                                if (!currentEditableState) {
                                    get(proto().role()).setValue(null);
                                }
                            } else {
                                setMandatoryDependant();
                            }
                        }
                    }
                });

                get(proto().role()).addValueChangeHandler(new RevalidationTrigger<TenantInLease.Role>(get(proto().tenant().person().birthDate())));
            }
        }

        private void setMandatoryDependant() {
            get(proto().role()).setValue(TenantInLease.Role.Dependent);
            get(proto().role()).setEditable(false);
        }

        private void enableStatusAndOwnership() {
            get(proto().role()).setEditable(true);
        }
    }

    private class SelectTenantBox extends OkCancelBox {

        private final IListerView<Tenant> tenantListerView;

        private Tenant selectedTenant;

        public SelectTenantBox(IListerView<Tenant> tenantListerView) {
            super(i18n.tr("Select Tenant"));
            this.tenantListerView = tenantListerView;
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(false);
            tenantListerView.getLister().addItemSelectionHandler(new ItemSelectionHandler<Tenant>() {
                @Override
                public void onSelect(Tenant selectedItem) {
                    selectedTenant = selectedItem;
                    okButton.setEnabled(true);
                }
            });

            VerticalPanel vPanel = new VerticalPanel();
            vPanel.add(tenantListerView.asWidget());
            vPanel.setWidth("100%");
            return vPanel;
        }

        @Override
        protected void setSize() {
            setSize("700px", "400px");
        }

        @Override
        protected void onCancel() {
            selectedTenant = null;
        }

        protected Tenant getSelectedTenant() {
            return selectedTenant;
        }
    }
}