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
import com.propertyvista.portal.domain.pt.PotentialTenant.Relationship;
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
                // Don't show dependent and takeOwnership 
                if (column.getObject() == proto().dependant() || column.getObject() == proto().takeOwnership()) {
                    continue;
                }
                CComponent<?> component = createCell(column);
                if (column.getObject() == proto().email()) {
                    ((CEditableComponent) component).setEditable(false);
                }
                main.add(createDecorator(component, column.getWidth()));
            }
            return main;
        } else {
            return super.createContent();
        }
    }

    @Override
    public void attachContent() {
        super.attachContent();

        get(proto().birthDate()).addValueValidator(new BirthdayDateValidator());

        get(proto().birthDate()).addValueValidator(new EditableValueValidator<Date>() {

            @Override
            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                Relationship relationship = getValue().relationship().getValue();
                if ((relationship == Relationship.Applicant) || (relationship == Relationship.CoApplicant)) {
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
                        get(proto().takeOwnership()).setVisible(true);
                        if (!get(proto().dependant()).getValue())
                            showCoApplicantRelation();
                    } else {
                        get(proto().dependant()).setValue(true);

                        get(proto().takeOwnership()).setValue(false);
                        get(proto().takeOwnership()).setVisible(false);
                        hideCoApplicantRelation();
                    }
                }
            });

            get(proto().relationship()).addValueChangeHandler(new ValueChangeHandler<Relationship>() {
                @Override
                public void onValueChange(ValueChangeEvent<Relationship> event) {
                    if (event.getValue() == Relationship.CoApplicant) {
                        get(proto().dependant()).setValue(false);
                        get(proto().dependant()).setVisible(false);
                    } else {
                        get(proto().dependant()).setVisible(true);
                    }
                }
            });

            get(proto().dependant()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue() == true) {
                        hideCoApplicantRelation();
                    } else {
                        showCoApplicantRelation();
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
                get(proto().takeOwnership()).setVisible(true);
                if (!value.dependant().getValue()) {
                    showCoApplicantRelation();
                }
            } else {
                get(proto().dependant()).setValue(true);

                get(proto().takeOwnership()).setValue(false);
                get(proto().takeOwnership()).setVisible(false);
                hideCoApplicantRelation();
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
        CComponent<?> comp = null;
        if (isFirst() && proto().relationship() == column.getObject()) {
            CTextField textComp = new CTextField();
            textComp.setEditable(false);
            textComp.setValue(Relationship.Applicant.name());
            comp = textComp;
        } else {
            comp = super.createCell(column);
            if (proto().relationship() == column.getObject()) {
                Collection<Relationship> relationships = EnumSet.allOf(Relationship.class);
                relationships.remove(Relationship.Applicant);
                ((CComboBox) comp).setOptions(relationships);
            }
        }
        return comp;
    }

    @Override
    public FolderItemDecorator createFolderItemDecorator() {
        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), TenantsViewForm.i18n.tr("Remove person"), !isFirst());
    }

    @SuppressWarnings("unchecked")
    private void hideCoApplicantRelation() {
        ((CComboBox<Relationship>) get(proto().relationship())).removeOption(Relationship.CoApplicant);
        if (get(proto().relationship()).isValueEmpty()) {
            //TODO this is wrong fix me!
            get(proto().relationship()).setValue(Relationship.Other);
        }
    }

    @SuppressWarnings("unchecked")
    private void showCoApplicantRelation() {
        ((CComboBox<Relationship>) get(proto().relationship())).updateOption(Relationship.CoApplicant);
    }
}