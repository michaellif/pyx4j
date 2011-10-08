/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.tenants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.VistaEntityFolder;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderItemDecorator;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.common.client.ui.validators.OldAgeValidator;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.domain.tenant.TenantIn;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInApplicationDTO;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInApplicationListDTO;

public class TenantsViewForm extends CEntityEditor<TenantInApplicationListDTO> {

    static I18n i18n = I18nFactory.getI18n(TenantsViewForm.class);

    private int maxTenants;

    public TenantsViewForm() {
        super(TenantInApplicationListDTO.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        main.add(inject(proto().tenants(), createTenantsEditorColumns()));
        return main;
    }

    @Override
    public void addValidations() {
        super.addValueValidator(new EditableValueValidator<TenantInApplicationListDTO>() {

            @Override
            public boolean isValid(CEditableComponent<TenantInApplicationListDTO, ?> component, TenantInApplicationListDTO value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().tenants());
            }

            @Override
            public String getValidationMessage(CEditableComponent<TenantInApplicationListDTO, ?> component, TenantInApplicationListDTO value) {
                return i18n.tr("Duplicate tenants specified");
            }
        });

        maxTenants = proto().tenants().getMeta().getLength();
        super.addValueValidator(new EditableValueValidator<TenantInApplicationListDTO>() {

            @Override
            public boolean isValid(CEditableComponent<TenantInApplicationListDTO, ?> component, TenantInApplicationListDTO value) {
                int size = getValue().tenants().size();
                return (size <= maxTenants) && ((value.tenantsMaximum().isNull() || (size <= value.tenantsMaximum().getValue())));
            }

            @Override
            public String getValidationMessage(CEditableComponent<TenantInApplicationListDTO, ?> component, TenantInApplicationListDTO value) {
                return i18n.tr("Exceeded number of allowed tenants");
            }
        });
    }

    private CEntityFolder<TenantInApplicationDTO> createTenantsEditorColumns() {

        return new VistaEntityFolder<TenantInApplicationDTO>(TenantInApplicationDTO.class, i18n.tr("Person"), isEditable()) {
            private final VistaEntityFolder<TenantInApplicationDTO> parent = this;

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().person().name().firstName(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto().person().name().middleName(), "5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().person().name().lastName(), "12em"));
                columns.add(new EntityFolderColumnDescriptor(proto().person().birthDate(), "8.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().person().email(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().status(), "8.5em"));
                columns.add(new EntityFolderColumnDescriptor(proto().takeOwnership(), "5em"));
            }

            @Override
            protected List<EntityFolderColumnDescriptor> columns() {
                return columns;
            }

            @Override
            protected CEntityFolderItemEditor<TenantInApplicationDTO> createItem() {
                return new CEntityFolderRowEditor<TenantInApplicationDTO>(TenantInApplicationDTO.class, columns()) {

                    @SuppressWarnings("rawtypes")
                    @Override
                    public IsWidget createContent() {
                        if (isFirst()) {
                            HorizontalPanel main = new HorizontalPanel();
                            for (EntityFolderColumnDescriptor column : columns) {
                                CComponent<?> component = createCell(column);
                                // Don't show relation and takeOwnership 
                                if (column.getObject() == proto().relationship() || column.getObject() == proto().takeOwnership()) {
                                    component.setVisible(false);
                                } else if (column.getObject() == proto().person().email()) {
                                    ((CEditableComponent) component).setEditable(false);
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
                                return TenantsViewForm.i18n.tr("Applicant and co-applicant should be at least 18 years old");
                            }
                        });

                        if (!isFirst()) { // all this stuff isn't for primary applicant:  
                            get(proto().person().birthDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
                                @Override
                                public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                                    TenantIn.Status status = getValue().status().getValue();
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
                    public void populate(TenantInApplicationDTO value) {
                        super.populate(value);

                        if (!isFirst() && !value.person().birthDate().isNull()) {
                            if (ValidationUtils.isOlderThen18(value.person().birthDate().getValue())) {
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

                    @Override
                    public IFolderItemDecorator<TenantInApplicationDTO> createDecorator() {
                        return new VistaTableFolderItemDecorator<TenantInApplicationDTO>(parent, parent.isEditable() && !isFirst());
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
                };
            }
        };
    }
}
