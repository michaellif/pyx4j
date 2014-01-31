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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.DocumentTypeSelectorDialog;
import com.propertyvista.domain.media.IdentificationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocumentFolder;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class IdUploaderFolder extends PortalBoxFolder<IdentificationDocumentFolder> {

    final static I18n i18n = I18n.get(IdUploaderFolder.class);

    protected ApplicationDocumentationPolicy documentsPolicy = null;

    public IdUploaderFolder() {
        super(IdentificationDocumentFolder.class, i18n.tr("Identification Document"));

        addValueValidator(new EditableValueValidator<IList<IdentificationDocumentFolder>>() {
            @Override
            public ValidationError isValid(CComponent<IList<IdentificationDocumentFolder>> component, IList<IdentificationDocumentFolder> value) {
                if (value != null) {
//                    assert (documentsPolicy != null);
                    if (documentsPolicy != null) {
                        int numOfRemainingDocs = documentsPolicy.numberOfRequiredIDs().getValue() - getValue().size();
                        if (numOfRemainingDocs > 0) {
                            return new ValidationError(component, i18n.tr("{0} more documents are required", numOfRemainingDocs));
                        }
                    }
                }
                return null;
            }
        });

        asWidget().setSize("100%", "100%");
    }

    public void setDocumentsPolicy(ApplicationDocumentationPolicy documentsPolicy) {
        this.documentsPolicy = documentsPolicy;

        if (documentsPolicy != null) {
            StringBuilder rule = new StringBuilder(i18n.tr("{0} ID(s) required", documentsPolicy.numberOfRequiredIDs().getValue()));
            rule.append(" (");
            for (IdentificationDocumentType docType : documentsPolicy.allowedIDs()) {
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
    protected void addItem() {
        new DocumentTypeSelectorDialog(documentsPolicy) {
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

    private class IdentificationDocumentEditor extends CEntityForm<IdentificationDocumentFolder> {

        public IdentificationDocumentEditor() {
            super(IdentificationDocumentFolder.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();

            int row = -1;
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().idType())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().donotHave())).componentWidth("auto").build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().idNumber())).build());
            content.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().notes())).build());

            IdentificationDocumentFolderUploaderFolder docPagesFolder = new IdentificationDocumentFolderUploaderFolder();
            docPagesFolder.addValueValidator(new EditableValueValidator<IList<IdentificationDocumentFile>>() {
                @Override
                public ValidationError isValid(CComponent<IList<IdentificationDocumentFile>> component, IList<IdentificationDocumentFile> value) {
                    if (value != null && value.size() < 1) {
                        return new ValidationError(component, i18n.tr("at least one document file is required"));
                    } else {
                        return null;
                    }
                }
            });

            // Tune ups:
            get(proto().idType()).setViewable(true);

            get(proto().donotHave()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    updateEditablity();
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
            get(proto().donotHave()).setVisible(getValue().idType().required().getValue(false));
            updateEditablity();
        }

        private void updateEditablity() {
            boolean canEdit = !getValue().donotHave().getValue(false);
            get(proto().idNumber()).setEnabled(canEdit);
            get(proto().notes()).setEnabled(canEdit);
            get(proto().files()).setEnabled(canEdit);
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
