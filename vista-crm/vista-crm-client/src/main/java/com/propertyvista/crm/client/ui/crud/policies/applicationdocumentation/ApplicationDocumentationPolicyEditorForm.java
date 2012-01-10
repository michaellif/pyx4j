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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.specials.IdentificationDocument;

public class ApplicationDocumentationPolicyEditorForm extends PolicyDTOTabPanelBasedEditorForm<ApplicationDocumentationPolicyDTO> {

    private static final I18n i18n = I18n.get(ApplicationDocumentationPolicyEditorForm.class);

    public ApplicationDocumentationPolicyEditorForm(IEditableComponentFactory factory) {
        super(ApplicationDocumentationPolicyDTO.class, factory);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
    }

    @Override
    protected List<com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedEditorForm.TabDescriptor> createCustomTabPanels() {
        return Arrays.asList(new TabDescriptor(createEdtorFormTab(), i18n.tr("Settings")));
    }

    private Widget createEdtorFormTab() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().allowedIDs(), new IdentificationDocumentFolder())).componentWidth(20).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfRequiredIDs())).componentWidth(3).build());
        get(proto().numberOfRequiredIDs()).addValueValidator(new EditableValueValidator<Integer>() {
            @Override
            public ValidationFailure isValid(CComponent<Integer, ?> component, Integer value) {
                if (value == null || value == 0) {
                    return new ValidationFailure(i18n.tr("At least one ID is required"));
                } else if (getValue() != null && (getValue().allowedIDs().isEmpty() || value > getValue().allowedIDs().size())) {
                    return new ValidationFailure(i18n.tr("The number of required IDs must not exceed the number of allowed IDs"));
                } else {
                    return null;
                }
            }
        });
        return content;
    }

    private static class IdentificationDocumentFolder extends VistaTableFolder<IdentificationDocument> {

        public IdentificationDocumentFolder() {
            super(IdentificationDocument.class);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(new EntityFolderColumnDescriptor(proto().name(), "20em"));
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof IdentificationDocument) {
                return new IdentificationDocumentEditor();
            } else {
                return super.create(member);
            }
        }

        private static class IdentificationDocumentEditor extends CEntityEditor<IdentificationDocument> {

            public IdentificationDocumentEditor() {
                super(IdentificationDocument.class);
            }

            @Override
            public IsWidget createContent() {
                return inject(proto().name());
            }

        }

    }

}
