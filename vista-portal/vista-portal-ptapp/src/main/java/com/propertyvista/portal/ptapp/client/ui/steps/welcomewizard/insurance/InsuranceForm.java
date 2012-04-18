/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.c.NewPaymentMethodForm;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.LeaseTemsFolder;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.ExistingInsurance;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsuranceDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.PurchaseInsuranceDTO;

public class InsuranceForm extends CEntityDecoratableEditor<InsuranceDTO> {

    private final static I18n i18n = I18n.get(InsuranceForm.class);

    private final static String WHY_WE_NEED_INSURANCE_EXPLANATION = ""
            + "As per your agreement, you must have Tenant Insurance with a minimum liability of $x,xxx,xxx. "
            + "For your convenience, we have teamed up with TenantSure to provide you your Tenant insurance"
            + "Please answer the following Questions in order for TenantSure to provide you with the best insurance rate" + "";

    public InsuranceForm() {
        super(InsuranceDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, new HTML("&nbsp")); // separator
        content.setWidget(++row, 0, inject(proto().purchaseInsurance(), new PurchaseInsuranceForm()));
        get(proto().purchaseInsurance()).setVisible(true);

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().alreadyHaveInsurance())).build());
        get(proto().alreadyHaveInsurance()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().purchaseInsurance()).setVisible(!event.getValue());
                get(proto().existingInsurance()).setVisible(event.getValue());
            }
        });

        content.setWidget(++row, 0, inject(proto().existingInsurance(), new ExistingInsuranceForm()));
        get(proto().existingInsurance()).setVisible(false);

        return content;
    }

    public static class PurchaseInsuranceForm extends CEntityDecoratableEditor<PurchaseInsuranceDTO> {

        public PurchaseInsuranceForm() {
            super(PurchaseInsuranceDTO.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            int row = -1;
            content.setWidget(++row, 0, new HTML(WHY_WE_NEED_INSURANCE_EXPLANATION));
            content.setH1(++row, 0, 1, i18n.tr("Coverage"));
            content.setH2(++row, 0, 1, i18n.tr("Personal Contents"));
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().personalContentsLimit(), new CoverageAmountCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().propertyAwayFromPremises(), new CoverageAmountCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().additionalLivingExpenses(), new CoverageAmountCombo())).build());

            content.setH2(++row, 0, 1, i18n.tr("Decuctible (per Claim)"));
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().deductible(), new DeductibleCombo())).build());

            content.setH2(++row, 0, 1, i18n.tr("Form of Coverage"));
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().formOfCoverage())).build());

            content.setH2(++row, 0, 1, i18n.tr("Special Limites (per Claim)"));
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().jewleryAndFurs(), new LimitCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bicycles(), new LimitCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().personalComputers(), new LimitCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().moneyOrGiftGardsOrGiftCertificates(), new LimitCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().securities(), new LimitCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().utilityTraders(), new LimitCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().spareAutomobileParts(), new LimitCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().coinBanknoteOrStampCollections(), new LimitCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().collectibleCardsandComics(), new LimitCombo())).build());

            content.setH2(++row, 0, 1, i18n.tr("Additional Coverage (per Claim)"));
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().freezerFoodSpoilage(), new CoverageAmountCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().animalsBirdsAndFish(), new CoverageAmountCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().personalLiability(), new CoverageAmountCombo())).build());

            content.setH2(++row, 0, 1, i18n.tr("Coverage Qualification Questions"));
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().homeBuiness(), new HomeBuisnessCombo())).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfPrevClaims())).build());

            content.setH2(++row, 0, 1, i18n.tr("Personal Disclaimer"));
            content.setWidget(++row, 0, inject(proto().personalDisclaimerTerms(), new LeaseTemsFolder(true)));

            // payment 
            content.setWidget(++row, 0, inject(proto().paymentMethod(), new NewPaymentMethodForm()));
            return content;
        }
    }

    public static class ExistingInsuranceForm extends CEntityDecoratableEditor<ExistingInsurance> {

        public ExistingInsuranceForm() {
            super(ExistingInsurance.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            int row = -1;
            content.setH1(++row, 0, 1, i18n.tr("Proof of Insurance"));
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceProvider()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceCertificateNumber()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().personalLiability()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceStartDate()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceExpirationDate()), 10).build());
            content.setH2(++row, 0, 1, i18n.tr("Attach Insurance Certificate"));
            content.setWidget(++row, 0, inject(proto().documents(), new InsuranceUploaderFolder()));

            addValueValidator(new EditableValueValidator<ExistingInsurance>() {

                @Override
                public ValidationFailure isValid(CComponent<ExistingInsurance, ?> component, ExistingInsurance value) {
                    if (!component.isValid()) {
                        return new ValidationFailure(i18n.tr("Valid Proof of Insurance is Required prior to Move-In"));
                    } else {
                        return null;
                    }
                }
            });
            return content;
        }

    }

    public static class CoverageAmountCombo extends CComboBox<String> {

        private static final List<String> COVERAGE_OPTIONS = Arrays.asList(//@formatter:off
                "No Coverage",
                "$2,000 Coverage",
                "$5,000 Coverage",
                "$10,000 Coverage",
                "$1,000,000 Coverage"
        );//@formatter:on

        public CoverageAmountCombo() {
            setOptions(COVERAGE_OPTIONS);
        }
    }

    public static class DeductibleCombo extends CComboBox<String> {

        private static final List<String> DEDUCTIBLE_OPTIONS = Arrays.asList(//@formatter:off
                "$1,000 Deductible",
                "$2,000 Deductible"
        );//@formatter:on

        public DeductibleCombo() {
            setOptions(DEDUCTIBLE_OPTIONS);
        }
    }

    public static class LimitCombo extends CComboBox<String> {

        private static final List<String> LIMITS = Arrays.asList(//@formatter:off
                "$500 Limit",
                "$1,000 Limit"
        );//@formatter:on

        public LimitCombo() {
            setOptions(LIMITS);
        }
    }

    public static class HomeBuisnessCombo extends CComboBox<String> {

        private static final List<String> HOME_BUISNESS_OPTIONS = Arrays.asList(//@formatter:off
                "Karaoke",
                "Hair Salon",
                "Spa"               
        );//@formatter:on

        public HomeBuisnessCombo() {
            setOptions(HOME_BUISNESS_OPTIONS);
        }
    }
}
