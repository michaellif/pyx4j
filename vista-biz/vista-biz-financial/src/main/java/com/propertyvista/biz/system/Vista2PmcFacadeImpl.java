/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.Callable;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.payment.MerchantTerminalSourceTenantSure;
import com.propertyvista.biz.financial.payment.MerchantTerminalSourceVista;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcEquifaxInfo;
import com.propertyvista.domain.pmc.PmcPaymentTypeInfo;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.domain.pmc.fee.AbstractPaymentSetup;
import com.propertyvista.domain.pmc.fee.PmcEquifaxFee;
import com.propertyvista.operations.domain.vista2pmc.DefaultEquifaxFee;
import com.propertyvista.operations.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.server.TaskRunner;

public class Vista2PmcFacadeImpl implements Vista2PmcFacade {

    @SuppressWarnings("unchecked")
    private static <S extends Serializable> void setNonNullMember(IPrimitive<S> dst, IEntity... srcs) {
        for (IEntity src : srcs) {
            if (!src.getMember(dst.getFieldName()).isNull()) {
                dst.set((IPrimitive<S>) src.getMember(dst.getFieldName()));
                break;
            }
        }
    }

    @Override
    public AbstractEquifaxFee getEquifaxFee() {
        DefaultEquifaxFee defaultEfxFeee = TaskRunner.runInOperationsNamespace(new Callable<DefaultEquifaxFee>() {
            @Override
            public DefaultEquifaxFee call() {
                return Persistence.service().retrieve(EntityQueryCriteria.create(DefaultEquifaxFee.class));
            }
        });
        final Pmc pmc = VistaDeployment.getCurrentPmc().duplicate();
        TaskRunner.runInOperationsNamespace(new Callable<PmcEquifaxFee>() {
            @Override
            public PmcEquifaxFee call() {
                Persistence.service().retrieveMember(pmc.equifaxFee());
                return pmc.equifaxFee();
            }
        });

        AbstractEquifaxFee fee = EntityFactory.create(AbstractEquifaxFee.class);
        setNonNullMember(fee.recommendationReportPerApplicantFee(), pmc.equifaxFee(), defaultEfxFeee);
        setNonNullMember(fee.recommendationReportSetUpFee(), pmc.equifaxFee(), defaultEfxFeee);
        setNonNullMember(fee.fullCreditReportPerApplicantFee(), pmc.equifaxFee(), defaultEfxFeee);
        setNonNullMember(fee.fullCreditReportSetUpFee(), pmc.equifaxFee(), defaultEfxFeee);

        fee.taxRate().setValue(defaultEfxFeee.taxRate().getValue());

        return fee;
    }

