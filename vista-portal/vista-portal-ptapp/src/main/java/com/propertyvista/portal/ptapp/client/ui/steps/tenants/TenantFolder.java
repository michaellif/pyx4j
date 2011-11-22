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
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.OldAgeValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.dto.TenantInLeaseDTO;

public class TenantFolder extends VistaTableFolder<TenantInLeaseDTO> {

    public TenantFolder(boolean modifyable) {
        super(TenantInLeaseDTO.class, modifyable);
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof TenantInLeaseDTO) {
            return new TenantEditor();
        }
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
        columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().name().firstName(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().name().lastName(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().birthDate(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().tenant().person().email(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().role(), "8.5em"));
        columns.add(new EntityFolderColumnDescriptor(proto().takeOwnership(), "5em"));
        return columns;
    }

    private class TenantEditor extends CEntityFolderRowEditor<TenantInLeaseDTO> {

        private boolean applicant;

        private Widget relationship, takeOwnership;

        private CComponent email;

        private CComboBox<Role> role;

        public TenantEditor() {
            super(TenantInLeaseDTO.class, columns());
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?, ?> comp = super.createCell(column);

            if (proto().role() == column.getObject() && comp instanceof CComboBox) {
                role = ((CComboBox) comp);
            } else if (proto().tenant().person().email() == column.getObject()) {
                email = comp;
            } else if (proto().relationship() == column.getObject()) {
                relationship = comp.asWidget();
            } else if (proto().takeOwnership() == column.getObject()) {
                takeOwnership = comp.asWidget();
            }

            return comp;
        }

        @Override
        public void populate(TenantInLeaseDTO value) {
            super.populate(value);

            applicant = (value.role().getValue() == Role.Applicant);
            if (applicant) {
                relationship.setVisible(false);
                takeOwnership.setVisible(false);

                email.setEditable(false);

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
                    return TenantsViewForm.i18n.tr("Applicant and Co-Applicant must be at least 18 years old");
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

            get(proto().takeOwnership()).setValue(true);
            get(proto().takeOwnership()).setEnabled(false);
        }

        private void enableStatusAndOwnership() {
            get(proto().role()).setEditable(true);
            get(proto().takeOwnership()).setEnabled(true);
        }
    }
}