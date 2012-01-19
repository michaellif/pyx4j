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
import com.google.gwt.user.client.Command;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderItem;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.OldAgeValidator;
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

        private boolean takeOwnershipSetManually = false;

        public TenantEditor() {
            super(TenantInLeaseDTO.class, columns());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPopulate() {
            super.onPopulate();

            applicant = (getValue().role().getValue() == Role.Applicant);
            if (applicant) {
                get(proto().role()).setViewable(true);
                get(proto().relationship()).setVisible(false);
                get(proto().takeOwnership()).setVisible(false);
                get(proto().tenant().person().email()).setViewable(true);

                // correct folder item:
                if (getParent() instanceof CEntityFolderItem) {
                    CEntityFolderItem<TenantInLeaseDTO> item = (CEntityFolderItem<TenantInLeaseDTO>) getParent();
                    item.setRemovable(false);
                    item.setMovable(false);
                }
            } else if (get(proto().role()) instanceof CComboBox) {
                Collection<TenantInLease.Role> roles = EnumSet.allOf(TenantInLease.Role.class);
                roles.remove(TenantInLease.Role.Applicant);
                ((CComboBox<Role>) get(proto().role())).setOptions(roles);
            }

            if (!applicant && !getValue().tenant().person().birthDate().isNull()) {
                if (ValidationUtils.isOlderThen18(getValue().tenant().person().birthDate().getValue())) {
                    enableRoleAndOwnership();
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
                public ValidationFailure isValid(CComponent<Date, ?> component, Date value) {
                    if (getValue() == null || getValue().isEmpty()) {
                        return null;
                    }

                    if (getValue().role().getValue() == TenantInLease.Role.Applicant) {
                        return ValidationUtils.isOlderThen18(value) ? null : new ValidationFailure(i18n.tr("Applicant must be at least 18 years old"));
                    } else {
                        return null;
                    }
                }
            });

            { // all this stuff isn't for primary applicant:  
                get(proto().tenant().person().birthDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                        if (applicant) {
                            return; // all this stuff isn't for primary applicant!..
                        }

                        if (getValue() == null || getValue().isEmpty()) {
                            return;
                        }

                        if (ValidationUtils.isOlderThen18(event.getValue())) {
                            enableRoleAndOwnership();
                            get(proto().role()).setValue(TenantInLease.Role.CoApplicant, false);
                            if (!takeOwnershipSetManually) {
                                get(proto().takeOwnership()).setValue(false, false);
                            }
                        } else {
                            setMandatoryDependant();
                        }
                    }
                });

                get(proto().role()).addValueChangeHandler(new RevalidationTrigger<TenantInLease.Role>(get(proto().tenant().person().birthDate())));
                get(proto().role()).addValueChangeHandler(new ValueChangeHandler<TenantInLease.Role>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Role> event) {
                        if (getValue() == null || getValue().isEmpty()) {
                            return;
                        }

                        if (Role.Dependent == event.getValue() && !ValidationUtils.isOlderThen18(get(proto().tenant().person().birthDate()).getValue())) {
                            setMandatoryDependant();
                        }
                    }
                });

                get(proto().takeOwnership()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<Boolean> event) {
                        takeOwnershipSetManually = false;
                        if (event.getValue()) {
                            MessageDialog.confirm(i18n.tr("Confirm"),
                                    i18n.tr("By Checking This Box I Agree That The Main Applicant Will Have Full Access To My Account"), new Command() {
                                        @Override
                                        public void execute() {
                                            takeOwnershipSetManually = true;
                                        }
                                    }, new Command() {
                                        @Override
                                        public void execute() {
                                            get(proto().takeOwnership()).setValue(false);
                                        }
                                    });
                        }
                    }
                });
            }
        }

        private void setMandatoryDependant() {
            get(proto().role()).setValue(TenantInLease.Role.Dependent, false);
            get(proto().role()).setEditable(false);

            get(proto().takeOwnership()).setValue(true, false);
            get(proto().takeOwnership()).setEnabled(false);
        }

        private void enableRoleAndOwnership() {
            get(proto().role()).setEditable(true);
            get(proto().takeOwnership()).setEnabled(true);
        }
    }
}