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
package com.propertyvista.portal.shared.ui.util.editors;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.DocumentTypeSelectorDialog;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType.Importance;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class IdUploaderFolder extends PortalBoxFolder<IdentificationDocumentFolder> {

    final static I18n i18n = I18n.get(IdUploaderFolder.class);

    private ApplicationDocumentationPolicy documentationPolicy;

    public IdUploaderFolder() {
        super(IdentificationDocumentFolder.class, i18n.tr("Identification Document"));
    }

    public void setDocumentsPolicy(ApplicationDocumentationPolicy policy) {
        this.documentationPolicy = policy;

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

    @Override
    public void addValidations() {
        super.addValidations();

        addComponentValidator(new AbstractComponentValidator<IList<IdentificationDocumentFolder>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && documentationPolicy != null) {
                    int requredDocsCount = documentationPolicy.numberOfRequiredIDs().getValue();
                    int remainingDocsCount = requredDocsCount - getValue().size();
                    if (remainingDocsCount > 0) {
                        return new FieldValidationError(getComponent(), i18n.tr(
                                "You have to provide {0} identification document(s), {1} more document(s) is/are required", requredDocsCount,
                                remainingDocsCount));
                    }
                }
                return null;
            }
        });
    }

    @Override
    protected void addItem() {
        Collection<IdentificationDocumentType> usedTypes = new ArrayList<>();
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

            // TODO - here for testing - remove
            @Override
            public void setRemovable(boolean removable) {
                super.setRemovable(removable);
            }
        };
    }

    private class IdentificationDocumentEditor extends CEntityForm<IdentificationDocumentFolder> {

        public IdentificationDocumentEditor() {
            super(IdentificationDocumentFolder.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().idType(), new CEntityLabel<IdentificationDocumentType>())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().idNumber())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().notes())).build());

            IdentificationDocumentFolderUploaderFolder docPagesFolder = new IdentificationDocumentFolderUploaderFolder();
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

//            CEntityFolderItem<?> parent = (CEntityFolderItem<?>) getParent();
//            parent.setRemovable(!Importance.Required.equals(getValue().idType().importance().getValue()));
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().idNumber()).addComponentValidator(new AbstractComponentValidator<String>() {
                @Override
                public FieldValidationError isValid() {
                    if (get(proto().idType()).getValue() != null) {
                        switch (get(proto().idType()).getValue().type().getValue()) {
                        case canadianSIN:
                            if (!ValidationUtils.isSinValid(getComponent().getValue())) {
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

        }

        @Override
        public void generateMockData() {
            if (getValue().idType().type().getValue() == IdentificationDocumentType.Type.canadianSIN) {
                get(proto().idNumber()).setMockValue(CreditCardNumberGenerator.generateCanadianSin());
            }
        }
    }
}
