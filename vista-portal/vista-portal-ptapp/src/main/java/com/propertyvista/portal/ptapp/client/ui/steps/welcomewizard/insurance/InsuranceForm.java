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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.shared.IObject;
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
            + "For your convenience, we have teamed up with TenantSure to provide you your Tenant insurance. "
            + "Please answer the following Questions in order for TenantSure to provide you with the best insurance rate.";

    private final static Integer[] PERSONAL_CONTENTS_LIMITS_OPTIONS = { 10000, 12000, 14000, 16000, 18000, 20000, 22000, 24000, 26000, 28000, 30000, 35000,
            40000, 50000, 60000 };

    private final static Integer[] PROPERTY_AWAY_FROM_PREMISES_OPTIONS = { 0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000 };

    private final static Integer[] ADDITIONAL_LIVING_EXPENSES_OPTIONS = { 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 12000, 14000, 16000, 18000,
            20000, 22000, 24000, 26000, 28000, 30000 };

    private final static Integer[] JEWLERY_AND_FURS_OPTIONS = { 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000 };

    private final static Integer[] BYCICLES_OPTIONS = { 500, 1000, 1500, 2000, 2500, 3000 };

    private final static Integer[] PERSONAL_COMPUTERS_OPTIONS = { 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000 };

    private final static Integer[] MONEY_GIFT_CARDS_AND_CERTIFICATES_OPTIONS = { 500 };

    private final static Integer[] SECURITIES_OPTIONS = { 500 };

    private final static Integer[] DEFAULT_LIMITIES_OPTIONS = { 500, 1000, 1500, 2000, 2500, 3000 };

    public InsuranceForm() {
        super(InsuranceDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, new HTML("&nbsp")); // separator
        content.setWidget(++row, 0, inject(proto().purchaseInsurance(), new PurchaseInsuranceForm()));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().alreadyHaveInsurance())).build());
        content.setWidget(++row, 0, inject(proto().existingInsurance(), new ExistingInsuranceForm()));

        return content;
    }

    public static String formatMoney(Integer value) {
        assert value >= 0;
        if (value == 0) {
            return "No";
        } else {
            StringBuilder formatted = new StringBuilder();
            formatted.append('$');
            String stringValue = String.valueOf(value);
            for (int i = 0; i < stringValue.length(); ++i) {
                if (i != 0 & ((stringValue.length() - i) % 3 == 0)) {
                    formatted.append(",");
                }
                formatted.append(stringValue.charAt(i));
            }
            return formatted.toString();
        }
    }

    public static String formatMoney(BigDecimal money) {
        assert money.compareTo(BigDecimal.ZERO) >= 0;
        if (money.equals(BigDecimal.ZERO)) {
            return "No";
        } else {
            String[] stringValue = money.toPlainString().split("\\.");
            StringBuilder formatted = new StringBuilder();
            formatted.append("$");
            for (int a = 0; a < stringValue.length; ++a) {
                for (int i = 0; i < stringValue[a].length(); ++i) {
                    if (((stringValue[a].length() - i) % 3 == 0) & i != 0) {
                        formatted.append(",");
                    }
                    formatted.append(stringValue[a].charAt(i));
                }
                if (a == 0) {
                    formatted.append(".");
                }
            }
            return formatted.toString();
        }
    }

    public static class PurchaseInsuranceForm extends CEntityDecoratableEditor<PurchaseInsuranceDTO> {

        public PurchaseInsuranceForm() {
            super(PurchaseInsuranceDTO.class);
        }

        @Override
        public IsWidget createContent() {

            FormFlexPanel content = new FormFlexPanel();

            FormFlexPanel insuranceTerms = new FormFlexPanel();
            int row = -1;
            insuranceTerms.setWidget(++row, 0, new HTML(WHY_WE_NEED_INSURANCE_EXPLANATION));
            insuranceTerms.setH1(++row, 0, 1, i18n.tr("Coverage"));
            insuranceTerms.setH2(++row, 0, 1, i18n.tr("Personal Contents"));
            insuranceTerms.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().personalContentsLimit(), new CoverageAmountCombo(PERSONAL_CONTENTS_LIMITS_OPTIONS))).build());
            insuranceTerms.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().propertyAwayFromPremises(), new CoverageAmountCombo(PROPERTY_AWAY_FROM_PREMISES_OPTIONS))).build());
            insuranceTerms.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().additionalLivingExpenses(), new CoverageAmountCombo(ADDITIONAL_LIVING_EXPENSES_OPTIONS))).build());

            insuranceTerms.setH2(++row, 0, 1, i18n.tr("Decuctible (per Claim)"));
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().deductible(), new DeductibleCombo())).build());

            insuranceTerms.setH2(++row, 0, 1, i18n.tr("Form of Coverage"));
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().formOfCoverage())).build());

            insuranceTerms.setH2(++row, 0, 1, i18n.tr("Special Limites (per Claim)"));
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().jewleryAndFurs(), new LimitCombo(JEWLERY_AND_FURS_OPTIONS))).build());
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bicycles(), new LimitCombo(BYCICLES_OPTIONS))).build());
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().personalComputers(), new LimitCombo(PERSONAL_COMPUTERS_OPTIONS))).build());
            insuranceTerms.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().moneyOrGiftGardsOrGiftCertificates(), new LimitCombo(MONEY_GIFT_CARDS_AND_CERTIFICATES_OPTIONS)))
                            .build());
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().securities(), new LimitCombo(SECURITIES_OPTIONS))).build());
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().utilityTraders(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().spareAutomobileParts(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
            insuranceTerms.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().coinBanknoteOrStampCollections(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
            insuranceTerms.setWidget(++row, 0,
                    new DecoratorBuilder(inject(proto().collectibleCardsandComics(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());

            insuranceTerms.setH2(++row, 0, 1, i18n.tr("Additional Coverage (per Claim)"));
            insuranceTerms
                    .setWidget(++row, 0, new DecoratorBuilder(inject(proto().freezerFoodSpoilage(), new CoverageAmountCombo(0, 500, 1000, 1500))).build());
            insuranceTerms
                    .setWidget(++row, 0, new DecoratorBuilder(inject(proto().animalsBirdsAndFish(), new CoverageAmountCombo(0, 500, 1000, 1500))).build());
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().personalLiability(), new CoverageAmountCombo(500000, 1000000))).build());

            insuranceTerms.setH2(++row, 0, 1, i18n.tr("Coverage Qualification Questions"));
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().homeBuiness(), new HomeBuisnessCombo())).build());
            insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfPrevClaims())).build());

            insuranceTerms.setH2(++row, 0, 1, i18n.tr("Personal Disclaimer"));
            insuranceTerms.setWidget(++row, 0, inject(proto().personalDisclaimerTerms(), new LeaseTemsFolder(true)));

            // payment 
            insuranceTerms.setWidget(++row, 0, inject(proto().paymentMethod(), new NewPaymentMethodForm()));

            content.setWidget(0, 0, new ScrollPanel(insuranceTerms));
            content.getFlexCellFormatter().setWidth(0, 0, "50%");

            VerticalPanel quoteTotalPanel = new VerticalPanel();
            quoteTotalPanel.setWidth("20em");
            quoteTotalPanel.getElement().getStyle().setPosition(Position.FIXED);
            quoteTotalPanel.getElement().getStyle().setTop(50, Unit.PCT);
            quoteTotalPanel.getElement().getStyle().setRight(0, Unit.PCT);
            quoteTotalPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");

            {
                quoteTotalPanel.add(new DecoratorBuilder(inject(proto().monthlyInsurancePremium()), 10).build());
                get(proto().monthlyInsurancePremium()).setViewable(true);
                quoteTotalPanel.add(new DecoratorBuilder(inject(proto().totalCoverage()), 10).build());
                get(proto().totalCoverage()).setViewable(true);
                quoteTotalPanel.add(new DecoratorBuilder(inject(proto().totalPersonalLiability()), 10).build());
                get(proto().totalPersonalLiability()).setViewable(true);
            }
            content.setWidget(0, 1, quoteTotalPanel);

            return content;
        }

        private BigDecimal premium() {
            BigDecimal a = decValueOf(proto().personalContentsLimit()).add(decValueOf(proto().propertyAwayFromPremises()))
                    .add(decValueOf(proto().additionalLivingExpenses())).divide(new BigDecimal(20000));
            BigDecimal normalizedDeductible = new BigDecimal("3000").subtract(decValueOf(proto().deductible())).divide(new BigDecimal("500"));
            BigDecimal permium = new BigDecimal("5").add(a).add(normalizedDeductible);
            return permium;
        }

        private int totalCoverage() {
            int[] coverageItems = new int[] {//@formatter:off
                    intValueOf(proto().personalContentsLimit()),
                    intValueOf(proto().propertyAwayFromPremises()),
                    intValueOf(proto().additionalLivingExpenses())
            };//@formatter:on

            int total = 0;
            for (int value : coverageItems) {
                total += value;
            }
            return total;
        }

        private ValueLabelCombo cast(CComponent<?, ?> cmp) {
            return (ValueLabelCombo) cmp;
        }

        private int intValueOf(IObject<?> object) {
            ValueLabelWrapper wrapper = cast(get(object)).getValue();
            return wrapper != null ? wrapper.getValue() : 0;
        }

        private BigDecimal decValueOf(IObject<?> object) {
            return new BigDecimal(intValueOf(object));
        }

        private void recalculateSummary() {
            get(proto().monthlyInsurancePremium()).setValue(formatMoney(premium()));
            get(proto().totalCoverage()).setValue(formatMoney(totalCoverage()));
            get(proto().totalPersonalLiability()).setValue(formatMoney(intValueOf(proto().personalLiability())));
        }

        private ValueChangeHandler<InsuranceForm.ValueLabelWrapper> summaryRecalculationRequired() {
            return new ValueChangeHandler<InsuranceForm.ValueLabelWrapper>() {
                @Override
                public void onValueChange(ValueChangeEvent<ValueLabelWrapper> event) {
                    recalculateSummary();
                }
            };
        }

        public class CoverageAmountCombo extends ValueLabelCombo {
            public CoverageAmountCombo(Integer... values) {
                super("Coverage", values);
                addValueChangeHandler(summaryRecalculationRequired());
            }
        }

        public class DeductibleCombo extends ValueLabelCombo {

            public DeductibleCombo() {
                super("Decuctible", 500, 1000, 2000);
                addValueChangeHandler(summaryRecalculationRequired());
            }
        }

        public class LimitCombo extends ValueLabelCombo {

            public LimitCombo(Integer... options) {
                super("Limit", options);
                addValueChangeHandler(summaryRecalculationRequired());
            }
        }

        public class HomeBuisnessCombo extends CComboBox<String> {

            private final List<String> HOME_BUISNESS_OPTIONS = Arrays.asList(//@formatter:off
                    "Karaoke",
                    "Hair Salon",
                    "Spa",
                    "Bar",
                    "Gym"
            );//@formatter:on

            public HomeBuisnessCombo() {
                setOptions(HOME_BUISNESS_OPTIONS);
            }
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

    public static class ValueLabelWrapper {

        private final Integer value;

        private final String label;

        public ValueLabelWrapper(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        public Integer getValue() {
            return value;
        }

        @Override
        public String toString() {
            return formatMoney(value) + " " + label;
        }

    }

    public static class ValueLabelCombo extends CComboBox<ValueLabelWrapper> {

        public ValueLabelCombo(String label, Integer... options) {
            List<ValueLabelWrapper> wrappedOptions = new ArrayList<ValueLabelWrapper>(options.length);
            for (Integer value : options) {
                wrappedOptions.add(new ValueLabelWrapper(value, label));
            }
            setOptions(wrappedOptions);
        }
    }
}
