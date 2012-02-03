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
package com.propertyvista.portal.ptapp.client.ui.steps.summary;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
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

    public SignatureFolder() {
        super(DigitalSignature.class, false);
    }

    @Override
    public IFolderItemDecorator<DigitalSignature> createItemDecorator() {
        BoxFolderItemDecorator<DigitalSignature> decor = (BoxFolderItemDecorator<DigitalSignature>) super.createItemDecorator();
        decor.setCollapsible(false);
        return decor;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof DigitalSignature) {
            return new DigitalSignatureEditor();
        }
        return super.create(member);
    }

    private class DigitalSignatureEditor extends CEntityDecoratableEditor<DigitalSignature> {

        public DigitalSignatureEditor() {
            super(DigitalSignature.class);
            inheritEditable(false);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().person().person().name(), new CEntityLabel<Name>()), 25).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fullName(), new CTextField()), 25).build());

            row = -1;
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().ipAddress()), 20).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().timestamp()), 20).build());

            main.setStyleName(DEFAULT_STYLE_PREFIX);
            return main;
        }

        @Override
        protected void onPopulate() {
            super.onPopulate();
            setEditable(!DigitalSignatureValidation.isSignatureValid(getValue().person().person(), getValue().fullName().getValue()));
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().fullName()).addValueValidator(new EditableValueValidator<String>() {
                @Override
                public ValidationFailure isValid(CComponent<String, ?> component, String value) {
                    if (getValue() == null || getValue().isEmpty()) {
                        return null;
                    }

                    return DigitalSignatureValidation.isSignatureValid(getValue().person().person(), value) ? null : new ValidationFailure(i18n
                            .tr("Digital Signature Must Match Your Name On File"));
                }

            });
        }
    }
}
