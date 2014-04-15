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
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.common.client.policy.ClientPolicyManager;
import com.propertyvista.common.client.ui.components.DocumentTypeSelectorDialog;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType.Importance;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.CreditCardNumberGenerator;

public class IdUploaderFolder extends VistaBoxFolder<IdentificationDocumentFolder> {

    final static I18n i18n = I18n.get(IdUploaderFolder.class);

    private ApplicationDocumentationPolicy documentationPolicy;

    public IdUploaderFolder() {
        super(IdentificationDocumentFolder.class, i18n.tr("Identification Document"));
    }

    public void setPolicyEntity(IEntity parentEntity) {
        ClientPolicyManager.obtainHierarchicalEffectivePolicy(parentEntity, ApplicationDocumentationPolicy.class,
                new DefaultAsyncCallback<ApplicationDocumentationPolicy>() {
                    @Override
                    public void onSuccess(ApplicationDocumentationPolicy result) {
                        documentationPolicy = result;

                        setNoDataNotificationWidget(null);
                        if (documentationPolicy != null) {
                            StringBuilder rule = new StringBuilder(i18n.tr("{0} ID(s) required", documentationPolicy.numberOfRequiredIDs().getValue()));
                            rule.append(" (");
                            for (IdentificationDocumentType docType : documentationPolicy.allowedIDs()) {
                                rule.append(docType.name().getStringView());
                                rule.append(", ");
                            }
                            rule.deleteCharAt(rule.length() - 1);
                            rule.deleteCharAt(rule.length() - 1);
                            rule.append(")");

                            setNoDataNotificationWidget(new Label(rule.toString()));
                        }
                    }
                });
    }

    @Override
    public void addValidations() {
        super.addValidations();

        addComponentValidator(new AbstractComponentValidator<IList<IdentificationDocumentFolder>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && documentationPolicy != null) {
                    int numOfRemainingDocs = documentationPolicy.numberOfRequiredIDs().getValue() - getValue().size();
                    if (numOfRemainingDocs > 0) {
                        return new FieldValidationError(getComponent(), i18n.tr("{0} more document(s) is/are required", numOfRemainingDocs));
                    }
                }
                return null;
            }
        });
    }

    @Override
    protected void addItem() {
        Collection<IdentificationDocumentType> usedTypes = new ArrayList<IdentificationDocumentType>();
        for (IdentificationDocumentFolder doc : getValue()) {
            usedTypes.add(doc.idType());
        }

        new DocumentTypeSelectorDialog(documentationPolicy, usedTypes) {
            @Override
            public boolean onClickOk() {
                IdentificationDocumentFolder document = EntityFactory.create(IdentificationDocumentFolder.class);
                document.idType().set(getSelectedItems().get(0));
                addItem(document);
                return true;
            }
        }.show();
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof IdentificationDocumentFolder) {
            return new IdentificationDocumentEditor();
        } else {
            return super.create(member);
        }
    }

    @Override
    protected CEntityFolderItem<IdentificationDocumentFolder> createItem(boolean first) {
        return new CEntityFolderItem<IdentificationDocumentFolder>(IdentificationDocumentFolder.class) {
            @Override
            public IFolderItemDecorator<IdentificationDocumentFolder> createItemDecorator() {
                return IdUploaderFolder.this.createItemDecorator();
            }

            @Override
            public void onValueSet(boolean populate) {
                // update removable
                setRemovable(!Importance.Required.equals(getValue().idType().importance().getValue()));
            }
        };
    }

    private class IdentificationDocumentEditor extends CEntityForm<IdentificationDocumentFolder> {

        public IdentificationDocumentEditor() {
            super(IdentificationDocumentFolder.class);
        }

        @Override
        protected IsWidget createContent() {
            TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, inject(proto().idType(), new FormDecoratorBuilder().build()));
            content.setWidget(++row, 0, inject(proto().idNumber(), new FormDecoratorBuilder().build()));
            content.setWidget(++row, 0, inject(proto().notes(), new FormDecoratorBuilder().build()));

            IdentificationDocumentFileUploaderFolder docPagesFolder = new IdentificationDocumentFileUploaderFolder();
            docPagesFolder.addComponentValidator(new AbstractComponentValidator<IList<IdentificationDocumentFile>>() {
                @Override
                public FieldValidationError isValid() {
                    if (getComponent().getValue() != null && getComponent().getValue().size() < 1) {
                        return new FieldValidationError(getComponent(), i18n.tr("At least one document file is required"));
                    } else {
                        return null;
                    }
                }
            });

            // Tune ups:
            get(proto().idType()).setViewable(true);

            content.setH3(++row, 0, 2, i18n.tr("Files"));
            content.setWidget(++row, 0, 2, inject(proto().files(), docPagesFolder));
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

            get(proto().idNumber()).addComponentValidator(new AbstractComponentValidator<String>() {
                @Override
                public FieldValidationError isValid() {
                    if (get(proto().idType()).getValue() != null && getComponent().getValue() != null) {
                        switch (get(proto().idType()).getValue().type().getValue()) {
                        case canadianSIN:
                            if (!ValidationUtils.isSinValid(getComponent().getValue().trim().replaceAll(" ", ""))) {
                                return new FieldValidationError(getComponent(), i18n.tr("Invalid SIN"));
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

            if (ApplicationMode.isDevelopment()) {
                this.addDevShortcutHandler(new DevShortcutHandler() {
                    @Override
                    public void onDevShortcut(DevShortcutEvent event) {
                        if (event.getKeyCode() == 'Q') {
                            event.consume();
                            devGenerateNumbers();
                        }
                    }
                });
            }
        }

        private void devGenerateNumbers() {
            if (getValue().idType().type().getValue() == IdentificationDocumentType.Type.canadianSIN) {
                get(proto().idNumber()).setValue(CreditCardNumberGenerator.generateCanadianSin());
            }
        }
    }
}
