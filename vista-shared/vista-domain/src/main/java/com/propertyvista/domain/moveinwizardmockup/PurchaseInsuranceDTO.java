/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.moveinwizardmockup;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.dto.LegalTermsDescriptorDTO;

@Transient
public interface PurchaseInsuranceDTO extends IEntity {

    @I18n(context = "Form of Coverage")
    public enum FormOfCoverage {

        basicCoverage, enhancedCoverage, extendedWaterCoverage;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        };
    }

    @I18n
    public enum HomeBuisnessOptions {

        no, yes;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    // Section: Personal Contents
    @Caption(description = "TBD")
    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> personalContentsLimit();

    @Caption(description = "TBD")
    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> propertyAwayFromPremises();

    @Caption(description = "TBD")
    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> additionalLivingExpenses();

    // Section: Deductible (per Claim)
    @Caption(description = "TBD")
    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> deductible();

    // Section: Form of Coverage
    @Caption(description = "TBD")
    @NotNull
    IPrimitive<FormOfCoverage> formOfCoverage();

    // Section: Special Limities (per Claim)
    @Caption(name = "Jewlery & Art")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> jewleryAndArt();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> sportsEquipment();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> electronics();

    IPrimitive<BigDecimal> sewerBackUp();

    @Caption(name = "Money, Gift Cards, or Gift Certificates", description = "TBD")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> moneyOrGiftGardsOrGiftCertificates();

    @Caption(description = "TBD")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> securities();

    @Caption(description = "TBD")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> utilityTraders();

    @Caption(description = "TBD")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> spareAutomobileParts();

    @Caption(description = "TBD")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> coinBanknoteOrStampCollections();

    @Caption(description = "TBD")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> collectibleCardsAndComics();

    // Section: Additional Coverage (per Claim)
    @Caption(description = "TBD")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> freezerFoodSpoilage();

    @Caption(description = "TBD")
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> animalsBirdsAndFish();

    @Caption(description = "TBD")
    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> personalLiability();

    // Section: Coverage Qualification Questions
    @Caption(name = "Do you operate any buisness from your home?", description = "TBD")
    @NotNull
    IPrimitive<HomeBuisnessOptions> homeBuiness();

    @Caption(name = "How many claims have you or any other resident of your household made against a previous Tenants, Condo, or Homeowners Policy in the past 5 years?", description = "TBD")
    @NotNull
    IPrimitive<Integer> numOfPrevClaims();

    // Section:  Quote Total (floating box with that containse the following values)
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> monthlyInsurancePremium();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalCoverage();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalPersonalLiability();

    // Personal Disclaimer
    IList<LegalTermsDescriptorDTO> personalDisclaimerTerms();

    @EmbeddedEntity
    InsurancePaymentMethodMockupDTO paymentMethod();

    IList<LegalTermsDescriptorDTO> agreementLegalBlurbAndPreAuthorizationAgreeement();

}
