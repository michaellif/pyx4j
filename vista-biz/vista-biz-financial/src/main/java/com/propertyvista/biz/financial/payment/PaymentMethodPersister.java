/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import org.apache.commons.lang.Validate;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityDiff;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.biz.financial.payment.CreditCardProcessor.MerchantTerminalSource;
import com.propertyvista.biz.financial.payment.CreditCardProcessor.MerchantTerminalSourceBuilding;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.domain.payment.CashInfo;
import com.propertyvista.domain.payment.CheckInfo;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.DirectDebitInfo;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.insurance.TenantSureTransaction;
import com.propertyvista.domain.util.DomainUtil;

class PaymentMethodPersister {

    private static boolean isAccountNumberChange(AbstractPaymentMethod paymentMethod, AbstractPaymentMethod origPaymentMethod) {
        switch (paymentMethod.type().getValue()) {
        case Echeck:
            EcheckInfo eci = paymentMethod.details().cast();
            if (!eci.accountNo().newNumber().isNull()) {
                return true;
            }
            EcheckInfo origeci = origPaymentMethod.details().cast();
            if (!EntityGraph.membersEquals(eci, origeci, eci.bankId(), eci.branchTransitNumber())) {
                return true;
            }
            break;
        case CreditCard:
            CreditCardInfo cc = paymentMethod.details().cast();
            if (!cc.card().newNumber().isNull()) {
                return true;
            }
            CreditCardInfo origcc = origPaymentMethod.details().cast();
            if (!EntityGraph.membersEquals(cc, origcc, cc.cardType())) {
                return true;
            }
            break;
        default:
            break;
        }
        return false;
    }

    static LeasePaymentMethod persistLeasePaymentMethod(Building building, LeasePaymentMethod paymentMethod) {
        LeasePaymentMethod origPaymentMethod = null;
        if (!paymentMethod.id().isNull()) {
            // Keep history of payment methods that were used.
            origPaymentMethod = Persistence.service().retrieve(LeasePaymentMethod.class, paymentMethod.getPrimaryKey());
            if (isAccountNumberChange(paymentMethod, origPaymentMethod)) {
                EntityQueryCriteria<PaymentRecord> criteria = EntityQueryCriteria.create(PaymentRecord.class);
                criteria.eq(criteria.proto().paymentMethod(), paymentMethod);
                criteria.ne(criteria.proto().paymentStatus(), PaymentStatus.Submitted);
                if (Persistence.service().count(criteria) != 0) {
                    origPaymentMethod.isDeleted().setValue(true);
                    Persistence.service().merge(origPaymentMethod);
                    EntityGraph.makeDuplicate(paymentMethod);
                    switch (paymentMethod.type().getValue()) {
                    case CreditCard:
                        CreditCardInfo cc = paymentMethod.details().cast();
                        cc.token().setValue(null);
                        break;
                    case Echeck:
                        EcheckInfo eci = paymentMethod.details().cast();
                        if (eci.accountNo().newNumber().isNull()) {
                            EcheckInfo origeci = origPaymentMethod.details().cast();
                            eci.accountNo().newNumber().setValue(origeci.accountNo().number().getValue());
                        }
                    default:
                        break;
                    }
                    origPaymentMethod = null;
                }
            }
        } else {
            paymentMethod.createdBy().set(VistaContext.getCurrentUserIfAvalable());
        }

        Validate.isTrue(!paymentMethod.customer().isNull(), "Owner (customer) is required for PaymentMethod");

        return persistPaymentMethod(paymentMethod, origPaymentMethod, new MerchantTerminalSourceBuilding(building));
    }

    static InsurancePaymentMethod persistInsurancePaymentMethod(InsurancePaymentMethod paymentMethod) {
        InsurancePaymentMethod origPaymentMethod = null;
        if (!paymentMethod.id().isNull()) {
            // Keep history of payment methods that were used.
            origPaymentMethod = Persistence.service().retrieve(InsurancePaymentMethod.class, paymentMethod.getPrimaryKey());
            if (isAccountNumberChange(paymentMethod, origPaymentMethod)) {
                EntityQueryCriteria<TenantSureTransaction> criteria = EntityQueryCriteria.create(TenantSureTransaction.class);
                criteria.eq(criteria.proto().paymentMethod(), paymentMethod);
                criteria.ne(criteria.proto().status(), TenantSureTransaction.TransactionStatus.Draft);
                if (Persistence.service().count(criteria) != 0) {
                    origPaymentMethod.isDeleted().setValue(true);
                    Persistence.service().merge(origPaymentMethod);
                    EntityGraph.makeDuplicate(paymentMethod);
                    switch (paymentMethod.type().getValue()) {
                    case CreditCard:
                        CreditCardInfo cc = paymentMethod.details().cast();
                        cc.token().setValue(null);
                        break;
                    case Echeck:
                        EcheckInfo eci = paymentMethod.details().cast();
                        if (eci.accountNo().newNumber().isNull()) {
                            EcheckInfo origeci = origPaymentMethod.details().cast();
                            eci.accountNo().newNumber().setValue(origeci.accountNo().number().getValue());
                        }
                    default:
                        break;
                    }
                    origPaymentMethod = null;
                }
            }
        }
        Validate.isTrue(!paymentMethod.tenant().isNull(), "Owner (tenant) is required for PaymentMethod");

        return persistPaymentMethod(paymentMethod, origPaymentMethod, new MerchantTerminalSourceTenantSure());
    }

