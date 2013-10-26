/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 8, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.ptapp.DigitalSignature;
import com.propertyvista.portal.rpc.ptapp.validators.DigitalSignatureValidation;

public class SignatureFolder extends VistaBoxFolder<DigitalSignature> {

    private static final I18n i18n = I18n.get(SignatureFolder.class);

    public final static String DEFAULT_STYLE_PREFIX = "SignatureFolder";

    public static enum StyleSuffix implements IStyleName {
        Label, Edit
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    private final boolean editable;

    public SignatureFolder(boolean modifiable) {
        super(DigitalSignature.class, false);
        this.editable = modifiable;
    }

    @Override
    public IFolderItemDecorator<DigitalSignature> createItemDecorator() {
        BoxFolderItemDecorator<DigitalSignature> decor = (BoxFolderItemDecorator<DigitalSignature>) super.createItemDecorator();
        decor.setCollapsible(false);
        return decor;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof DigitalSignature) {
            return new DigitalSignatureEditor();
        }
        return super.create(member);
    }

    private class DigitalSignatureEditor extends CEntityDecoratableForm<DigitalSignature> {

        public DigitalSignatureEditor() {
            super(DigitalSignature.class);
            setViewable(true);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

            int row = -1;
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().person().person().name(), new CEntityLabel<Name>()), 10).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().fullName()), 10).build());
            if (editable) {
                get(proto().fullName()).inheritViewable(false); // always not viewable!
                get(proto().fullName()).inheritEditable(false); // control state later in populate...
            }

            row = -1;
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().ipAddress()), 10).build());
            main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().timestamp()), 10).build());

            main.setStyleName(DEFAULT_STYLE_PREFIX);
            return main;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (editable) {
                get(proto().fullName()).setEditable(
                        !DigitalSignatureValidation.isSignatureValid(getValue().person().person(), getValue().fullName().getValue()));
            }
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().fullName()).addValueValidator(new EditableValueValidator<String>() {
                @Override
                public ValidationError isValid(CComponent<String> component, String value) {
                    if (getValue() == null || getValue().isEmpty()) {
                        return null;
                    }

                    return DigitalSignatureValidation.isSignatureValid(getValue().person().person(), value) ? null : new ValidationError(component, i18n
                            .tr("Digital Signature Must Match Your Name On File"));
                }

            });
        }
    }
}
