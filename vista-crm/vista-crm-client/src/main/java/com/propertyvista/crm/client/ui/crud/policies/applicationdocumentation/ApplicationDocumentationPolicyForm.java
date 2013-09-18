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
import java.util.List;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.DocumentTypeSelectorDialog;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

public class ApplicationDocumentationPolicyForm extends PolicyDTOTabPanelBasedForm<ApplicationDocumentationPolicyDTO> {

    private static final I18n i18n = I18n.get(ApplicationDocumentationPolicyForm.class);

    public ApplicationDocumentationPolicyForm(IForm<ApplicationDocumentationPolicyDTO> view) {
        super(ApplicationDocumentationPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createEdtorFormTab());
    }

    private TwoColumnFlexFormPanel createEdtorFormTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Settings"));

        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().numberOfRequiredIDs()), 3).build());

        content.setH3(++row, 0, 2, proto().allowedIDs().getMeta().getCaption());
        content.setWidget(++row, 0, 2, inject(proto().allowedIDs(), new IdentificationDocumentFolder()));

        return content;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().numberOfRequiredIDs()).addValueValidator(new EditableValueValidator<Integer>() {
            @Override
            public ValidationError isValid(CComponent<Integer> component, Integer value) {
                if (value == null || value == 0) {
                    return new ValidationError(component, i18n.tr("At least one ID is required"));
                } else if (getValue() != null && (getValue().allowedIDs().isEmpty() || value > getValue().allowedIDs().size())) {
                    return new ValidationError(component, i18n.tr("The number of required IDs must not exceed the number of allowed IDs"));
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
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                                new EntityFolderColumnDescriptor(proto().type(), "15em", true),                    
                                new EntityFolderColumnDescriptor(proto().name(), "30em")
                                );//@formatter:on
        }

        @Override
        protected void addItem() {
            new DocumentTypeSelectorDialog() {
                @Override
                public boolean onClickOk() {
                    addItem(getSelectedItems().get(0));
                    return true;
                }
            }.show();
        }
    }
}
