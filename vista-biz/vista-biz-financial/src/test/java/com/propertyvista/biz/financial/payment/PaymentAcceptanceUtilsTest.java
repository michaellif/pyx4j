/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 14, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.experimental.categories.Category;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.MerchantAccount.ElectronicPaymentSetup;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category({ FunctionalTests.class })
public class PaymentAcceptanceUtilsTest extends TestCase {

    static enum Expect {
        Fee, Disable, NoFee
    }

    private void assertAllowed(VistaApplication vistaApplication, ElectronicPaymentSetup setup, boolean requireCashEquivalent,
            PaymentTypeSelectionPolicy selectionPolicy, CreditCardType creditCardType, Expect expected) {
        Collection<CreditCardType> allCards = PaymentAcceptanceUtils.getAllowedCreditCardTypes(vistaApplication, setup, requireCashEquivalent, selectionPolicy,
                false);

        if (expected != Expect.Disable) {
            Assert.assertTrue("Allowed" + creditCardType + " expected, but was " + allCards, allCards.contains(creditCardType));
        } else {
            Assert.assertFalse("Not Allowed " + creditCardType + " expected, but was " + allCards, allCards.contains(creditCardType));
        }

        Collection<CreditCardType> feeCards = PaymentAcceptanceUtils.getAllowedCreditCardTypes(vistaApplication, setup, requireCashEquivalent, selectionPolicy,
                true);

        if (expected == Expect.Fee) {
            Assert.assertTrue("fee on " + creditCardType + " expected, but was " + feeCards, feeCards.contains(creditCardType));
        } else {
            Assert.assertFalse("not fee on " + creditCardType + " expected, but was " + feeCards, feeCards.contains(creditCardType));
        }
    }

