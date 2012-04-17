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
package com.propertyvista.portal.rpc.ptapp.dto.welcomewizard;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.portal.rpc.ptapp.dto.LegalTermsDescriptorDTO;

@Transient
public interface PurchaseInsuranceDTO extends IEntity {

    enum FormOfCoverage {

        basicCoverage;

        @Override
        public String toString() {
            return I18nEnum.toString(basicCoverage);
        };
    }

    // Section: Personal Contents 
    @Caption(description = "TBD")
    IPrimitive<String> personalContentsLimit();

    @Caption(description = "TBD")
    IPrimitive<String> propertyAwayFromPremises();

    @Caption(description = "TBD")
    IPrimitive<String> additionalLivingExpenses();

    // Section: Deductible (per Claim)
    @Caption(description = "TBD")
    IPrimitive<String> deductible();

    // Section: Form of Coverage
    IPrimitive<FormOfCoverage> formOfCoverage();

    // Section: Special Limities (per Claim)
    @Caption(name = "Jewlery & Furs")
    IPrimitive<String> jewleryAndFurs();

    IPrimitive<String> bicycles();

    IPrimitive<String> personalComputers();

    @Caption(name = "Money, Gift Cards, or Gift Certificates")
    IPrimitive<String> moneyOrGiftGardsOrGiftCertificates();

    IPrimitive<String> securities();

    IPrimitive<String> utilityTraders();

    IPrimitive<String> spareAutomobileParts();

    IPrimitive<String> coinBanknoteOrStampCollections();

    IPrimitive<String> collectibleCardsandComics();

    // Section: Additional Coverage (per Claim)    
    IPrimitive<String> freezerFoodSpoilage();

    IPrimitive<String> animalsBirdsAndFish();

    IPrimitive<String> personalLiability();

    // Section: Coverage Qualification Questions
    @Caption(name = "Do you operate any buisness from your home?")
    IPrimitive<String> homeBuiness();

    @Caption(name = "How many claims have you or any other resident of your household made against a previous Tenants, Condo, or Homeowners Policy in the past 5 years?")
    IPrimitive<Integer> numOfPrevClaims();

    // Personal Disclaimer
    IList<LegalTermsDescriptorDTO> personalDisclaimerTerms();

    @EmbeddedEntity
    PaymentMethod paymentMethod();

}
