/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.screening;

import java.util.Date;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.domain.tenant.income.TenantGuarantor;
import com.propertyvista.domain.util.ValidationUtils;

class TenantGuarantorFolder extends VistaBoxFolder<TenantGuarantor> {

    public TenantGuarantorFolder(boolean modifyable) {
        super(TenantGuarantor.class, modifyable);
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof TenantGuarantor) {
            return new TenantGuarantorEditor();
        }
        return super.create(member);
    }

    class TenantGuarantorEditor extends CEntityEditor<TenantGuarantor> {

        protected I18n i18n = I18n.get(TenantGuarantorEditor.class);

        public TenantGuarantorEditor() {
            super(TenantGuarantor.class);
        }

        @Override
        public IsWidget createContent() {
            VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(!isEditable());
            if (isEditable()) {
                main.add(inject(proto().name().namePrefix()), 4);
                main.add(inject(proto().name().firstName()), 12);
                main.add(inject(proto().name().middleName()), 12);
                main.add(inject(proto().name().lastName()), 20);
                main.add(inject(proto().name().nameSuffix()), 4);
            } else {
                main.add(inject(proto().name(), new CEntityLabel()), 25, "Guarantor");
                get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            }
            main.add(inject(proto().homePhone()), 15);
            main.add(inject(proto().mobilePhone()), 15);
            main.add(inject(proto().workPhone()), 15);
            main.add(inject(proto().birthDate()), 8);
            main.add(inject(proto().email()), 15);
            return main;
        }

        @Override
        public void addValidations() {

            get(proto().email()).setMandatory(true);

            get(proto().birthDate()).addValueValidator(new EditableValueValidator<Date>() {

                @Override
                public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                    return ValidationUtils.isOlderThen18(value);
                }

                @Override
                public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                    return i18n.tr("Guarantor should be at least 18 years old");
                }
            });
        }
    }
}