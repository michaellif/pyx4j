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
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.insurance;

import static com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.Utils.asBigDecimals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Arrays;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.moveinwizardmockup.PurchaseInsuranceDTO;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.LegalTermsFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.MoneyLabeledCombo;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.MultiDisclosurePanel;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.SignatureFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.Utils;

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

    private TwoColumnFlexFormPanel coverageTerms;

    private final Command onPurchaseConfirmed;

    private final boolean useFloatingQuote;

    private HTML tooManyPreviousClaimsMessage;

    public InsurancePurchaseForm(boolean useFloatingQuote, Command onPurchaseConfirmed) {
        super(PurchaseInsuranceDTO.class);
        this.summaryRecalculationRequiredHandler = new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                recalculateSummary();
            }
        };
        this.onPurchaseConfirmed = onPurchaseConfirmed;
        this.useFloatingQuote = useFloatingQuote;
    }

    public void setQuoteTotalPanelVisibility(boolean isVisible) {
        quoteTotalPanel.setVisible(isVisible);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        content.getColumnFormatter().setWidth(0, "70%");
        content.getColumnFormatter().setWidth(1, "50%");

        MultiDisclosurePanel sections = new MultiDisclosurePanel();

        sections.add(createConverageTermsPanel(), i18n.tr("Coverage Terms"));
        sections.add(inject(proto().personalDisclaimerTerms(), new LegalTermsFolder(true)), i18n.tr("Disclaimer"));
        sections.add(inject(proto().paymentMethod(), new InsurancePaymentMethodForm()), i18n.tr("Payment"));
        sections.add(inject(proto().agreementLegalBlurbAndPreAuthorizationAgreeement(), new LegalTermsFolder(true)), i18n.tr("Agreement"),
                i18n.tr("Pay and Continue"), onPurchaseConfirmed);
        int row = -1;
        content.setWidget(++row, 0, sections);

        if (useFloatingQuote) {
            content.setWidget(0, 1, createQuoteTotalPanel());
        }

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (useFloatingQuote) {
            // TODO: this is kind of hack to set the quoteTotalPanel to the correct position, but it works 
            quoteTotalPanel.getElement().getStyle().setPosition(Position.FIXED);
            quoteTotalPanel.getElement().getStyle().setTop(50, Unit.PCT);
            quoteTotalPanel.getElement().getStyle().setLeft(quoteTotalPanel.getElement().getAbsoluteLeft() + 10, Unit.PX);
        }

        recalculateSummary();
    }

    private Widget createConverageTermsPanel() {
        coverageTerms = new TwoColumnFlexFormPanel();
        coverageTerms.setWidth("50%");

        int irow = -1;
        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Personal Contents"));
        coverageTerms.setWidget(++irow, 0,
                new FormDecoratorBuilder(inject(proto().personalContentsLimit(), new CoverageAmountCombo(PERSONAL_CONTENTS_COVERAGE_OPTIONS))).build());

        if (false) {
            coverageTerms.setWidget(++irow, 0,
                    new FormDecoratorBuilder(inject(proto().propertyAwayFromPremises(), new CoverageAmountCombo(PROPERTY_AWAY_FROM_PREMISES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0,
                    new FormDecoratorBuilder(inject(proto().additionalLivingExpenses(), new CoverageAmountCombo(ADDITIONAL_LIVING_EXPENSES_OPTIONS))).build());
        }

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Deductible (per Claim)"));
        coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().deductible(), new DeductibleCombo())).build());

        if (false) {
            coverageTerms.setH2(++irow, 0, 1, i18n.tr("Form of Coverage"));
            coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().formOfCoverage())).build());
        }

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Sublimits"));
        coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().jewleryAndArt(), new CMoneyField())).build());
        get(proto().jewleryAndArt()).setViewable(true);
        coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().sportsEquipment(), new CMoneyField())).build());
        get(proto().sportsEquipment()).setViewable(true);
        coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().electronics(), new CMoneyField())).build());
        get(proto().electronics()).setViewable(true);
        coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().sewerBackUp(), new CMoneyField())).build());
        get(proto().sewerBackUp()).setViewable(true);

        if (false) {
            coverageTerms.setWidget(++irow, 0,
                    new FormDecoratorBuilder(inject(proto().moneyOrGiftGardsOrGiftCertificates(), new LimitCombo(MONEY_GIFT_CARDS_AND_CERTIFICATES_OPTIONS)))
                            .build());
            coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().securities(), new LimitCombo(SECURITIES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().utilityTraders(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0,
                    new FormDecoratorBuilder(inject(proto().spareAutomobileParts(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0,
                    new FormDecoratorBuilder(inject(proto().coinBanknoteOrStampCollections(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
            coverageTerms.setWidget(++irow, 0,
                    new FormDecoratorBuilder(inject(proto().collectibleCardsAndComics(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
        }
        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Additional Coverage (per Claim)"));

        if (false) {
            coverageTerms.setWidget(++irow, 0,
                    new FormDecoratorBuilder(inject(proto().freezerFoodSpoilage(), new CoverageAmountCombo(asBigDecimals(0, 500, 1000, 1500)))).build());
            coverageTerms.setWidget(++irow, 0,
                    new FormDecoratorBuilder(inject(proto().animalsBirdsAndFish(), new CoverageAmountCombo(asBigDecimals(0, 500, 1000, 1500)))).build());
        }

        coverageTerms.setWidget(++irow, 0,
                new FormDecoratorBuilder(inject(proto().personalLiability(), new CoverageAmountCombo(asBigDecimals(1000000, 2000000, 5000000)))).build());

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Coverage Qualification Questions"));
        if (false) {
            coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().homeBuiness())).build());
        }

        coverageTerms.setWidget(++irow, 0, new FormDecoratorBuilder(inject(proto().numOfPrevClaims(), new NumberOfPreviousClaimsCombo())).build());

        if (!useFloatingQuote) {
            coverageTerms.setH2(++irow, 0, 1, "");
            coverageTerms.setWidget(++irow, 0, createQuoteTotalPanel());
        }
        coverageTerms.setWidget(++irow, 0, inject(proto().digitalSignatures(), new SignatureFolder(true)));
        coverageTerms.getFlexCellFormatter().setColSpan(irow, 0, 2);

        return coverageTerms;
    }

    private Widget createQuoteTotalPanel() {
        quoteTotalPanel = new VerticalPanel();
        quoteTotalPanel.setWidth("20em");
        quoteTotalPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
        quoteTotalPanel.getElement().getStyle().setProperty("borderStyle", "outset");
        quoteTotalPanel.getElement().getStyle().setProperty("borderRadius", "5px");
        quoteTotalPanel.getElement().getStyle().setBorderColor("#000000");
        quoteTotalPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);

        {
            quoteTotalPanel.add(new FormDecoratorBuilder(inject(proto().monthlyInsurancePremium()), 10).build());
            get(proto().monthlyInsurancePremium()).setViewable(true);

            tooManyPreviousClaimsMessage = new HTML(i18n.tr("Please call 1888-XXXX-XXX for a custom quote"));
            tooManyPreviousClaimsMessage.setVisible(false);
            tooManyPreviousClaimsMessage.getElement().getStyle().setWidth(100, Unit.PCT);
            tooManyPreviousClaimsMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            tooManyPreviousClaimsMessage.getElement().getStyle().setProperty("textAlign", "center");

            quoteTotalPanel.add(tooManyPreviousClaimsMessage);

            quoteTotalPanel.add(new FormDecoratorBuilder(inject(proto().totalCoverage()), 10).build());
            get(proto().totalCoverage()).setViewable(true);
            quoteTotalPanel.add(new FormDecoratorBuilder(inject(proto().totalPersonalLiability()), 10).build());
            get(proto().totalPersonalLiability()).setViewable(true);
        }
        return quoteTotalPanel;
    }

    private BigDecimal calculatePremium() {

        BigDecimal baseRate = bigDecimalOf(proto().personalContentsLimit()).multiply(new BigDecimal("0.006"));

        BigDecimal claimsFreeCreditFactor = null;
        if (integerOf(proto().numOfPrevClaims()) == 1) {
            claimsFreeCreditFactor = BigDecimal.ZERO;
        } else if (bigDecimalOf(proto().personalContentsLimit()).equals(new BigDecimal("40000"))) {
            claimsFreeCreditFactor = new BigDecimal("0.0005");
        } else if (bigDecimalOf(proto().personalContentsLimit()).equals(new BigDecimal("50000"))) {
            claimsFreeCreditFactor = new BigDecimal("0.001");
        } else if (bigDecimalOf(proto().personalContentsLimit()).equals(new BigDecimal("60000"))) {
            claimsFreeCreditFactor = new BigDecimal("0.002");
        } else {
            claimsFreeCreditFactor = BigDecimal.ZERO;
        }

        BigDecimal alarmCreditFactor = new BigDecimal("0.05");
        BigDecimal largeDeductibleCreditFactor = (bigDecimalOf(proto().deductible()).compareTo(new BigDecimal(250)) <= 0) ? BigDecimal.ZERO : new BigDecimal(
                "0.05");

        BigDecimal oneClaimSurchargeFactor = new BigDecimal("0.05").multiply(new BigDecimal(integerOf(proto().numOfPrevClaims())));
        BigDecimal nonSprinkledSurchargeFactor = new BigDecimal("0.1");
        BigDecimal bcEarthquakeSurchargeFactor = new BigDecimal("0.1");

        BigDecimal liabliltyRate = BigDecimal.ZERO;
        if (bigDecimalOf(proto().personalLiability()).compareTo(new BigDecimal("1000000")) == 0) {
            liabliltyRate = new BigDecimal("100.00");
        } else if (bigDecimalOf(proto().personalLiability()).compareTo(new BigDecimal("2000000")) == 0) {
            liabliltyRate = new BigDecimal("125.00");
        } else if (bigDecimalOf(proto().personalLiability()).compareTo(new BigDecimal("5000000")) == 0) {
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
                bigDecimalOf(proto().personalContentsLimit()),
        };//@formatter:on

        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : coverageItems) {
            total = total.add(value);
        }
        return total;
    }

    private BigDecimal bigDecimalOf(IPrimitive<BigDecimal> object) {
        BigDecimal value = (get(object)).getValue();
        return value != null ? value : BigDecimal.ZERO;
    }

    private Integer integerOf(IPrimitive<Integer> object) {
        Integer value = (get(object)).getValue();
        return value != null ? value : 0;
    }

    private void recalculateSummary() {
        BigDecimal monthlyPremium = calculatePremium().setScale(2, BigDecimal.ROUND_HALF_UP).divide(
                new BigDecimal("12.00").setScale(2, BigDecimal.ROUND_HALF_UP), new MathContext(4, RoundingMode.HALF_UP));
        get(proto().monthlyInsurancePremium()).setValue(monthlyPremium);
        get(proto().totalCoverage()).setValue(totalCoverage());
        get(proto().totalPersonalLiability()).setValue(bigDecimalOf(proto().personalLiability()));
    }

    private class CoverageAmountCombo extends MoneyLabeledCombo {

        public CoverageAmountCombo(BigDecimal... values) {
            super(i18n.tr("Coverage"), values);
            addValueChangeHandler((ValueChangeHandler<BigDecimal>) summaryRecalculationRequiredHandler);
        }
    }

    private class DeductibleCombo extends CComboBox<BigDecimal> {

        public DeductibleCombo() {
            super(null, null, new IFormat<BigDecimal>() {
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

    private class NumberOfPreviousClaimsCombo extends CComboBox<Integer> {

        public NumberOfPreviousClaimsCombo() {
            super(null, null, new IFormat<Integer>() {

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
            addValueChangeHandler(new ValueChangeHandler<Integer>() {
                @Override
                public void onValueChange(ValueChangeEvent<Integer> event) {
                    get(proto().monthlyInsurancePremium()).setVisible(event.getValue() <= 1);
                    tooManyPreviousClaimsMessage.setVisible(event.getValue() > 1);
                }
            });
        }

    }

}