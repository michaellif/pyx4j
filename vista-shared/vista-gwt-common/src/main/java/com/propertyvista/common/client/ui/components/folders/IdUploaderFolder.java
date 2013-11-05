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
package com.propertyvista.common.client.ui.components.folders;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.ApplicationDocumentFileUploaderFolder;
import com.propertyvista.common.client.ui.components.DocumentTypeSelectorDialog;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.VistaTODO;

public class IdUploaderFolder extends VistaBoxFolder<IdentificationDocument> {

    final static I18n i18n = I18n.get(IdUploaderFolder.class);

    protected ApplicationDocumentationPolicy documentationPolicy = null;

    public IdUploaderFolder() {
        super(IdentificationDocument.class);

        if (!VistaTODO.ApplicationDocumentationPolicyRefacotring) {

            addValueValidator(new EditableValueValidator<IList<IdentificationDocument>>() {
                @Override
                public ValidationError isValid(CComponent<IList<IdentificationDocument>> component, IList<IdentificationDocument> value) {
                    if (value != null) {
// TODO it should be enough, but now validate is called on populate!?                    
//                    assert (documentationPolicy != null);
                        if (documentationPolicy != null) {
                            int numOfRemainingDocs = documentationPolicy.numberOfRequiredIDs().getValue() - getValue().size();
                            if (numOfRemainingDocs > 0) {
                                return new ValidationError(component, i18n.tr("{0} more documents are required", numOfRemainingDocs));
                            }
                        }
                    }
                    return null;
                }
            });

        }

        asWidget().setSize("100%", "100%");
    }

    public void setParentEntity(IEntity parentEntity) {
        if (!VistaTODO.ApplicationDocumentationPolicyRefacotring) {

            ClientPolicyManager.obtainHierarchicalEffectivePolicy(parentEntity, ApplicationDocumentationPolicy.class,
                    new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                        @Override
                        public void onSuccess(ApplicationDocumentationPolicy result) {
                            documentationPolicy = result;
                        }
                    });

        }
    }

    @Override
    protected void addItem() {
        new DocumentTypeSelectorDialog() {
            @Override
            public boolean onClickOk() {
                IdentificationDocument document = EntityFactory.create(IdentificationDocument.class);
                document.idType().set(getSelectedItems().get(0));
                addItem(document);
                return true;
            }
        }.show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof IdentificationDocument) {
            return new IdentificationDocumentEditor();
        } else {
            return super.create(member);
        }
    }

    private class IdentificationDocumentEditor extends CEntityForm<IdentificationDocument> {

        public IdentificationDocumentEditor() {
            super(IdentificationDocument.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().idType())).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().idNumber())).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().notes())).build());

            ApplicationDocumentFileUploaderFolder docPagesFolder = new ApplicationDocumentFileUploaderFolder();
            docPagesFolder.addValueValidator(new EditableValueValidator<IList<ApplicationDocumentFile>>() {
                @Override
                public ValidationError isValid(CComponent<IList<ApplicationDocumentFile>> component, IList<ApplicationDocumentFile> value) {
                    if (value != null && value.size() < 1) {
                        return new ValidationError(component, i18n.tr("at least one document file is required"));
                    } else {
                        return null;
                    }
                }
            });

            // Tune ups:
            get(proto().idType()).setViewable(true);

            content.setH3(++row, 0, 2, i18n.tr("Files"));
            content.setWidget(++row, 0, 2, inject(proto().documentPages(), docPagesFolder));
            return content;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (isViewable()) {
                get(proto().notes()).setVisible(!getValue().notes().isNull());
            }
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().idNumber()).addValueValidator(new EditableValueValidator<String>() {
                @Override
                public ValidationError isValid(CComponent<String> component, String value) {
                    if (get(proto().idType()).getValue() != null) {
                        switch (get(proto().idType()).getValue().type().getValue()) {
                        case canadianSIN:
                            if (!ValidationUtils.isSinValid(value.trim().replaceAll(" ", ""))) {
                                return new ValidationError(component, i18n.tr("Invalid SIN"));
                            }
                            break;
                        case citizenship:
                            break;
                        case immigration:
                            break;
                        case license:
                            break;
                        case other:
                            break;
                        case passport:
                            break;
                        default:
                            break;
                        }
                    }

                    return null;
                }
            });
        }
    }
}