    static PmcPaymentMethod persistPmcPaymentMethod(PmcPaymentMethod paymentMethod) {
        PmcPaymentMethod origPaymentMethod = null;
        if (!paymentMethod.id().isNull()) {
            // Keep history of payment methods that were used.
            origPaymentMethod = Persistence.service().retrieve(PmcPaymentMethod.class, paymentMethod.getPrimaryKey());
            if (isAccountNumberChange(paymentMethod, origPaymentMethod)) {
                EntityQueryCriteria<TenantSureTransaction> criteria = EntityQueryCriteria.create(TenantSureTransaction.class);
                criteria.eq(criteria.proto().paymentMethod(), paymentMethod);
                criteria.ne(criteria.proto().status(), TenantSureTransaction.TransactionStatus.Draft);
                if (Persistence.service().count(criteria) != 0) {
                    origPaymentMethod.isDeleted().setValue(true);
                    Persistence.service().merge(origPaymentMethod);
                    EntityGraph.makeDuplicate(paymentMethod);
                    switch (paymentMethod.type().getValue()) {
                    case CreditCard:
                        CreditCardInfo cc = paymentMethod.details().cast();
                        cc.token().setValue(null);
                        break;
                    case Echeck:
                        EcheckInfo eci = paymentMethod.details().cast();
                        if (eci.accountNo().newNumber().isNull()) {
                            EcheckInfo origeci = origPaymentMethod.details().cast();
                            eci.accountNo().newNumber().setValue(origeci.accountNo().number().getValue());
                        }
                    default:
                        break;
                    }
                    origPaymentMethod = null;
                }
            }
        }
        Validate.isTrue(!paymentMethod.pmc().isNull(), "Owner (pmc) is required for PaymentMethod");

        return persistPaymentMethod(paymentMethod, origPaymentMethod, new MerchantTerminalSourceVista());
    }

    static <E extends AbstractPaymentMethod> E persistPaymentMethod(E paymentMethod, E origPaymentMethod, MerchantTerminalSource merchantTerminalSource) {
        if (paymentMethod.id().isNull()) {
            // New Value validation
            switch (paymentMethod.type().getValue()) {
            case CreditCard:
                CreditCardInfo cc = paymentMethod.details().cast();
                Validate.isTrue(cc.token().isNull(), "Can't attach to token");
                break;
            default:
                break;
            }
        }

        paymentMethod.isDeleted().setValue(Boolean.FALSE);

        switch (paymentMethod.type().getValue()) {
        case Echeck:
            EcheckInfo eci = paymentMethod.details().cast();
            if (!eci.accountNo().newNumber().isNull()) {
                eci.accountNo().number().setValue(eci.accountNo().newNumber().getValue());
                eci.accountNo().obfuscatedNumber().setValue(DomainUtil.obfuscateAccountNumber(eci.accountNo().number().getValue()));
            } else {
                Validate.isTrue(!paymentMethod.details().id().isNull(), "Account number is required when creating Echeck");
                EcheckInfo origeci = origPaymentMethod.details().cast();
                eci.accountNo().number().setValue(origeci.accountNo().number().getValue());
                Validate.isTrue(eci.accountNo().obfuscatedNumber().equals(origeci.accountNo().obfuscatedNumber()), "obfuscatedNumber changed");
            }
            break;
        case CreditCard:
            //Verify CC change
            CreditCardInfo cc = paymentMethod.details().cast();
            if (!paymentMethod.details().id().isNull()) {
                CreditCardInfo origcc = origPaymentMethod.details().cast();
                if (cc.card().newNumber().isNull()) {
                    Validate.isTrue(cc.card().obfuscatedNumber().equals(origcc.card().obfuscatedNumber()), "obfuscatedNumber changed");
                }
            }
            break;
        case Cash:
            // Assert value type
            Validate.isTrue(paymentMethod.details().isInstanceOf(CashInfo.class));
            break;
        case Check:
            // Assert value type
            Validate.isTrue(paymentMethod.details().isInstanceOf(CheckInfo.class));
            break;
        case DirectBanking:
            Validate.isTrue(paymentMethod.details().isInstanceOf(DirectDebitInfo.class));
            break;
        default:
            throw new IllegalArgumentException("Unsupported PaymentMethod type " + paymentMethod.type().getValue());
        }

        Persistence.service().merge(paymentMethod);

        switch (paymentMethod.type().getValue()) {
        case CreditCard:
            CreditCardInfo cc = paymentMethod.details().cast();
            if (!cc.card().newNumber().isNull()) {
                cc.card().number().setValue(cc.card().newNumber().getValue());
                cc.card().obfuscatedNumber().setValue(DomainUtil.obfuscateCreditCardNumber(cc.card().newNumber().getValue()));
            }
            // Allow to update expiryDate or create token
            boolean needUpdate = (origPaymentMethod == null);
            needUpdate |= (!cc.card().newNumber().isNull());
            if (origPaymentMethod != null) {
                needUpdate |= (!EntityGraph.membersEquals(cc, origPaymentMethod.details().cast(), cc.expiryDate()));
            }
            if (needUpdate) {
                ServerSideFactory.create(CreditCardFacade.class).persistToken(merchantTerminalSource.getMerchantTerminalId(), cc);
                Persistence.service().merge(cc);
            }
            break;
        default:
            break;
        }
        if (origPaymentMethod == null) {
            ServerSideFactory.create(AuditFacade.class).created(paymentMethod);
        } else {
            String diff = EntityDiff.getChanges(origPaymentMethod, paymentMethod);
            if (diff.length() > 0) {
                ServerSideFactory.create(AuditFacade.class).updated(paymentMethod, diff);
            }
        }

        return paymentMethod;
    }

}