    public void testPaymentTypeSelectionNoConvenienceFee() {
        if (VistaTODO.convenienceFeeEnabled) {
            //tests are not applicable
            return;
        }
        ElectronicPaymentSetup setup = EntityFactory.create(ElectronicPaymentSetup.class);
        setup.acceptedCreditCard().setValue(true);
        setup.acceptedCreditCardConvenienceFee().setValue(false);

        // Nothing accepted, fee applied on all but On CashEquivalent
        {
            PaymentTypeSelectionPolicy selectionPolicy = EntityFactory.create(PaymentTypeSelectionPolicy.class);

            for (CreditCardType creditCardType : CreditCardType.values()) {
                assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, creditCardType, Expect.Disable);
                assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, creditCardType, Expect.Disable);
                // On CashEquivalent
                assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, creditCardType, Expect.Disable);
                assertAllowed(VistaApplication.crm, setup, true, selectionPolicy, creditCardType, Expect.Disable);
            }
        }

        // Accepted only MasterCard on CashEquivalent
        {
            PaymentTypeSelectionPolicy selectionPolicy = EntityFactory.create(PaymentTypeSelectionPolicy.class);
            selectionPolicy.cashEquivalentCreditCardMasterCard().setValue(true);

            for (CreditCardType creditCardType : CreditCardType.values()) {
                assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, creditCardType, Expect.Disable);
                assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, creditCardType, Expect.Disable);
            }
            // On CashEquivalent
            assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, CreditCardType.MasterCard, Expect.Disable);
            assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, CreditCardType.Visa, Expect.Disable);
            assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, CreditCardType.VisaDebit, Expect.Disable);
        }

        // Accepted only MasterCard in portal; e.g. There are no fee on MasterCard
        {
            PaymentTypeSelectionPolicy selectionPolicy = EntityFactory.create(PaymentTypeSelectionPolicy.class);
            // This is need 
            selectionPolicy.acceptedCreditCardMasterCard().setValue(true);
            selectionPolicy.residentPortalCreditCardMasterCard().setValue(true);

            assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, CreditCardType.MasterCard, Expect.NoFee);
            assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, CreditCardType.Visa, Expect.Disable);
            assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, CreditCardType.VisaDebit, Expect.Disable);

            assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, CreditCardType.MasterCard, Expect.NoFee);
            assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, CreditCardType.Visa, Expect.Disable);
            assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, CreditCardType.VisaDebit, Expect.Disable);

            // On CashEquivalent
            for (CreditCardType creditCardType : CreditCardType.values()) {
                assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, creditCardType, Expect.Disable);
                assertAllowed(VistaApplication.crm, setup, true, selectionPolicy, creditCardType, Expect.Disable);
            }
        }

    }

    public void testConvenienceFeeAcceptance() {
        if (!VistaTODO.convenienceFeeEnabled) {
            //tests are not applicable
            return;
        }
        ElectronicPaymentSetup setup = EntityFactory.create(ElectronicPaymentSetup.class);
        setup.acceptedCreditCard().setValue(true);
        setup.acceptedCreditCardConvenienceFee().setValue(true);

        // Nothing accepted, fee applied on all but On CashEquivalent
        {
            PaymentTypeSelectionPolicy selectionPolicy = EntityFactory.create(PaymentTypeSelectionPolicy.class);

            for (CreditCardType creditCardType : CreditCardType.values()) {
                assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, creditCardType, Expect.Fee);
                assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, creditCardType, Expect.Disable);
                // On CashEquivalent
                assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, creditCardType, Expect.Disable);
                assertAllowed(VistaApplication.crm, setup, true, selectionPolicy, creditCardType, Expect.Disable);
            }

            Collection<PaymentType> paymentTypesCrm = PaymentAcceptanceUtils.getAllowedPaymentTypes(VistaApplication.crm, setup, true, false, selectionPolicy);
            Assert.assertFalse("Cards Not Allowed expected, but was " + paymentTypesCrm, paymentTypesCrm.contains(PaymentType.CreditCard));

            Collection<PaymentType> paymentTypesResident = PaymentAcceptanceUtils.getAllowedPaymentTypes(VistaApplication.resident, setup, true, false,
                    selectionPolicy);
            Assert.assertTrue("Cards Allowed expected, but was " + paymentTypesResident, paymentTypesResident.contains(PaymentType.CreditCard));
        }

        // Accepted only MasterCard on CashEquivalent
        {
            PaymentTypeSelectionPolicy selectionPolicy = EntityFactory.create(PaymentTypeSelectionPolicy.class);
            selectionPolicy.cashEquivalentCreditCardMasterCard().setValue(true);

            for (CreditCardType creditCardType : CreditCardType.values()) {
                assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, creditCardType, Expect.Fee);
                assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, creditCardType, Expect.Disable);
            }
            // On CashEquivalent
            assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, CreditCardType.MasterCard, Expect.Fee);
            assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, CreditCardType.Visa, Expect.Disable);
            assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, CreditCardType.VisaDebit, Expect.Disable);
        }

        // Accepted only MasterCard in portal; e.g. There are no fee on MasterCard
        {
            PaymentTypeSelectionPolicy selectionPolicy = EntityFactory.create(PaymentTypeSelectionPolicy.class);
            // This is need 
            selectionPolicy.acceptedCreditCardMasterCard().setValue(true);
            selectionPolicy.residentPortalCreditCardMasterCard().setValue(true);

            assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, CreditCardType.MasterCard, Expect.NoFee);
            assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, CreditCardType.Visa, Expect.Fee);
            assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, CreditCardType.VisaDebit, Expect.Fee);

            assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, CreditCardType.MasterCard, Expect.NoFee);
            assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, CreditCardType.Visa, Expect.Disable);
            assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, CreditCardType.VisaDebit, Expect.Disable);

            // On CashEquivalent
            for (CreditCardType creditCardType : CreditCardType.values()) {
                assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, creditCardType, Expect.Disable);
                assertAllowed(VistaApplication.crm, setup, true, selectionPolicy, creditCardType, Expect.Disable);
            }
        }

        // Accepted only MasterCard in portal; but disabled in general = will fallback to Fee model
        {
            PaymentTypeSelectionPolicy selectionPolicy = EntityFactory.create(PaymentTypeSelectionPolicy.class);
            selectionPolicy.residentPortalCreditCardMasterCard().setValue(true);

            assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, CreditCardType.MasterCard, Expect.Fee);
            assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, CreditCardType.Visa, Expect.Fee);
            assertAllowed(VistaApplication.resident, setup, false, selectionPolicy, CreditCardType.VisaDebit, Expect.Fee);

            for (CreditCardType creditCardType : CreditCardType.values()) {
                assertAllowed(VistaApplication.crm, setup, false, selectionPolicy, creditCardType, Expect.Disable);
            }

            // On CashEquivalent
            for (CreditCardType creditCardType : CreditCardType.values()) {
                assertAllowed(VistaApplication.resident, setup, true, selectionPolicy, creditCardType, Expect.Disable);
                assertAllowed(VistaApplication.crm, setup, true, selectionPolicy, creditCardType, Expect.Disable);
            }
        }
    }
}
