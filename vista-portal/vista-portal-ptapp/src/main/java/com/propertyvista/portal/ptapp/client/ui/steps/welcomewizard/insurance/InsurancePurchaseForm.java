/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance;

import static com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components.Utils.asBigDecimals;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.SignatureFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components.FormattableCombo;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components.MoneyLabeledCombo;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components.MultiDisclosurePanel;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components.Utils;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.reviewlease.LeaseTermsFolder;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.PurchaseInsuranceDTO;

public class InsurancePurchaseForm extends CEntityDecoratableForm<PurchaseInsuranceDTO> {

    private final static BigDecimal[] PERSONAL_CONTENTS_COVERAGE_OPTIONS = asBigDecimals(//@formatter:off
            10000,
            20000,
            30000,
            40000,
            50000,
            60000
    );//@formatter:on

    private final static BigDecimal[] PROPERTY_AWAY_FROM_PREMISES_OPTIONS = asBigDecimals(0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000);

    private final static BigDecimal[] ADDITIONAL_LIVING_EXPENSES_OPTIONS = asBigDecimals(2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 12000, 14000,
            16000, 18000, 20000, 22000, 24000, 26000, 28000, 30000, 35000, 40000);

    private final static BigDecimal[] JEWLERY_AND_FURS_OPTIONS = asBigDecimals(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000);

    private final static BigDecimal[] BYCICLES_OPTIONS = asBigDecimals(500, 1000, 1500, 2000, 2500, 3000);

    private final static BigDecimal[] PERSONAL_COMPUTERS_OPTIONS = asBigDecimals(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000);

    private final static BigDecimal[] MONEY_GIFT_CARDS_AND_CERTIFICATES_OPTIONS = asBigDecimals(500);

    private final static BigDecimal[] SECURITIES_OPTIONS = asBigDecimals(500);

    private final static BigDecimal[] DEFAULT_LIMITIES_OPTIONS = asBigDecimals(500, 1000, 1500, 2000, 2500, 3000);

    private final static I18n i18n = I18n.get(InsurancePurchaseForm.class);

    private VerticalPanel quoteTotalPanel;

    private final ValueChangeHandler<?> summaryRecalculationRequiredHandler;

    private FormFlexPanel coverageTerms;

    private final Command onPurchaseConfirmed;

