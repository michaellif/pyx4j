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
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.screening;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.validators.ClientBusinessRules;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateWithinPeriodValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.customer.common.components.IdUploaderFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.PersonalAssetFolder;
import com.propertyvista.crm.client.ui.crud.customer.common.components.PersonalIncomeFolder;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.misc.VistaTODO;

public class CustomerScreeningForm extends CrmEntityForm<LeaseParticipantScreeningTO> {

    private static final I18n i18n = I18n.get(CustomerScreeningForm.class);

    private final FormPanel previousAddress = new FormPanel(this) {
        @Override
        public void setVisible(boolean visible) {
// this will clean previous address:
//            if (visible != get(proto().screening().version().previousAddress()).isVisible()) {
//                get(proto().screening().version().previousAddress()).reset();
//            }
            get(proto().data().version().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    private final IdUploaderFolder fileUpload = new IdUploaderFolder();

    public CustomerScreeningForm(IPrimeFormView<LeaseParticipantScreeningTO, ?> view) {
        super(LeaseParticipantScreeningTO.class, view);

        selectTab(addTab(createIdentificationDocumentsTab(), i18n.tr("Identification")));
        addTab(createAddressesTab(), i18n.tr("Addresses"));
        addTab(createlegalQuestionsTab(), proto().data().version().legalQuestions().getMeta().getCaption());
        addTab(createIncomesTab(), i18n.tr("Incomes"));
        addTab(createAssetsTab(), i18n.tr("Assets"));
    }

    private IEntity getPolicyEntity() {
        if (getValue().getPrimaryKey() == null) {
            return getValue().leaseParticipantId();
        } else {
            return getValue();
        }
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            fileUpload.setPolicyEntity(getPolicyEntity());

            ((PersonalIncomeFolder) (CComponent<?, ?, ?, ?>) get(proto().data().version().incomes())).setPolicyEntity(getPolicyEntity());
            ((PersonalAssetFolder) (CComponent<?, ?, ?, ?>) get(proto().data().version().assets())).setPolicyEntity(getValue());
        }

        enablePreviousAddress();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        // ------------------------------------------------------------------------------------------------
        @SuppressWarnings("unchecked")
        CForm<PriorAddress> currentAF = ((CForm<PriorAddress>) get(proto().data().version().currentAddress()));

        currentAF.get(currentAF.proto().moveInDate()).addComponentValidator(new PastDateIncludeTodayValidator());
        currentAF.get(currentAF.proto().moveOutDate()).addComponentValidator(new FutureDateIncludeTodayValidator());
        currentAF.get(currentAF.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        // ------------------------------------------------------------------------------------------------
        @SuppressWarnings("unchecked")
        CForm<PriorAddress> previousAF = ((CForm<PriorAddress>) get(proto().data().version().previousAddress()));

        previousAF.get(previousAF.proto().moveInDate()).addComponentValidator(new PastDateValidator());
        previousAF.get(previousAF.proto().moveOutDate()).addComponentValidator(new PastDateValidator());

        // ------------------------------------------------------------------------------------------------
        new StartEndDateWithinPeriodValidation(previousAF.get(previousAF.proto().moveOutDate()), currentAF.get(currentAF.proto().moveInDate()), 1, 0,
                i18n.tr("Current Move In date should be within 1 month of previous Move Out date"));
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
        QuestionsFormPanel formPanel = new QuestionsFormPanel(this);
        formPanel.appendQuestion(proto().data().version().legalQuestions().suedForRent());
        formPanel.appendQuestion(proto().data().version().legalQuestions().suedForDamages());
        formPanel.appendQuestion(proto().data().version().legalQuestions().everEvicted());
        formPanel.appendQuestion(proto().data().version().legalQuestions().defaultedOnLease());
        formPanel.appendQuestion(proto().data().version().legalQuestions().convictedOfFelony());
        formPanel.appendQuestion(proto().data().version().legalQuestions().legalTroubles());
        formPanel.appendQuestion(proto().data().version().legalQuestions().filedBankruptcy());

        if (VistaTODO.VISTA_4498_Remove_Unnecessary_Validation_Screening_CRM) {
            get(proto().data().version().legalQuestions().suedForRent()).setMandatory(false);
            get(proto().data().version().legalQuestions().suedForDamages()).setMandatory(false);
            get(proto().data().version().legalQuestions().everEvicted()).setMandatory(false);
            get(proto().data().version().legalQuestions().defaultedOnLease()).setMandatory(false);
            get(proto().data().version().legalQuestions().convictedOfFelony()).setMandatory(false);
            get(proto().data().version().legalQuestions().legalTroubles()).setMandatory(false);
            get(proto().data().version().legalQuestions().filedBankruptcy()).setMandatory(false);
        }

        return formPanel;
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(ClientBusinessRules.needPreviousAddress(getValue().data().version().currentAddress().moveInDate().getValue(), getValue()
                .yearsToForcingPreviousAddress().getValue()));
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

    class QuestionsFormPanel extends FormPanel {

        public QuestionsFormPanel(CForm<?> parent) {
            super(parent);
        }

        public void appendQuestion(IObject<?> member) {
            append(Location.Dual, member).decorate().labelWidth(400).labelPosition(LabelPosition.top).useLabelSemicolon(false);
        }
    };
}