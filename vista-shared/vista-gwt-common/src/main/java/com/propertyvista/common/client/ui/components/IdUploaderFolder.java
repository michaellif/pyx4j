/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

public class IdUploaderFolder extends VistaBoxFolder<IdentificationDocument> {

    private final static I18n i18n = I18n.get(IdUploaderFolder.class);

    protected ApplicationDocumentationPolicy documentationPolicy = null;

    public IdUploaderFolder() {
        super(IdentificationDocument.class);
        addValueValidator(new EditableValueValidator<IList<IdentificationDocument>>() {
            @Override
            public ValidationFailure isValid(CComponent<IList<IdentificationDocument>, ?> component, IList<IdentificationDocument> value) {
                if (value != null) {
                    if (documentationPolicy != null) {
                        int numOfRemainingDocs = documentationPolicy.numberOfRequiredIDs().getValue() - getValue().size();
                        if (numOfRemainingDocs > 0) {
                            return new ValidationFailure(i18n.tr("{0} more documents are required", numOfRemainingDocs));
                        }
                    } else {
                        return new ValidationFailure(i18n.tr("Validation Policy Not Available"));
                    }
                }
                return null;
            }
        });
        asWidget().setSize("100%", "100%");
    }

    @Override
    protected void onPopulate() {
        ClientPolicyManager.obtainEffectivePolicy(ClientPolicyManager.getOrganizationPoliciesNode(), ApplicationDocumentationPolicy.class,
                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                    @Override
                    public void onSuccess(ApplicationDocumentationPolicy result) {
                        documentationPolicy = result;
                        IdUploaderFolder.super.onPopulate();
                    }
                });
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof IdentificationDocument) {
            return new IdentificationDocumentEditor();
        } else {
            return super.create(member);
        }
    }

    private class IdentificationDocumentEditor extends CEntityDecoratableEditor<IdentificationDocument> {

        public IdentificationDocumentEditor() {
            super(IdentificationDocument.class);
        }

        @SuppressWarnings("unchecked")
        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            content.setSize("100%", "100%");
            int row = -1;
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().idType(), new CEntityComboBox(IdentificationDocumentType.class))).labelWidth(8)
                    .build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().idNumber())).labelWidth(8).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().notes())).labelWidth(8).build());
            ApplicationDocumentFileUploaderFolder docPagesFolder = new ApplicationDocumentFileUploaderFolder();
            docPagesFolder.addValueValidator(new EditableValueValidator<IList<ApplicationDocumentFile>>() {

                @Override
                public ValidationFailure isValid(CComponent<IList<ApplicationDocumentFile>, ?> component, IList<ApplicationDocumentFile> value) {
                    if (value != null && value.size() < 1) {
                        return new ValidationFailure(i18n.tr("at least one document file is required"));
                    } else {
                        return null;
                    }
                }
            });
            content.setH3(++row, 0, 1, i18n.tr("Files"));
            content.setWidget(++row, 0, inject(proto().documentPages(), docPagesFolder));
            return content;
        }
    }

}