    public InsurancePurchaseForm(Command onPurchaseConfirmed) {
        super(PurchaseInsuranceDTO.class);
        this.summaryRecalculationRequiredHandler = new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                recalculateSummary();
            }
        };
        this.onPurchaseConfirmed = onPurchaseConfirmed;
    }

    public void setQuoteTotalPanelVisibility(boolean isVisible) {
        quoteTotalPanel.setVisible(isVisible);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        content.getColumnFormatter().setWidth(0, "70%");
        content.getColumnFormatter().setWidth(1, "50%");

        MultiDisclosurePanel sections = new MultiDisclosurePanel();

        sections.add(createConverageTermsPanel(), i18n.tr("Coverage Terms"));
        sections.add(inject(proto().personalDisclaimerTerms(), new LeaseTermsFolder(true)), i18n.tr("Disclaimer"));
        sections.add(inject(proto().paymentMethod(), new InsurancePaymentMethodForm()), i18n.tr("Payment"));
        sections.add(inject(proto().agreementLegalBlurbAndPreAuthorizationAgreeement(), new LeaseTermsFolder(true)), i18n.tr("Agreement"),
                i18n.tr("Pay and Continue"), onPurchaseConfirmed);
        int row = -1;
        content.setWidget(++row, 0, sections);

        quoteTotalPanel = new VerticalPanel();
        quoteTotalPanel.setWidth("20em");
        quoteTotalPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
        quoteTotalPanel.getElement().getStyle().setProperty("borderStyle", "outset");
        quoteTotalPanel.getElement().getStyle().setProperty("borderRadius", "5px");
        quoteTotalPanel.getElement().getStyle().setBorderColor("#000000");
        quoteTotalPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);

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

    @Override
    protected void onPopulate() {
        super.onPopulate();

        // TODO: this is kind of hack to set the quoteTotalPanel to the correct position, but it works 
        quoteTotalPanel.getElement().getStyle().setPosition(Position.FIXED);
        quoteTotalPanel.getElement().getStyle().setTop(50, Unit.PCT);
        quoteTotalPanel.getElement().getStyle().setLeft(quoteTotalPanel.getElement().getAbsoluteLeft() + 10, Unit.PX);

        recalculateSummary();
    }

    private Widget createConverageTermsPanel() {
        coverageTerms = new FormFlexPanel();
        coverageTerms.setWidth("50%");

        int irow = -1;
        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Personal Contents"));
        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().personalContentsLimit(), new CoverageAmountCombo(PERSONAL_CONTENTS_COVERAGE_OPTIONS))).build());

        if (false) {
            coverageTerms.setWidget(++irow, 0,
                    new DecoratorBuilder(inject(proto().propertyAwayFromPremises(), new CoverageAmountCombo(PROPERTY_AWAY_FROM_PREMISES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0,
                    new DecoratorBuilder(inject(proto().additionalLivingExpenses(), new CoverageAmountCombo(ADDITIONAL_LIVING_EXPENSES_OPTIONS))).build());
        }

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Deductible (per Claim)"));
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().deductible(), new DeductibleCombo())).build());

        if (false) {
            coverageTerms.setH2(++irow, 0, 1, i18n.tr("Form of Coverage"));
            coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().formOfCoverage())).build());
        }

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Sublimits"));
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().jewleryAndArt(), new CMoneyField())).build());
        get(proto().jewleryAndArt()).setViewable(true);
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().sportsEquipment(), new CMoneyField())).build());
        get(proto().sportsEquipment()).setViewable(true);
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().electronics(), new CMoneyField())).build());
        get(proto().electronics()).setViewable(true);
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().sewerBackUp(), new CMoneyField())).build());
        get(proto().sewerBackUp()).setViewable(true);

        if (false) {
            coverageTerms.setWidget(++irow, 0,
                    new DecoratorBuilder(inject(proto().moneyOrGiftGardsOrGiftCertificates(), new LimitCombo(MONEY_GIFT_CARDS_AND_CERTIFICATES_OPTIONS)))
                            .build());
            coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().securities(), new LimitCombo(SECURITIES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().utilityTraders(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().spareAutomobileParts(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0,
                    new DecoratorBuilder(inject(proto().coinBanknoteOrStampCollections(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0,
                    new DecoratorBuilder(inject(proto().collectibleCardsAndComics(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
        }
        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Additional Coverage (per Claim)"));

        if (false) {
            coverageTerms.setWidget(++irow, 0,
                    new DecoratorBuilder(inject(proto().freezerFoodSpoilage(), new CoverageAmountCombo(asBigDecimals(0, 500, 1000, 1500)))).build());
            coverageTerms.setWidget(++irow, 0,
                    new DecoratorBuilder(inject(proto().animalsBirdsAndFish(), new CoverageAmountCombo(asBigDecimals(0, 500, 1000, 1500)))).build());
        }

        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().personalLiability(), new CoverageAmountCombo(asBigDecimals(1000000, 2000000, 5000000)))).build());

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Coverage Qualification Questions"));
        if (false) {
            coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().homeBuiness())).build());
        }

        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().numOfPrevClaims(), new NumberOfPreviousClaimsCombo())).build());

        coverageTerms.setWidget(++irow, 0, inject(proto().digitalSignatures(), new SignatureFolder(true)));
        coverageTerms.getFlexCellFormatter().setColSpan(irow, 0, 2);

        return coverageTerms;
    }

    private BigDecimal calculatePremium() {

        BigDecimal baseRate = valueOf(proto().personalContentsLimit()).multiply(new BigDecimal("0.006"));

        BigDecimal claimsFreeCreditFactor = null;
        if (valueOf(proto().numOfPrevClaims()) == 1) {
            claimsFreeCreditFactor = BigDecimal.ZERO;
        } else if (valueOf(proto().personalContentsLimit()).equals(new BigDecimal("40000"))) {
            claimsFreeCreditFactor = new BigDecimal("0.0005");
        } else if (valueOf(proto().personalContentsLimit()).equals(new BigDecimal("50000"))) {
            claimsFreeCreditFactor = new BigDecimal("0.001");
        } else if (valueOf(proto().personalContentsLimit()).equals(new BigDecimal("60000"))) {
            claimsFreeCreditFactor = new BigDecimal("0.002");
        } else {
            claimsFreeCreditFactor = BigDecimal.ZERO;
        }

        BigDecimal alarmCreditFactor = new BigDecimal("0.05");
        BigDecimal largeDeductibleCreditFactor = (valueOf(proto().deductible()).compareTo(new BigDecimal(250)) <= 0) ? BigDecimal.ZERO : new BigDecimal("0.05");

        BigDecimal oneClaimSurchargeFactor = new BigDecimal("0.05").multiply(new BigDecimal(valueOf(proto().numOfPrevClaims())));
        BigDecimal nonSprinkledSurchargeFactor = new BigDecimal("0.1");
        BigDecimal bcEarthquakeSurchargeFactor = new BigDecimal("0.1");

        BigDecimal liabliltyRate = BigDecimal.ZERO;
        if (valueOf(proto().personalLiability()).compareTo(new BigDecimal("1000000")) == 0) {
            liabliltyRate = new BigDecimal("100.00");
        } else if (valueOf(proto().personalLiability()).compareTo(new BigDecimal("2000000")) == 0) {
            liabliltyRate = new BigDecimal("125.00");
        } else if (valueOf(proto().personalLiability()).compareTo(new BigDecimal("5000000")) == 0) {
            liabliltyRate = new BigDecimal("200.00");
        }

        //@formatter:off
        BigDecimal permium = baseRate
                                .multiply(new BigDecimal(1)
                                            .subtract(claimsFreeCreditFactor)
                                            .subtract(alarmCreditFactor)
                                            .subtract(largeDeductibleCreditFactor)
                                            .add(oneClaimSurchargeFactor)
                                            .add(nonSprinkledSurchargeFactor)
                                            .add(bcEarthquakeSurchargeFactor))
                                .add(liabliltyRate);
        //@formatter:on
        return permium;
    }

    private BigDecimal totalCoverage() {
        BigDecimal[] coverageItems = new BigDecimal[] {//@formatter:off
                valueOf(proto().personalContentsLimit()),
        };//@formatter:on

        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : coverageItems) {
            total = total.add(value);
        }
        return total;
    }

    private BigDecimal valueOf(IPrimitive<BigDecimal> object) {
        BigDecimal value = ((CComponent<BigDecimal, ?>) (get(object))).getValue();
        return value != null ? value : BigDecimal.ZERO;
    }

    private Integer valueOf(IPrimitive<Integer> object) {
        Integer value = ((CComponent<Integer, ?>) (get(object))).getValue();
        return value != null ? value : 0;
    }

    private void recalculateSummary() {
        get(proto().monthlyInsurancePremium()).setValue(calculatePremium());
        get(proto().totalCoverage()).setValue(totalCoverage());
        get(proto().totalPersonalLiability()).setValue(valueOf(proto().personalLiability()));
    }

    private class CoverageAmountCombo extends MoneyLabeledCombo {

        public CoverageAmountCombo(BigDecimal... values) {
            super(i18n.tr("Coverage"), values);
            addValueChangeHandler((ValueChangeHandler<BigDecimal>) summaryRecalculationRequiredHandler);
        }
    }

    private class DeductibleCombo extends FormattableCombo<BigDecimal> {

        public DeductibleCombo() {
            super(new IFormat<BigDecimal>() {
                @Override
                public String format(BigDecimal value) {
                    if (value.compareTo(new BigDecimal("250")) <= 0) {
                        return i18n.tr("Standard Deductible {0}", Utils.formatMoney(value));
                    } else {
                        return i18n.tr("Optional Deductible {0}", Utils.formatMoney(value));
                    }
                }

                @Override
                public BigDecimal parse(String string) throws ParseException {
                    return null;
                }

            });
            setOptions(Arrays.asList(asBigDecimals(250, 1000)));
            addValueChangeHandler((ValueChangeHandler<BigDecimal>) summaryRecalculationRequiredHandler);
        }
    }

    private class LimitCombo extends MoneyLabeledCombo {

        public LimitCombo(BigDecimal... options) {
            super(i18n.tr("Limit"), options);
            addValueChangeHandler((ValueChangeHandler<BigDecimal>) summaryRecalculationRequiredHandler);
        }
    }

    private class NumberOfPreviousClaimsCombo extends FormattableCombo<Integer> {

        public NumberOfPreviousClaimsCombo() {
            super(new IFormat<Integer>() {

                @Override
                public String format(Integer value) {
                    if (value == null || value.equals(new Integer(0))) {
                        return i18n.tr("None");
                    }
                    if (value.equals(5)) {
                        return i18n.tr("More than {0}", 4);
                    } else {
                        return value.toString();
                    }
                }

                @Override
                public Integer parse(String string) throws ParseException {
                    return null;
                }
            });
            setOptions(Arrays.asList(0, 1, 2, 3, 4, 5));
            addValueChangeHandler((ValueChangeHandler<Integer>) summaryRecalculationRequiredHandler);
        }

    }

}