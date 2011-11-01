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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.common.client.ui.components.editors.CEmailLabel;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.OldAgeValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
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
        columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().birthDate(), "8.2em"));
        columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().email(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().status(), "8.5em"));
//                columns.add(new EntityFolderColumnDescriptor(proto().takeOwnership(), "5em"));
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
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof TenantInLease) {
            return new TenantInLeaseEditor();
        }
        return super.create(member);
    }

    private class TenantInLeaseEditor extends CEntityFolderRowEditor<TenantInLease> {

        // TODO - get somehow this info:
        private final boolean first = false;

        public TenantInLeaseEditor() {
            super(TenantInLease.class, columns());
        }

        @Override
        public IsWidget createContent() {
            if (first) {
                HorizontalPanel main = new HorizontalPanel();
                for (EntityFolderColumnDescriptor column : columns) {
                    CComponent<?> component = createCell(column);
                    // Don't show relation and takeOwnership 
                    if (column.getObject() == proto().relationship() || column.getObject() == proto().takeOwnership()) {
                        component.setVisible(false);
                    }
                    main.add(createCellDecorator(column, component, column.getWidth()));
                }
                main.setWidth("100%");
                return main;
            } else {
                return super.createContent();
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?> comp = null;
            if (first && proto().status() == column.getObject()) {
                CTextField textComp = new CTextField();
                textComp.setEditable(false);
                textComp.setValue(TenantInLease.Status.Applicant.name());
                comp = textComp;
            } else if (proto().tenant() == column.getObject()) {
                comp = inject(column.getObject(), new CEntityLabel());
            } else if (proto().tenant().person().birthDate() == column.getObject()) {
                comp = inject(column.getObject(), new CLabel());
            } else if (proto().tenant().person().email() == column.getObject()) {
                comp = inject(column.getObject(), new CEmailLabel());
            } else {
                comp = super.createCell(column);

                if (proto().status() == column.getObject() && comp instanceof CComboBox) {
                    Collection<TenantInLease.Status> status = EnumSet.allOf(TenantInLease.Status.class);
                    status.remove(TenantInLease.Status.Applicant);
                    ((CComboBox) comp).setOptions(status);
                }
            }
            return comp;
        }

        @Override
        public void populate(TenantInLease value) {
            super.populate(value);

            if (!first && !value.tenant().person().birthDate().isNull()) {
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
                public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                    TenantInLease.Status status = getValue().status().getValue();
                    if ((status == TenantInLease.Status.Applicant) || (status == TenantInLease.Status.CoApplicant)) {
                        // TODO I Believe that this is not correct, this logic has to be applied to Dependents as well, as per VISTA-273
                        return ValidationUtils.isOlderThen18(value);
                    } else {
                        return true;
                    }
                }

                @Override
                public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                    return i18n.tr("Applicant and Co-applicant must be at least 18 years old");
                }
            });

            if (!first) { // all this stuff isn't for primary applicant:  
                get(proto().tenant().person().birthDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {

                    @Override
                    public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                        TenantInLease.Status status = getValue().status().getValue();
                        if ((status == null) || (status == TenantInLease.Status.Dependant)) {
                            if (ValidationUtils.isOlderThen18(event.getValue())) {
                                boolean currentEditableState = get(proto().status()).isEditable();
                                enableStatusAndOwnership();
                                if (!currentEditableState) {
                                    get(proto().status()).setValue(null);
                                }
                            } else {
                                setMandatoryDependant();
                            }
                        }
                    }
                });

                get(proto().status()).addValueChangeHandler(new RevalidationTrigger<TenantInLease.Status>(get(proto().tenant().person().birthDate())));
            }
        }

        private void setMandatoryDependant() {
            get(proto().status()).setValue(TenantInLease.Status.Dependant);
            get(proto().status()).setEditable(false);

//                get(proto().takeOwnership()).setValue(true);
//                get(proto().takeOwnership()).setEnabled(false);
        }

        private void enableStatusAndOwnership() {
            get(proto().status()).setEditable(true);
//                get(proto().takeOwnership()).setEnabled(true);
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