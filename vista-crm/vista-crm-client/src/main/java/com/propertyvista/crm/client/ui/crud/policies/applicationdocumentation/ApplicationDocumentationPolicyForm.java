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

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

public class ApplicationDocumentationPolicyForm extends PolicyDTOTabPanelBasedForm<ApplicationDocumentationPolicyDTO> {

    private static final I18n i18n = I18n.get(ApplicationDocumentationPolicyForm.class);

    public ApplicationDocumentationPolicyForm() {
        this(false);
    }

    public ApplicationDocumentationPolicyForm(boolean viewMode) {
        super(ApplicationDocumentationPolicyDTO.class, viewMode);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        return Arrays.asList(createEdtorFormTab());
    }

    private FormFlexPanel createEdtorFormTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("Settings"));
        int row = -1;
        content.setH3(++row, 0, 1, proto().allowedIDs().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().allowedIDs(), new IdentificationDocumentFolder()));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numberOfRequiredIDs())).componentWidth(3).build());
        return content;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().numberOfRequiredIDs()).addValueValidator(new EditableValueValidator<Integer>() {
            @Override
            public ValidationError isValid(CComponent<Integer, ?> component, Integer value) {
                if (value == null || value == 0) {
                    return new ValidationError(component, i18n.tr("At least one ID is required"));
                } else if (getValue() != null && (getValue().allowedIDs().isEmpty() || value > getValue().allowedIDs().size())) {
                    return new ValidationError(component, i18n.tr("The number of required IDs must not exceed the number of allowed IDs"));
                } else {
                    return null;
                }
            }
        });
    }

    private static class IdentificationDocumentFolder extends VistaTableFolder<IdentificationDocumentType> {

        public IdentificationDocumentFolder() {
            super(IdentificationDocumentType.class);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                        new EntityFolderColumnDescriptor(proto().name(), "15em"), 
                        new EntityFolderColumnDescriptor(proto().type(), "15em")                    
                    );//@formatter:on
        }
//
//        @Override
//        public CComponent<?, ?> create(IObject<?> member) {
//            if (member instanceof IdentificationDocument) {
//                return new IdentificationDocumentEditor();
//            } else {
//                return super.create(member);
//            }
//        }
//
//        private static class IdentificationDocumentEditor extends CEntityEditor<IdentificationDocument> {
//
//            public IdentificationDocumentEditor() {
//                super(IdentificationDocument.class);
//            }
//
//            @Override
//            public IsWidget createContent() {
//                Widget w = inject(proto().name()).asWidget();
//                w.setWidth("30em");
//                return w;
//            }
//        }
    }

}
