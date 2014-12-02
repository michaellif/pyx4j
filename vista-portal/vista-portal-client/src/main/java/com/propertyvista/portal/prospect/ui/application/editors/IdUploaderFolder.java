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
package com.propertyvista.portal.prospect.ui.application.editors;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.DocumentTypeSelectorDialog;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.ApplicationDocumentType.Importance;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.portal.shared.ui.AccessoryEntityForm;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

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

    // currently internal folder validation doesn't work - so this validation copied(moved) to the AboutYouStep.addValidations()
    public void _addValidations() {
        super.addValidations();

        addComponentValidator(new AbstractComponentValidator<IList<IdentificationDocumentFolder>>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() != null && documentationPolicy != null) {
                    int requredDocsCount = documentationPolicy.numberOfRequiredIDs().getValue();
                    int remainingDocsCount = requredDocsCount - getCComponent().getValue().size();
                    if (remainingDocsCount > 0) {
                        return new BasicValidationError(getCComponent(), i18n.tr(
                                "You have to provide {0} identification document(s), {1} more document(s) is/are required", requredDocsCount,
                                remainingDocsCount));
                    }

                    // 'Required' check:
                    for (IdentificationDocumentType docType : documentationPolicy.allowedIDs()) {
                        if (docType.importance().getValue() == Importance.Required) {
                            boolean found = false;
                            for (IdentificationDocumentFolder doc : getCComponent().getValue()) {
                                if (doc.idType().equals(docType)) {
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                return new BasicValidationError(getCComponent(), i18n.tr("You have to provide {0} identification document which is required",
                                        docType.getStringView()));
                            }
                        }
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
    protected CForm<IdentificationDocumentFolder> createItemForm(IObject<?> member) {
        return new IdentificationDocumentEditor();
    }

    @Override
    protected CFolderItem<IdentificationDocumentFolder> createItem(boolean first) {
        return new CFolderItem<IdentificationDocumentFolder>(IdentificationDocumentFolder.class) {
            @Override
            public IFolderItemDecorator<IdentificationDocumentFolder> createItemDecorator() {
                return IdUploaderFolder.this.createItemDecorator();
            }

            @Override
            public void onValueSet(boolean populate) {
                // update removable
                setRemovable(!Importance.Required.equals(getValue().idType().importance().getValue()));
            }

            @Override
            protected CForm<IdentificationDocumentFolder> createItemForm(IObject<?> member) {
                return IdUploaderFolder.this.createItemForm(null);
            }
        };
    }

    private class IdentificationDocumentEditor extends AccessoryEntityForm<IdentificationDocumentFolder> {

        public IdentificationDocumentEditor() {
            super(IdentificationDocumentFolder.class);
        }

        @Override
        protected IsWidget createContent() {
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().idType(), new CEntityLabel<IdentificationDocumentType>()).decorate();
            formPanel.append(Location.Left, proto().idNumber()).decorate();
            formPanel.append(Location.Left, proto().notes()).decorate();

            formPanel.h3(i18n.tr("Proof Documents"));
            formPanel.append(Location.Left, proto().files(), new IdFileUploaderFolder());
            return formPanel;
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
                public BasicValidationError isValid() {
                    if (get(proto().idType()).getValue() != null) {
                        switch (get(proto().idType()).getValue().type().getValue()) {
                        case canadianSIN:
                            if (!ValidationUtils.isSinValid(getCComponent().getValue())) {
                                return new BasicValidationError(getCComponent(), i18n.tr("Invalid SIN"));
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

            @SuppressWarnings("unchecked")
            CFolder<IdentificationDocumentFile> folder = ((CFolder<IdentificationDocumentFile>) ((CComponent<?, ?, ?, ?>) get(proto().files())));
            folder.setNoDataLabel(i18n.tr("Please provide at least one document file"));
            folder.addComponentValidator(new AbstractComponentValidator<IList<IdentificationDocumentFile>>() {
                @Override
                public BasicValidationError isValid() {
                    if (getCComponent().getValue() != null && getCComponent().getValue().size() < 1) {
                        return new BasicValidationError(getCComponent(), i18n.tr("At least one document file is required"));
                    }
                    return null;
                }
            });
        }

        @Override
        public void generateMockData() {
            if (get(proto().idType()).getValue() != null) {
                switch (get(proto().idType()).getValue().type().getValue()) {
                case canadianSIN:
                    get(proto().idNumber()).setMockValue(CreditCardNumberGenerator.generateCanadianSin());
                    break;
                case citizenship:
                    get(proto().idNumber()).setMockValue("CS1234567890");
                    break;
                case immigration:
                    get(proto().idNumber()).setMockValue("IM1234567890");
                    break;
                case license:
                    get(proto().idNumber()).setMockValue("LS1234567890");
                    break;
                case other:
                    get(proto().idNumber()).setMockValue("OR1234567890");
                    break;
                case passport:
                    get(proto().idNumber()).setMockValue("PT1234567890");
                    break;
                default:
                    break;
                }
            }
        }
    }
}
