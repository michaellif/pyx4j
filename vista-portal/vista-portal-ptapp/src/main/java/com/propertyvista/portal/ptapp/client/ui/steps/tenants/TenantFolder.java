/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.tenants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.OldAgeValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.dto.TenantInLeaseDTO;

public class TenantFolder extends VistaTableFolder<TenantInLeaseDTO> {

    public TenantFolder(boolean modifyable) {
        super(TenantInLeaseDTO.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {

// TODO uncomment when isEditable() be fixed!          
//        if (isEditable() && member instanceof TenantInApplicationDTO) {
//            return new TenantEditor();
//        }
        return super.create(member);
    }

    @Override
    protected CEntityFolderItem<TenantInLeaseDTO> createItem(boolean first) {
        CEntityFolderItem<TenantInLeaseDTO> item = super.createItem(first);
        item.setRemovable(!first);
        item.setMovable(!first);
        return item;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().person().name().firstName(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().person().name().middleName(), "5em"));
        columns.add(new EntityFolderColumnDescriptor(proto().person().name().lastName(), "12em"));
        columns.add(new EntityFolderColumnDescriptor(proto().person().birthDate(), "8.2em"));
        columns.add(new EntityFolderColumnDescriptor(proto().person().email(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().status(), "8.5em"));
        columns.add(new EntityFolderColumnDescriptor(proto().takeOwnership(), "5em"));
        return columns;
    }

    private class TenantEditor extends CEntityFolderRowEditor<TenantInLeaseDTO> {

        private final boolean first = false;

        public TenantEditor() {
            super(TenantInLeaseDTO.class, columns());
        }

        @SuppressWarnings("rawtypes")
        @Override
        public IsWidget createContent() {
            if (first) {
                HorizontalPanel main = new HorizontalPanel();
                for (EntityFolderColumnDescriptor column : columns) {
                    CComponent<?, ?> component = createCell(column);
                    // Don't show relation and takeOwnership 
                    if (column.getObject() == proto().relationship() || column.getObject() == proto().takeOwnership()) {
                        component.setVisible(false);
                    } else if (column.getObject() == proto().person().email()) {
                        ((CComponent) component).setEditable(false);
                    }
                    main.add(createCellDecorator(column, component, column.getWidth()));
                }
                main.setWidth("100%");
                return main;
            } else {
                return super.createContent();
            }
        }

        @Override
        public void addValidations() {

            get(proto().person().birthDate()).addValueValidator(new OldAgeValidator());
            get(proto().person().birthDate()).addValueValidator(new BirthdayDateValidator());
            get(proto().person().birthDate()).addValueValidator(new EditableValueValidator<Date>() {
                @Override
                public boolean isValid(CComponent<Date, ?> component, Date value) {
                    TenantInLease.Status status = getValue().status().getValue();
                    if ((status == TenantInLease.Status.Applicant) || (status == TenantInLease.Status.CoApplicant)) {
                        // TODO I Believe that this is not correct, this logic has to be applied to Dependents as well, as per VISTA-273
                        return ValidationUtils.isOlderThen18(value);
                    } else {
                        return true;
                    }
                }

                @Override
                public String getValidationMessage(CComponent<Date, ?> component, Date value) {
                    return TenantsViewForm.i18n.tr("Applicant and Co-Applicant must be at least 18 years old");
                }
            });

            if (!first) { // all this stuff isn't for primary applicant:  
                get(proto().person().birthDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
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

                get(proto().status()).addValueChangeHandler(new RevalidationTrigger<TenantInLease.Status>(get(proto().person().birthDate())));
            }
        }

        @Override
        public void populate(TenantInLeaseDTO value) {
            super.populate(value);
            boolean applicant = TenantInLease.Status.Applicant.equals(value.status().getValue());
            if (!applicant && !value.person().birthDate().isNull()) {
                if (ValidationUtils.isOlderThen18(value.person().birthDate().getValue())) {
                    enableStatusAndOwnership();
                } else {
                    setMandatoryDependant();
                }
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?, ?> comp = null;
            if (first && proto().status() == column.getObject()) {
                CTextField textComp = new CTextField();
                textComp.setEditable(false);
                textComp.setValue(TenantInLease.Status.Applicant.name());
                comp = textComp;
            } else {
                comp = super.createCell(column);
                if (proto().status() == column.getObject()) {
                    Collection<TenantInLease.Status> status = EnumSet.allOf(TenantInLease.Status.class);
                    status.remove(TenantInLease.Status.Applicant);
                    ((CComboBox) comp).setOptions(status);
                }
            }
            return comp;
        }

        private void setMandatoryDependant() {
            get(proto().status()).setValue(TenantInLease.Status.Dependant);
            get(proto().status()).setEditable(false);

            get(proto().takeOwnership()).setValue(true);
            get(proto().takeOwnership()).setEnabled(false);
        }

        private void enableStatusAndOwnership() {
            get(proto().status()).setEditable(true);
            get(proto().takeOwnership()).setEnabled(true);
        }
    }
}