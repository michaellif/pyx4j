/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 */
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.validators.ClientBusinessRules;
import com.propertyvista.crm.client.ui.crud.customer.common.components.IdentificationDocumentFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.LegalQuestionFolder;
import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.dto.TenantInfoDTO;

public class TenantInfoViewForm extends CForm<TenantInfoDTO> {

    private static final I18n i18n = I18n.get(TenantInfoViewForm.class);

    private final FormPanel previousAddress = new FormPanel(this) {
        @Override
        public void setVisible(boolean visible) {
            get(proto().version().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    public TenantInfoViewForm() {
        super(TenantInfoDTO.class, new VistaEditorsComponentFactory());

        setEditable(false);
        setViewable(true);
    }

    @Override
    protected IsWidget createContent() {
        InfoViewFormPanel formPanel = new InfoViewFormPanel(this);

        formPanel.append(Location.Dual, proto().person().name(), new NameEditor(i18n.tr("Person")));

        formPanel.append(Location.Left, proto().person().sex()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().person().birthDate()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().person().email()).decorate().componentWidth(300);

        formPanel.append(Location.Right, proto().person().homePhone()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().person().mobilePhone()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().person().workPhone()).decorate().componentWidth(180);

        formPanel.h1(i18n.tr("Identification Documents"));
        formPanel.append(Location.Dual, proto().version().documents(), new IdentificationDocumentFolder());

        formPanel.h1(proto().version().currentAddress().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().version().currentAddress(), new PriorAddressEditor());

        previousAddress.h1(proto().version().previousAddress().getMeta().getCaption());
        previousAddress.append(Location.Dual, proto().version().previousAddress(), new PriorAddressEditor());
        formPanel.append(Location.Dual, previousAddress);

        formPanel.h1(i18n.tr("General Questions"));
        formPanel.append(Location.Dual, proto().version().legalQuestions(), new LegalQuestionFolder());

        if (!SecurityController.check(PortalResidentBehavior.Guarantor)) {
            formPanel.h1(proto().emergencyContacts().getMeta().getCaption());
            formPanel.append(Location.Dual, proto().emergencyContacts(), new EmergencyContactFolder(isEditable()));
        }

        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        previousAddress.setVisible(ClientBusinessRules.needPreviousAddress(getValue().version().currentAddress().moveInDate().getValue(), getValue()
                .yearsToForcingPreviousAddress().getValue()));
    }

    private class InfoViewFormPanel extends FormPanel {

        public InfoViewFormPanel(CForm<?> parent) {
            super(parent);
        }

        void appendLegalQuestion(IObject<?> member) {
            append(Location.Dual, member).decorate().labelWidth(400).labelPosition(LabelPosition.top).useLabelSemicolon(false);
        }
    }
}
