/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.validators.BirthdayDateValidator;
import com.propertyvista.portal.client.ptapp.ui.validators.ValidationUtils;
import com.propertyvista.portal.domain.pt.PotentialTenant.Status;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;

import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;

final class TenantsViewFolderRow extends CEntityFolderRow<PotentialTenantInfo> {

    TenantsViewFolderRow(List<EntityFolderColumnDescriptor> columns) {
        super(PotentialTenantInfo.class, columns);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public IsWidget createContent() {
        if (isFirst()) {
            HorizontalPanel main = new HorizontalPanel();
            main.setWidth("100%");
            for (EntityFolderColumnDescriptor column : columns) {
                CComponent<?> component = createCell(column);
                // Don't show relation and takeOwnership 
                if (column.getObject() == proto().relationship() || column.getObject() == proto().takeOwnership()) {
                    component.setVisible(false);
                } else if (column.getObject() == proto().email()) {
                    ((CEditableComponent) component).setEditable(false);
                }
                main.add(createDecorator(column, component, column.getWidth()));
            }
            return main;
        } else {
            return super.createContent();
        }
    }

    @Override
    public void addValidations() {

        get(proto().birthDate()).addValueValidator(new BirthdayDateValidator());

        get(proto().birthDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                Status status = getValue().status().getValue();
                if ((status == Status.Applicant) || (status == Status.CoApplicant)) {
                    // TODO I Believe that this is not correct, this logic has to be applied to Dependents as well, as per VISTA-273
                    return ValidationUtils.isOlderThen18(value);
                } else {
                    return true;
                }
            }

            @Override
            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                return TenantsViewForm.i18n.tr("Applicant and co-applicant should be at least 18 years old");
            }
        });

        if (!isFirst()) { // all this stuff isn't for primary applicant:  
            get(proto().birthDate()).addValueChangeHandler(new ValueChangeHandler<Date>() {

                @Override
                public void onValueChange(ValueChangeEvent<Date> event) {
                    if (ValidationUtils.isOlderThen18(event.getValue())) {
                        enableStatusAndOwnership();
                        get(proto().status()).setValue(null);
                    } else {
                        setMandatoryDependant();
                    }
                }
            });
        }
    }

    @Override
    public void populate(PotentialTenantInfo value) {
        super.populate(value);

        if (!isFirst()) {
            if (ValidationUtils.isOlderThen18(value.birthDate().getValue())) {
                enableStatusAndOwnership();
            } else {
                setMandatoryDependant();
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
        CComponent<?> comp = null;

        if (isFirst() && proto().status() == column.getObject()) {
            CTextField textComp = new CTextField();
            textComp.setEditable(false);
            textComp.setValue(Status.Applicant.name());
            comp = textComp;
        } else {
            comp = super.createCell(column);

            if (proto().status() == column.getObject()) {
                Collection<Status> status = EnumSet.allOf(Status.class);
                status.remove(Status.Applicant);
                ((CComboBox) comp).setOptions(status);
            }
        }
        return comp;
    }

    @Override
    public FolderItemDecorator createFolderItemDecorator() {
        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), TenantsViewForm.i18n.tr("Remove person"), !isFirst());
    }

    private void setMandatoryDependant() {
        get(proto().status()).setValue(Status.Dependant);
        get(proto().status()).setEnabled(false);

        get(proto().takeOwnership()).setValue(true);
        get(proto().takeOwnership()).setEnabled(false);
    }

    private void enableStatusAndOwnership() {
        get(proto().status()).setEnabled(true);
        get(proto().takeOwnership()).setEnabled(true);
    }
}