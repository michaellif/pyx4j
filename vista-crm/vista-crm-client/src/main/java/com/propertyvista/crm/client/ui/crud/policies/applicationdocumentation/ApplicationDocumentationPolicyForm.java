/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

public class ApplicationDocumentationPolicyForm extends PolicyDTOTabPanelBasedForm<ApplicationDocumentationPolicyDTO> {

    private static final I18n i18n = I18n.get(ApplicationDocumentationPolicyForm.class);

    public ApplicationDocumentationPolicyForm(IForm<ApplicationDocumentationPolicyDTO> view) {
        super(ApplicationDocumentationPolicyDTO.class, view);
        addTab(createEdtorFormTab(), i18n.tr("Settings"));
    }

    private IsWidget createEdtorFormTab() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().numberOfRequiredIDs()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().mandatoryProofOfIncome()).decorate().componentWidth(120);

        formPanel.h3(proto().allowedIDs().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().allowedIDs(), new IdentificationDocumentFolder());

        return formPanel;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().numberOfRequiredIDs()).addComponentValidator(new AbstractComponentValidator<Integer>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() == null || getComponent().getValue() == 0) {
                    return new FieldValidationError(getComponent(), i18n.tr("At least one ID is required"));
                } else if (getValue() != null && (getValue().allowedIDs().isEmpty() || getComponent().getValue() > getValue().allowedIDs().size())) {
                    return new FieldValidationError(getComponent(), i18n.tr("The number of required IDs must not exceed the number of allowed IDs"));
                } else {
                    return null;
                }
            }
        });

        get(proto().allowedIDs()).addValueChangeHandler(new RevalidationTrigger<List<IdentificationDocumentType>>(get(proto().numberOfRequiredIDs())));
    }

    private class IdentificationDocumentFolder extends VistaTableFolder<IdentificationDocumentType> {

        public IdentificationDocumentFolder() {
            super(IdentificationDocumentType.class);
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                                new FolderColumnDescriptor(proto().type(), "15em", true),                    
                                new FolderColumnDescriptor(proto().name(), "30em"),
                                new FolderColumnDescriptor(proto().importance(), "10em")
                                );//@formatter:on
        }

        @Override
        protected void addItem() {
            new SelectEnumDialog<IdentificationDocumentType.Type>(i18n.tr("Select Document Type"), EnumSet.allOf(IdentificationDocumentType.Type.class)) {
                @Override
                public boolean onClickOk() {
                    IdentificationDocumentType item = EntityFactory.create(IdentificationDocumentType.class);
                    item.type().setValue(getSelectedType());
                    item.name().setValue(getSelectedType().toString());
                    addItem(item);
                    return true;
                }
            }.show();
        }
    }
}
