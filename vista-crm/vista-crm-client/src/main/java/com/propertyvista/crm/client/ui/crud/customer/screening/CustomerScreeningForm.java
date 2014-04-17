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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.validators.FutureDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.common.client.ui.validators.PastDateValidator;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.common.client.ui.validators.StartEndDateWithinPeriodValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.lease.application.components.IdUploaderFolder;
import com.propertyvista.crm.client.ui.crud.lease.application.components.PersonalAssetFolder;
import com.propertyvista.crm.client.ui.crud.lease.application.components.PersonalIncomeFolder;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.dto.LeaseParticipantScreeningTO;
import com.propertyvista.misc.BusinessRules;

public class CustomerScreeningForm extends CrmEntityForm<LeaseParticipantScreeningTO> {

    private static final I18n i18n = I18n.get(CustomerScreeningForm.class);

    private final TwoColumnFlexFormPanel previousAddress = new TwoColumnFlexFormPanel() {
        @Override
        public void setVisible(boolean visible) {
            get(proto().screening().version().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    private final IdUploaderFolder fileUpload = new IdUploaderFolder();

    public CustomerScreeningForm(IForm<LeaseParticipantScreeningTO> view) {
        super(LeaseParticipantScreeningTO.class, view);

        Tab tab = addTab(createIdentificationDocumentsTab(i18n.tr("Identification Documents")));
        selectTab(tab);

        addTab(createAddressesTab(i18n.tr("Addresses")));
        addTab(createlegalQuestionsTab(proto().screening().version().legalQuestions().getMeta().getCaption()));
        addTab(createIncomesTab(i18n.tr("Incomes")));
        addTab(createAssetsTab(i18n.tr("Assets")));

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
            ((PersonalIncomeFolder) (CComponent<?, ?>) get(proto().screening().version().incomes())).setPolicyEntity(getPolicyEntity());
        }

        enablePreviousAddress();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        // ------------------------------------------------------------------------------------------------
        CEntityForm<PriorAddress> currentAF = ((CEntityForm<PriorAddress>) get(proto().screening().version().currentAddress()));

        currentAF.get(currentAF.proto().moveInDate()).addComponentValidator(new PastDateIncludeTodayValidator());
        currentAF.get(currentAF.proto().moveOutDate()).addComponentValidator(new FutureDateIncludeTodayValidator());
        new StartEndDateValidation(currentAF.get(currentAF.proto().moveInDate()), currentAF.get(currentAF.proto().moveOutDate()),
                i18n.tr("Move In date must be before Move Out date"));

        currentAF.get(currentAF.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        // ------------------------------------------------------------------------------------------------
        CEntityForm<PriorAddress> previousAF = ((CEntityForm<PriorAddress>) get(proto().screening().version().previousAddress()));

        previousAF.get(previousAF.proto().moveInDate()).addComponentValidator(new PastDateValidator());
        previousAF.get(previousAF.proto().moveOutDate()).addComponentValidator(new PastDateValidator());
        new StartEndDateValidation(previousAF.get(previousAF.proto().moveInDate()), previousAF.get(previousAF.proto().moveOutDate()),
                i18n.tr("Move In date must be before Move Out date"));

        // ------------------------------------------------------------------------------------------------
        new StartEndDateWithinPeriodValidation(previousAF.get(previousAF.proto().moveOutDate()), currentAF.get(currentAF.proto().moveInDate()), 1, 0,
                i18n.tr("Current Move In date should be within 1 month of previous Move Out date"));
    }

    private TwoColumnFlexFormPanel createIdentificationDocumentsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setWidget(++row, 0, 2, inject(proto().screening().version().documents(), fileUpload));

        return main;
    }

    private TwoColumnFlexFormPanel createAddressesTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setH1(++row, 0, 2, proto().screening().version().currentAddress().getMeta().getCaption());
        main.setWidget(++row, 0, 2, inject(proto().screening().version().currentAddress(), new PriorAddressEditor()));

        previousAddress.setH1(0, 0, 2, proto().screening().version().previousAddress().getMeta().getCaption());
        previousAddress.setWidget(1, 0, 2, inject(proto().screening().version().previousAddress(), new PriorAddressEditor()));
        main.setWidget(++row, 0, 2, previousAddress);

        return main;
    }

    private TwoColumnFlexFormPanel createlegalQuestionsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = 0;
        main.setWidget(row++, 0, 2, inject(proto().screening().version().legalQuestions().suedForRent(), legalQuestionDecorator()));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, inject(proto().screening().version().legalQuestions().suedForDamages(), legalQuestionDecorator()));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, inject(proto().screening().version().legalQuestions().everEvicted(), legalQuestionDecorator()));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, inject(proto().screening().version().legalQuestions().defaultedOnLease(), legalQuestionDecorator()));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, inject(proto().screening().version().legalQuestions().convictedOfFelony(), legalQuestionDecorator()));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, inject(proto().screening().version().legalQuestions().legalTroubles(), legalQuestionDecorator()));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, inject(proto().screening().version().legalQuestions().filedBankruptcy(), legalQuestionDecorator()));

        return main;
    }

    private FieldDecorator legalQuestionDecorator() {
        return new FieldDecoratorBuilder(60, 10, 20).labelAlignment(Alignment.left).useLabelSemicolon(false).build();
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(BusinessRules.infoPageNeedPreviousAddress(getValue().screening().version().currentAddress().moveInDate().getValue()));
    }

// Financial: ------------------------------------------------------------------------------------------------

    private TwoColumnFlexFormPanel createIncomesTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        main.setWidget(0, 0, 2, inject(proto().screening().version().incomes(), new PersonalIncomeFolder(isEditable())));

        return main;
    }

    private TwoColumnFlexFormPanel createAssetsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        main.setWidget(0, 0, 2, inject(proto().screening().version().assets(), new PersonalAssetFolder(isEditable())));

        return main;
    }
}