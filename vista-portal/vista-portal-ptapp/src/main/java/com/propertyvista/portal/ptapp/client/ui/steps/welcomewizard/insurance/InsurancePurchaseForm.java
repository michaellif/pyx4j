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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.SignatureFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components.FormattableCombo;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components.MoneyLabeledCombo;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components.MultiDisclosurePanel;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.reviewlease.LeaseTermsFolder;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.PurchaseInsuranceDTO;

public class InsurancePurchaseForm extends CEntityDecoratableForm<PurchaseInsuranceDTO> {

    private final static BigDecimal[] PERSONAL_CONTENTS_LIMITS_OPTIONS = asBigDecimals(10000, 12000, 14000, 16000, 18000, 20000, 22000, 24000, 26000, 28000,
            30000, 35000, 40000, 50000, 60000, 70000, 80000);

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

    private final ValueChangeHandler<BigDecimal> summaryRecalculationRequiredHandler;

    private FormFlexPanel coverageTerms;

    private final Command onPurchaseConfirmed;

    public InsurancePurchaseForm(Command onPurchaseConfirmed) {
        super(PurchaseInsuranceDTO.class);
        this.summaryRecalculationRequiredHandler = new ValueChangeHandler<BigDecimal>() {
            @Override
            public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                recalculateSummary();
            }
        };
        this.onPurchaseConfirmed = onPurchaseConfirmed;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        content.getColumnFormatter().setWidth(0, "70%");
        content.getColumnFormatter().setWidth(1, "50%");

        coverageTerms = new FormFlexPanel();
        coverageTerms.setWidth("50%");

        int irow = -1;
//        insuranceTerms.setH1(++irow, 0, 1, i18n.tr("Coverage"));
        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Personal Contents"));
        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().personalContentsLimit(), new CoverageAmountCombo(PERSONAL_CONTENTS_LIMITS_OPTIONS))).build());

        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().propertyAwayFromPremises(), new CoverageAmountCombo(PROPERTY_AWAY_FROM_PREMISES_OPTIONS))).build());
        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().additionalLivingExpenses(), new CoverageAmountCombo(ADDITIONAL_LIVING_EXPENSES_OPTIONS))).build());

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Decuctible (per Claim)"));
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().deductible(), new DeductibleCombo())).build());

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Form of Coverage"));
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().formOfCoverage())).build());

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Special Limites (per Claim)"));
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().jewleryAndFurs(), new LimitCombo(JEWLERY_AND_FURS_OPTIONS))).build());
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().bicycles(), new LimitCombo(BYCICLES_OPTIONS))).build());
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().personalComputers(), new LimitCombo(PERSONAL_COMPUTERS_OPTIONS))).build());
        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().moneyOrGiftGardsOrGiftCertificates(), new LimitCombo(MONEY_GIFT_CARDS_AND_CERTIFICATES_OPTIONS))).build());
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().securities(), new LimitCombo(SECURITIES_OPTIONS))).build());
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().utilityTraders(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().spareAutomobileParts(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().coinBanknoteOrStampCollections(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().collectibleCardsAndComics(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Additional Coverage (per Claim)"));
        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().freezerFoodSpoilage(), new CoverageAmountCombo(asBigDecimals(0, 500, 1000, 1500)))).build());
        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().animalsBirdsAndFish(), new CoverageAmountCombo(asBigDecimals(0, 500, 1000, 1500)))).build());
        coverageTerms.setWidget(++irow, 0,
                new DecoratorBuilder(inject(proto().personalLiability(), new CoverageAmountCombo(asBigDecimals(500000, 1000000, 5000000)))).build());

        coverageTerms.setH2(++irow, 0, 1, i18n.tr("Coverage Qualification Questions"));
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().homeBuiness())).build());
        coverageTerms.setWidget(++irow, 0, new DecoratorBuilder(inject(proto().numOfPrevClaims(), new NumberOfPreviousClaimsCombo())).build());
        coverageTerms.setWidget(++irow, 0, inject(proto().digitalSignatures(), new SignatureFolder(true)));
        coverageTerms.getFlexCellFormatter().setColSpan(irow, 0, 2);

        MultiDisclosurePanel sections = new MultiDisclosurePanel();

        sections.add(coverageTerms, i18n.tr("Coverage Terms"));
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

    public void setQuoteTotalPanelVisibility(boolean isVisible) {
        quoteTotalPanel.setVisible(isVisible);
    }

    private BigDecimal premium() {
        BigDecimal liablilty = valueOf(proto().personalLiability()).divide(new BigDecimal("1000000"));
        BigDecimal a = valueOf(proto().personalContentsLimit()).add(valueOf(proto().propertyAwayFromPremises()))
                .add(valueOf(proto().additionalLivingExpenses())).divide(new BigDecimal(20000));
        BigDecimal normalizedDeductible = new BigDecimal("3000").subtract(valueOf(proto().deductible())).divide(new BigDecimal("500"));
        BigDecimal permium = new BigDecimal("5").add(liablilty).add(a).add(normalizedDeductible);
        return permium;
    }

    private BigDecimal totalCoverage() {
        BigDecimal[] coverageItems = new BigDecimal[] {//@formatter:off
                valueOf(proto().personalContentsLimit()),
                valueOf(proto().propertyAwayFromPremises()),
                valueOf(proto().additionalLivingExpenses())
        };//@formatter:on

        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : coverageItems) {
            total = total.add(value);
        }
        return total;
    }

    private BigDecimal valueOf(IObject<?> object) {
        BigDecimal value = ((MoneyLabeledCombo) (get(object))).getValue();
        return value != null ? value : BigDecimal.ZERO;
    }

    private void recalculateSummary() {
        get(proto().monthlyInsurancePremium()).setValue(premium());
        get(proto().totalCoverage()).setValue(totalCoverage());
        get(proto().totalPersonalLiability()).setValue(valueOf(proto().personalLiability()));
    }

    private class CoverageAmountCombo extends MoneyLabeledCombo {

        public CoverageAmountCombo(BigDecimal... values) {
            super("Coverage", values);
            addValueChangeHandler(summaryRecalculationRequiredHandler);
        }
    }

    private class DeductibleCombo extends MoneyLabeledCombo {

        public DeductibleCombo() {
            super("Decuctible", asBigDecimals(500, 1000, 2000));
            addValueChangeHandler(summaryRecalculationRequiredHandler);
        }
    }

    private class LimitCombo extends MoneyLabeledCombo {

        public LimitCombo(BigDecimal... options) {
            super("Limit", options);
            addValueChangeHandler(summaryRecalculationRequiredHandler);
        }
    }

    private class NumberOfPreviousClaimsCombo extends FormattableCombo<Integer> {

        public NumberOfPreviousClaimsCombo() {
            super(new IFormat<Integer>() {

                @Override
                public String format(Integer value) {
                    if (value == null || value.equals(new Integer(0))) {
                        return "None";
                    }
                    if (value.equals(Integer.MAX_VALUE)) {
                        return "More than 4";
                    } else {
                        return value.toString();
                    }
                }

                @Override
                public Integer parse(String string) throws ParseException {
                    // TODO Auto-generated method stub
                    return null;
                }
            });
            setOptions(Arrays.asList(0, 1, 2, 3, 4, Integer.MAX_VALUE));
        }

    }

}