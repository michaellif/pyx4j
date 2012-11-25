/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import junit.framework.Assert;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.RpcEntityServiceFilter;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.FinancialTestBase;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.misc.CreditCardNumberGenerator;
import com.propertyvista.test.preloader.MerchantAccountDataModel;
import com.propertyvista.test.preloader.PmcDataModel;
import com.propertyvista.test.preloader.PreloadConfig;

public class PaymentTestBase extends FinancialTestBase {

    protected PmcDataModel pmcDataModel;

    protected MerchantAccountDataModel merchantAccountDataModel;

    @Override
    protected void preloadData(PreloadConfig config) {
        pmcDataModel = new PmcDataModel();
        pmcDataModel.generate();

        super.preloadData(config);

        merchantAccountDataModel = new MerchantAccountDataModel(config, buildingDataModel);
        merchantAccountDataModel.generate();

    }

    public List<LeasePaymentMethod> retrieveAllPaymentMethods() {
        EntityQueryCriteria<LeasePaymentMethod> criteria = new EntityQueryCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), tenantDataModel.getTenantCustomer()));
        return Persistence.service().query(criteria);
    }

    protected List<LeasePaymentMethod> retrieveProfilePaymentMethodsSerializable() {
        List<LeasePaymentMethod> profileMethods = ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(
                tenantDataModel.getTenantCustomer());
        RpcEntityServiceFilter.filterRpcTransient((Serializable) profileMethods);
        return profileMethods;
    }

    public void assertRpcTransientMemebers(List<LeasePaymentMethod> methods) {
        for (LeasePaymentMethod method : methods) {
            assertRpcTransientMemebers(method);
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void assertRpcTransientMemebers(LeasePaymentMethod paymentMethod) {
        switch (paymentMethod.type().getValue()) {
        case Echeck:
            EcheckInfo ec = paymentMethod.details().cast();
            Assert.assertNull(ec.accountNo().number().getValue());
            break;
        case CreditCard:
            CreditCardInfo cc = paymentMethod.details().cast();
            Assert.assertNull(cc.card().number().getValue());
            Assert.assertNull(cc.securityCode().getValue());
            break;
        }
    }

    protected LeasePaymentMethod createPaymentMethod(PaymentType type) {
        LeasePaymentMethod paymentMethod = EntityFactory.create(LeasePaymentMethod.class);
        paymentMethod.customer().set(tenantDataModel.getTenantCustomer());
        createPaymentMethodDetails(paymentMethod, type);
        return paymentMethod;
    }

    private void createPaymentMethodDetails(PaymentMethod paymentMethod, PaymentType type) {
        paymentMethod.type().setValue(type);
        switch (type) {
        case Echeck: {
            EcheckInfo details = EntityFactory.create(EcheckInfo.class);
            setEcheckInfoDetails(details);
            paymentMethod.details().set(details);
        }
            break;
        case CreditCard: {
            CreditCardInfo details = EntityFactory.create(CreditCardInfo.class);
            setCreditCardDetails(details);
            paymentMethod.details().set(details);
        }
            break;
        default:
            throw new IllegalArgumentException();
        }

    }

    protected void setNewPaymentMethodDetails(PaymentMethod paymentMethod) {
        switch (paymentMethod.type().getValue()) {
        case Echeck:
            setEcheckInfoDetails((EcheckInfo) paymentMethod.details().cast());
            break;
        case CreditCard:
            setCreditCardDetails((CreditCardInfo) paymentMethod.details().cast());
            break;
        default:
            throw new IllegalArgumentException();
        }

    }

    private void setEcheckInfoDetails(EcheckInfo details) {
        details.bankId().setValue(CommonsStringUtils.paddZerro(RandomUtil.randomInt(999), 3));
        details.branchTransitNumber().setValue(CommonsStringUtils.paddZerro(RandomUtil.randomInt(99999), 5));
        details.accountNo().newNumber().setValue(Integer.toString(RandomUtil.randomInt(99999)) + Integer.toString(RandomUtil.randomInt(999999)));
    }

    private void setCreditCardDetails(CreditCardInfo details) {
        details.cardType().setValue(CreditCardType.Visa);
        details.card().newNumber().setValue(CreditCardNumberGenerator.generateCardNumber(details.cardType().getValue()));
        details.expiryDate().setValue(new LogicalDate(2015 - 1900, 1, 1));
    }

    protected PaymentRecord createPaymentRecord(LeasePaymentMethod paymentMethod, String amount) {
        Lease lease = retrieveLease();
        // Just use the first tenant
        LeaseTermParticipant<?> leaseParticipant = lease.currentTerm().version().tenants().iterator().next();

        PaymentRecord paymentRecord = EntityFactory.create(PaymentRecord.class);
        paymentRecord.amount().setValue(new BigDecimal(amount));
        paymentRecord.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
        paymentRecord.billingAccount().set(lease.billingAccount());
        paymentRecord.leaseTermParticipant().set(leaseParticipant);

        paymentRecord.paymentMethod().set(paymentMethod);

        return paymentRecord;
    }
}
