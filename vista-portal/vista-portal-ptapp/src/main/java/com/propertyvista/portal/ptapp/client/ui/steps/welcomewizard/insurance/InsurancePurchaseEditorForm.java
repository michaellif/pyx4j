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
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.ptapp.client.resources.welcomewizard.WelcomeWizardResources;
import com.propertyvista.portal.ptapp.client.ui.steps.summary.SignatureFolder;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance.components.MoneyLabeledCombo;
import com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.reviewlease.LeaseTermsFolder;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.PurchaseInsuranceDTO;

public class InsurancePurchaseEditorForm extends CEntityDecoratableForm<PurchaseInsuranceDTO> {

    private final static BigDecimal[] PERSONAL_CONTENTS_LIMITS_OPTIONS = asBigDecimals(10000, 12000, 14000, 16000, 18000, 20000, 22000, 24000, 26000, 28000,
            30000, 35000, 40000, 50000, 60000);

    private final static BigDecimal[] PROPERTY_AWAY_FROM_PREMISES_OPTIONS = asBigDecimals(0, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000);

    private final static BigDecimal[] ADDITIONAL_LIVING_EXPENSES_OPTIONS = asBigDecimals(2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 12000, 14000,
            16000, 18000, 20000, 22000, 24000, 26000, 28000, 30000);

    private final static BigDecimal[] JEWLERY_AND_FURS_OPTIONS = asBigDecimals(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000);

    private final static BigDecimal[] BYCICLES_OPTIONS = asBigDecimals(500, 1000, 1500, 2000, 2500, 3000);

    private final static BigDecimal[] PERSONAL_COMPUTERS_OPTIONS = asBigDecimals(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000);

    private final static BigDecimal[] MONEY_GIFT_CARDS_AND_CERTIFICATES_OPTIONS = asBigDecimals(500);

    private final static BigDecimal[] SECURITIES_OPTIONS = asBigDecimals(500);

    private final static BigDecimal[] DEFAULT_LIMITIES_OPTIONS = asBigDecimals(500, 1000, 1500, 2000, 2500, 3000);

    private final static I18n i18n = I18n.get(InsurancePurchaseEditorForm.class);

    private VerticalPanel quoteTotalPanel;

    private final ValueChangeHandler<BigDecimal> summaryRecalculationRequiredHandler;

    private FormFlexPanel insuranceTerms;

    public InsurancePurchaseEditorForm() {
        super(PurchaseInsuranceDTO.class);
        this.summaryRecalculationRequiredHandler = new ValueChangeHandler<BigDecimal>() {
            @Override
            public void onValueChange(ValueChangeEvent<BigDecimal> event) {
                recalculateSummary();
            }
        };
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        content.getColumnFormatter().setWidth(0, "50%");
        content.getColumnFormatter().setWidth(1, "50%");

        insuranceTerms = new FormFlexPanel();
        insuranceTerms.setWidth("50%");

        int row = -1;
        insuranceTerms.setWidget(++row, 0, new HTML(WelcomeWizardResources.INSTANCE.insuranceReasonExplanation().getText()));
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
                new DecoratorBuilder(inject(proto().moneyOrGiftGardsOrGiftCertificates(), new LimitCombo(MONEY_GIFT_CARDS_AND_CERTIFICATES_OPTIONS))).build());
        insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().securities(), new LimitCombo(SECURITIES_OPTIONS))).build());
        insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().utilityTraders(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
        insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().spareAutomobileParts(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
        insuranceTerms.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().coinBanknoteOrStampCollections(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());
        insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().collectibleCardsAndComics(), new LimitCombo(DEFAULT_LIMITIES_OPTIONS))).build());

        insuranceTerms.setH2(++row, 0, 1, i18n.tr("Additional Coverage (per Claim)"));
        insuranceTerms.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().freezerFoodSpoilage(), new CoverageAmountCombo(new BigDecimal(0), new BigDecimal(500),
                        new BigDecimal(1000), new BigDecimal(1500)))).build());
        insuranceTerms.setWidget(
                ++row,
                0,
                new DecoratorBuilder(inject(proto().animalsBirdsAndFish(), new CoverageAmountCombo(new BigDecimal(0), new BigDecimal(500),
                        new BigDecimal(1000), new BigDecimal(1500)))).build());
        insuranceTerms.setWidget(++row, 0,
                new DecoratorBuilder(inject(proto().personalLiability(), new CoverageAmountCombo(new BigDecimal(500000), new BigDecimal(1000000)))).build());

        insuranceTerms.setH2(++row, 0, 1, i18n.tr("Coverage Qualification Questions"));
        insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().homeBuiness(), new HomeBuisnessCombo())).build());
        insuranceTerms.setWidget(++row, 0, new DecoratorBuilder(inject(proto().numOfPrevClaims())).build());

        insuranceTerms.setWidget(++row, 0, inject(proto().personalDisclaimerTerms(), new LeaseTermsFolder(true)));

        insuranceTerms.setWidget(++row, 0, inject(proto().digitalSignatures(), new SignatureFolder(true)));
        insuranceTerms.getFlexCellFormatter().setColSpan(row, 0, 2);

        insuranceTerms.setWidget(++row, 0, inject(proto().paymentMethod(), new InsurancePaymentMethodForm()));
        insuranceTerms.setWidget(++row, 0, inject(proto().agreementLegalBlurbAndPreAuthorizationAgreeement(), new LeaseTermsFolder(true)));

        content.setWidget(0, 0, new ScrollPanel(insuranceTerms));

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
        BigDecimal a = valueOf(proto().personalContentsLimit()).add(valueOf(proto().propertyAwayFromPremises()))
                .add(valueOf(proto().additionalLivingExpenses())).divide(new BigDecimal(20000));
        BigDecimal normalizedDeductible = new BigDecimal("3000").subtract(valueOf(proto().deductible())).divide(new BigDecimal("500"));
        BigDecimal permium = new BigDecimal("5").add(a).add(normalizedDeductible);
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
            super("Decuctible", new BigDecimal(500), new BigDecimal(1000), new BigDecimal(2000));
            addValueChangeHandler(summaryRecalculationRequiredHandler);
        }
    }

    private class LimitCombo extends MoneyLabeledCombo {

        public LimitCombo(BigDecimal... options) {
            super("Limit", options);
            addValueChangeHandler(summaryRecalculationRequiredHandler);
        }
    }

    private class HomeBuisnessCombo extends CComboBox<String> {

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