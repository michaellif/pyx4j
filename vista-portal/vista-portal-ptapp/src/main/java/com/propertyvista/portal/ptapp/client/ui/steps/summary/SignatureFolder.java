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

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.ptapp.DigitalSignature;

public class SignatureFolder extends VistaBoxFolder<DigitalSignature> {

    private static I18n i18n = I18n.get(SignatureFolder.class);

    public final static String DEFAULT_STYLE_PREFIX = "SignatureFolder";

    public static enum StyleSuffix implements IStyleName {
        DigitalSignature, DigitalSignatureLabel, DigitalSignatureEdit
    }

    public static enum StyleDependent implements IStyleDependent {
        selected, disabled, hover
    }

    public SignatureFolder() {
        super(DigitalSignature.class, false);
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
            inheritContainerAccessRules(false);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel main = new FormFlexPanel();

            int row = -1;
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().tenant().tenant().person().name(), new CEntityLabel<Name>()), 25).build());
            main.setBR(++row, 0, 2);
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().agree(), new CCheckBox()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().fullName(), new CTextField()), 25).build());

            row = 1;
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().ipAddress()), 20).build());
            main.setWidget(++row, 1, new DecoratorBuilder(inject(proto().timestamp()), 20).build());

            main.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.DigitalSignature.name());
            return main;
        }

        @Override
        public void populate(DigitalSignature entity) {
            setEditable(!(entity.agree().isBooleanTrue() && new SignatureValidator().isValid(null, entity.fullName().getValue())));
            super.populate(entity);
        }

        @Override
        public void addValidations() {

            get(proto().agree()).addValueValidator(new EditableValueValidator<Boolean>() {
                @Override
                public boolean isValid(CComponent<Boolean, ?> component, Boolean value) {
                    return value == Boolean.TRUE;
                }

                @Override
                public String getValidationMessage(CComponent<Boolean, ?> component, Boolean value) {
                    return i18n.tr("You Must Agree To The Terms And Conditions To Continue");
                }
            });

            get(proto().fullName()).addValueValidator(new SignatureValidator());

            super.addValidations();
        }

        private class SignatureValidator implements EditableValueValidator<String> {
            @Override
            public boolean isValid(CComponent<String, ?> component, String value) {
                return isSignatureValid(value);
            }

            @Override
            public String getValidationMessage(CComponent<String, ?> component, String value) {
                return i18n.tr("Digital Signature Must Match Your Name On File");
            }

            public boolean isSignatureValid(String signature) {
                if (CommonsStringUtils.isEmpty(signature)) {
                    return false;
                }

                Tenant tenant = getValue().tenant().tenant();
                return isCombinationMatch(signature, tenant.person().name().firstName(), tenant.person().name().lastName(), tenant.person().name().middleName());
            }

            private boolean isCombinationMatch(String signature, IPrimitive<String> value1, IPrimitive<String> value2, IPrimitive<String> value3) {
                signature = signature.trim().toLowerCase().replaceAll("\\s+", " ");
                String s1 = CommonsStringUtils.nvl(value1.getValue()).trim().toLowerCase();
                String s2 = CommonsStringUtils.nvl(value2.getValue()).trim().toLowerCase();
                String s3 = CommonsStringUtils.nvl(value3.getValue()).trim().toLowerCase();
                if ((signature.equals(CommonsStringUtils.nvl_concat(s1, s2, " ")) || (signature.equals(CommonsStringUtils.nvl_concat(s2, s1, " "))))) {
                    return true;
                }
                if ((signature.equals(CommonsStringUtils.nvl_concat(CommonsStringUtils.nvl_concat(s1, s3, " "), s2, " ")))) {
                    return true;
                }
                if ((signature.equals(CommonsStringUtils.nvl_concat(CommonsStringUtils.nvl_concat(s2, s3, " "), s1, " ")))) {
                    return true;
                }
                return false;
            }
        }
    }
}
