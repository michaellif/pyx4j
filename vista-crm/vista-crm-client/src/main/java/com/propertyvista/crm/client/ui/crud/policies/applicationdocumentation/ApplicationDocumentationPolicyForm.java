/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.applicationdocumentation;

import java.util.EnumSet;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.IdentificationDocumentType;

public class ApplicationDocumentationPolicyForm extends PolicyDTOTabPanelBasedForm<ApplicationDocumentationPolicyDTO> {

    private static final I18n i18n = I18n.get(ApplicationDocumentationPolicyForm.class);

    public ApplicationDocumentationPolicyForm(IForm<ApplicationDocumentationPolicyDTO> view) {
        super(ApplicationDocumentationPolicyDTO.class, view);

        addTab(createIdentificationDocsTab(), i18n.tr("Required IDs"));
        addTab(createFinancialDocsTab(), i18n.tr("Financial Docs"));
    }

    private IsWidget createIdentificationDocsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().numberOfRequiredIDs()).decorate().labelWidth(200).componentWidth(50);

        formPanel.h3(proto().allowedIDs().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().allowedIDs(), new IdentificationDocumentFolder());

        return formPanel;
    }

    private IsWidget createFinancialDocsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().mandatoryProofOfIncome()).decorate().labelWidth(200).componentWidth(110);

//        formPanel.h3(proto().allowedFinancialDocs().getMeta().getCaption());
//        formPanel.append(Location.Dual, proto().allowedIDs(), new IdentificationDocumentFolder());

        return formPanel;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().numberOfRequiredIDs()).addComponentValidator(new AbstractComponentValidator<Integer>() {
            @Override
            public BasicValidationError isValid() {
                if (getCComponent().getValue() == null || getCComponent().getValue() == 0) {
                    return new BasicValidationError(getCComponent(), i18n.tr("At least one ID is required"));
                } else if (getValue() != null && (getValue().allowedIDs().isEmpty() || getCComponent().getValue() > getValue().allowedIDs().size())) {
                    return new BasicValidationError(getCComponent(), i18n.tr("The number of required IDs must not exceed the number of allowed IDs"));
                } else {
                    return null;
                }
            }
        });

        get(proto().allowedIDs()).addValueChangeHandler(new RevalidationTrigger<List<IdentificationDocumentType>>(get(proto().numberOfRequiredIDs())));
    }

    private class IdentificationDocumentFolder extends VistaBoxFolder<IdentificationDocumentType> {

        public IdentificationDocumentFolder() {
            super(IdentificationDocumentType.class);
        }

        @Override
        protected void addItem() {
            new SelectEnumDialog<IdentificationDocumentType.Type>(i18n.tr("Select Document Type"), EnumSet.allOf(IdentificationDocumentType.Type.class)) {
                @Override
                public boolean onClickOk() {
                    IdentificationDocumentType item = EntityFactory.create(IdentificationDocumentType.class);

                    item.type().setValue(getSelectedType());
                    item.name().setValue(getSelectedType().toString());

                    addItem(item);
                    return true;
                }
            }.show();
        }

        @Override
        protected CForm<? extends IdentificationDocumentType> createItemForm(IObject<?> member) {
            return new CForm<IdentificationDocumentType>(IdentificationDocumentType.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel formPanel = new FormPanel(this);

                    formPanel.append(Location.Left, proto().type()).decorate();
                    formPanel.append(Location.Right, proto().importance()).decorate();

                    formPanel.append(Location.Dual, proto().name()).decorate();
                    formPanel.append(Location.Dual, proto().notes()).decorate();

                    return formPanel;
                }

                @Override
                protected void onValueSet(boolean populate) {
                    super.onValueSet(populate);

                    get(proto().notes()).setVisible(isEditable() || !getValue().notes().isNull());
                }
            };
        }
    }
}
