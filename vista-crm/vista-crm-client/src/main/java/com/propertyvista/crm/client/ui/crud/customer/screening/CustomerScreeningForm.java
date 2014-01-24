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

import java.util.Calendar;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.ui.components.editors.PriorAddressEditor;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
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
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.misc.BusinessRules;

public class CustomerScreeningForm extends CrmEntityForm<CustomerScreening> {

    private static final I18n i18n = I18n.get(CustomerScreeningForm.class);

    private final TwoColumnFlexFormPanel previousAddress = new TwoColumnFlexFormPanel() {
        @Override
        public void setVisible(boolean visible) {
            get(proto().version().previousAddress()).setVisible(visible);
            super.setVisible(visible);
        }
    };

    private IdUploaderFolder fileUpload;

    public CustomerScreeningForm(IForm<CustomerScreening> view) {
        super(CustomerScreening.class, view);

        Tab tab = addTab(createIdentificationDocumentsTab(i18n.tr("Identification Documents")));
        selectTab(tab);

        addTab(createAddressesTab(i18n.tr("Addresses")));
        addTab(createlegalQuestionsTab(proto().version().legalQuestions().getMeta().getCaption()));
        addTab(createIncomesTab(i18n.tr("Incomes")));
        addTab(createAssetsTab(i18n.tr("Assets")));

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            fileUpload.setParentEntity(getValue());
        }

        enablePreviousAddress();
    }

    @Override
    public void addValidations() {
        super.addValidations();

        // ------------------------------------------------------------------------------------------------
        CEntityForm<PriorAddress> currentAF = ((CEntityForm<PriorAddress>) get(proto().version().currentAddress()));

        currentAF.get(currentAF.proto().moveInDate()).addValueValidator(new PastDateIncludeTodayValidator());
        currentAF.get(currentAF.proto().moveOutDate()).addValueValidator(new FutureDateIncludeTodayValidator());
        new StartEndDateValidation(currentAF.get(currentAF.proto().moveInDate()), currentAF.get(currentAF.proto().moveOutDate()),
                i18n.tr("Move In date must be before Move Out date"));

        currentAF.get(currentAF.proto().moveInDate()).addValueChangeHandler(new ValueChangeHandler<LogicalDate>() {
            @Override
            public void onValueChange(ValueChangeEvent<LogicalDate> event) {
                enablePreviousAddress();
            }
        });

        // ------------------------------------------------------------------------------------------------
        CEntityForm<PriorAddress> previousAF = ((CEntityForm<PriorAddress>) get(proto().version().previousAddress()));

        previousAF.get(previousAF.proto().moveInDate()).addValueValidator(new PastDateValidator());
        previousAF.get(previousAF.proto().moveOutDate()).addValueValidator(new PastDateValidator());
        new StartEndDateValidation(previousAF.get(previousAF.proto().moveInDate()), previousAF.get(previousAF.proto().moveOutDate()),
                i18n.tr("Move In date must be before Move Out date"));

        // ------------------------------------------------------------------------------------------------
        new StartEndDateWithinPeriodValidation(previousAF.get(previousAF.proto().moveOutDate()), currentAF.get(currentAF.proto().moveInDate()), Calendar.MONTH,
                1, i18n.tr("Current Move In date should be within 1 month of previous Move Out date"));
    }

    private TwoColumnFlexFormPanel createIdentificationDocumentsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setWidget(++row, 0, 2, inject(proto().version().documents(), fileUpload = new IdUploaderFolder()));

        return main;
    }

    private TwoColumnFlexFormPanel createAddressesTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = -1;
        main.setH1(++row, 0, 2, proto().version().currentAddress().getMeta().getCaption());
        main.setWidget(++row, 0, 2, inject(proto().version().currentAddress(), new PriorAddressEditor()));

        previousAddress.setH1(0, 0, 2, proto().version().previousAddress().getMeta().getCaption());
        previousAddress.setWidget(1, 0, 2, inject(proto().version().previousAddress(), new PriorAddressEditor()));
        main.setWidget(++row, 0, 2, previousAddress);

        return main;
    }

    private TwoColumnFlexFormPanel createlegalQuestionsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        int row = 0;
        main.setWidget(row++, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().suedForRent())));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().suedForDamages())));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().everEvicted())));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().defaultedOnLease())));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().convictedOfFelony())));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().legalTroubles())));
        main.setHR(row++, 0, 2);
        main.setWidget(row++, 0, 2, decorateLegalQuestion(inject(proto().version().legalQuestions().filedBankruptcy())));

        return main;
    }

    private WidgetDecorator decorateLegalQuestion(CComponent<?> comp) {
        return new FormDecoratorBuilder(comp, 60, 10, 20).labelAlignment(Alignment.left).useLabelSemicolon(false).build();
    }

    private void enablePreviousAddress() {
        previousAddress.setVisible(BusinessRules.infoPageNeedPreviousAddress(getValue().version().currentAddress().moveInDate().getValue()));
    }

// Financial: ------------------------------------------------------------------------------------------------

    private TwoColumnFlexFormPanel createIncomesTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        main.setWidget(0, 0, 2, inject(proto().version().incomes(), new PersonalIncomeFolder(isEditable())));

        return main;
    }

    private TwoColumnFlexFormPanel createAssetsTab(String title) {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(title);

        main.setWidget(0, 0, 2, inject(proto().version().assets(), new PersonalAssetFolder(isEditable())));

        return main;
    }
}