/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.crud.customer.screening;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.customer.common.components.IdentificationDocumentFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.LegalQuestionFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.PersonalAssetFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.PersonalIncomeFolder;
import com.propertyvista.dto.LeaseParticipantScreeningTO;

public class CustomerScreeningForm extends CrmEntityForm<LeaseParticipantScreeningTO> {

    private static final I18n i18n = I18n.get(CustomerScreeningForm.class);

    private final FormPanel previousAddress = new FormPanel(this) {
        @Override
        public void setVisible(boolean visible) {
            get(proto().data().version().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    private final IdentificationDocumentFolder fileUpload = new IdentificationDocumentFolder();

    public CustomerScreeningForm(IPrimeFormView<LeaseParticipantScreeningTO, ?> view) {
        super(LeaseParticipantScreeningTO.class, view);

        selectTab(addTab(createIdentificationDocumentsTab(), i18n.tr("Identification")));
        addTab(createAddressesTab(), i18n.tr("Addresses"));
        addTab(createlegalQuestionsTab(), i18n.tr("General Questions"));
        addTab(createIncomesTab(), i18n.tr("Incomes"));
        addTab(createAssetsTab(), i18n.tr("Assets"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        previousAddress.setVisible(!getValue().data().version().previousAddress().isEmpty());
    }

    private IsWidget createIdentificationDocumentsTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().data().version().documents(), fileUpload);
        return formPanel;
    }

    private IsWidget createAddressesTab() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(proto().data().version().currentAddress().getMeta().getCaption());
        formPanel.append(Location.Dual, inject(proto().data().version().currentAddress(), new PriorAddressEditor()));

        previousAddress.h1(proto().data().version().previousAddress().getMeta().getCaption());
        previousAddress.append(Location.Dual, proto().data().version().previousAddress(), new PriorAddressEditor(true));
        formPanel.append(Location.Dual, previousAddress);

        return formPanel;
    }

    private IsWidget createlegalQuestionsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().data().version().legalQuestions(), new LegalQuestionFolder());

        return formPanel;
    }

// Financial: ------------------------------------------------------------------------------------------------

    private IsWidget createIncomesTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().data().version().incomes(), new PersonalIncomeFolder(isEditable()));

        return formPanel;
    }

    private IsWidget createAssetsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().data().version().assets(), new PersonalAssetFolder(isEditable()));

        return formPanel;
    }
}