    @Override
    public BigDecimal getPmcPerApplicantFee() {
        final Pmc pmc = VistaDeployment.getCurrentPmc().duplicate();
        TaskRunner.runInOperationsNamespace(new Callable<PmcEquifaxInfo>() {
            @Override
            public PmcEquifaxInfo call() {
                Persistence.ensureRetrieve(pmc.equifaxInfo(), AttachLevel.Attached);
                return pmc.equifaxInfo();
            }
        });

        AbstractEquifaxFee fee = getEquifaxFee();
        switch (pmc.equifaxInfo().reportType().getValue()) {
        case FullCreditReport:
            return fee.fullCreditReportPerApplicantFee().getValue();
        case RecomendationReport:
            return fee.recommendationReportPerApplicantFee().getValue();
        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    public AbstractPaymentFees getPaymentFees() {
        final Pmc pmc = VistaDeployment.getCurrentPmc();
        DefaultPaymentFees systemDefaultFeee = TaskRunner.runInOperationsNamespace(new Callable<DefaultPaymentFees>() {
            @Override
            public DefaultPaymentFees call() {
                Persistence.ensureRetrieve(pmc.paymentTypeInfo(), AttachLevel.Attached);
                return Persistence.service().retrieve(EntityQueryCriteria.create(DefaultPaymentFees.class));
            }
        });
        PmcPaymentTypeInfo pmcFee = pmc.paymentTypeInfo();

        AbstractPaymentFees fee = EntityFactory.create(AbstractPaymentFees.class);
        setNonNullMember(fee.ccVisaFee(), pmcFee, systemDefaultFeee);
        setNonNullMember(fee.ccMasterCardFee(), pmcFee, systemDefaultFeee);
        setNonNullMember(fee.ccDiscoverFee(), pmcFee, systemDefaultFeee);
        setNonNullMember(fee.ccAmexFee(), pmcFee, systemDefaultFeee);
        setNonNullMember(fee.eChequeFee(), pmcFee, systemDefaultFeee);
        setNonNullMember(fee.directBankingFee(), pmcFee, systemDefaultFeee);
        setNonNullMember(fee.interacCaledonFee(), pmcFee, systemDefaultFeee);
        setNonNullMember(fee.interacPaymentPadFee(), pmcFee, systemDefaultFeee);
        setNonNullMember(fee.interacVisaFee(), pmcFee, systemDefaultFeee);
        setNonNullMember(fee.visaDebitFee(), pmcFee, systemDefaultFeee);

        // System Setup OFF will override Pmc setup ON
        combinePmcAndSystem(fee.acceptedEcheck(), pmcFee, systemDefaultFeee);
        combinePmcAndSystem(fee.acceptedDirectBanking(), pmcFee, systemDefaultFeee);
        combinePmcAndSystem(fee.acceptedVisa(), pmcFee, systemDefaultFeee);
        combinePmcAndSystem(fee.acceptedVisaConvenienceFee(), pmcFee, systemDefaultFeee);
        combinePmcAndSystem(fee.acceptedVisaDebit(), pmcFee, systemDefaultFeee);
        combinePmcAndSystem(fee.acceptedVisaDebitConvenienceFee(), pmcFee, systemDefaultFeee);
        combinePmcAndSystem(fee.acceptedMasterCard(), pmcFee, systemDefaultFeee);
        combinePmcAndSystem(fee.acceptedMasterCardConvenienceFee(), pmcFee, systemDefaultFeee);

        if (!fee.acceptedVisa().getValue()) {
            fee.acceptedVisaConvenienceFee().setValue(false);
        }
        if (!fee.acceptedVisaDebit().getValue()) {
            fee.acceptedVisaDebitConvenienceFee().setValue(false);
        }
        if (!fee.acceptedMasterCard().getValue()) {
            fee.acceptedMasterCardConvenienceFee().setValue(false);
        }

        return fee;
    }

    @SuppressWarnings("unchecked")
    private static void combinePmcAndSystem(IPrimitive<Boolean> dst, IEntity pmcSettings, IEntity systemSettings) {
        boolean systemValue = ((IPrimitive<Boolean>) systemSettings.getMember(dst.getFieldName())).getValue();
        if (!systemValue) {
            dst.setValue(false);
        } else {
            Boolean pmcValue = ((IPrimitive<Boolean>) pmcSettings.getMember(dst.getFieldName())).getValue();
            if (pmcValue == Boolean.FALSE) {
                dst.setValue(false);
            } else {
                dst.setValue(true);
            }
        }
    }

    @Override
    public AbstractPaymentSetup getPaymentSetup() {
        return getPaymentFees().duplicate(AbstractPaymentSetup.class);
    }

    @Override
    public String getTenantSureMerchantTerminalId() {
        return new MerchantTerminalSourceTenantSure().getMerchantTerminalId();
    }

    @Override
    public String getVistaMerchantTerminalId() {
        return new MerchantTerminalSourceVista().getMerchantTerminalId();
    }

    @Override
    public MerchantAccount calulateMerchantAccountStatus(MerchantAccount merchantAccount) {
        if (merchantAccount.invalid().getValue(Boolean.TRUE)) {
            merchantAccount.paymentsStatus().setValue(MerchantAccount.MerchantAccountPaymentsStatus.Invalid);
        } else if (merchantAccount.merchantTerminalId().isNull() || (merchantAccount.status().getValue() != MerchantAccountActivationStatus.Active)) {
            merchantAccount.paymentsStatus().setValue(MerchantAccount.MerchantAccountPaymentsStatus.NoElectronicPaymentsAllowed);
        } else {
            merchantAccount.paymentsStatus().setValue(MerchantAccount.MerchantAccountPaymentsStatus.ElectronicPaymentsAllowed);
        }
        return merchantAccount;
    }

}
