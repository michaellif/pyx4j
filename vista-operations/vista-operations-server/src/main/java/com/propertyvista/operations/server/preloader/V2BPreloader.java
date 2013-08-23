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
package com.propertyvista.operations.server.preloader;

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.operations.domain.vista2pmc.DefaultEquifaxFee;
import com.propertyvista.operations.domain.vista2pmc.DefaultEquifaxLimit;
import com.propertyvista.operations.domain.vista2pmc.DefaultPaymentFees;
import com.propertyvista.operations.domain.vista2pmc.TenantSureMerchantAccount;
import com.propertyvista.operations.domain.vista2pmc.VistaMerchantAccount;

public class V2BPreloader extends AbstractDataPreloader {

    @Override
    public String create() {
        {
            TenantSureMerchantAccount ma = EntityFactory.create(TenantSureMerchantAccount.class);
            ma.merchantTerminalId().setValue("BIRCHWT6");
            ma.bankId().setValue("000");
            ma.branchTransitNumber().setValue("00000");
            ma.accountNumber().setValue("000000000000");
            Persistence.service().persist(ma);
        }
        {
            VistaMerchantAccount ma = EntityFactory.create(VistaMerchantAccount.class);
            ma.accountType().setValue(VistaMerchantAccount.AccountType.PaymentAggregation);
            ma.merchantTerminalId().setValue("BIRCHWT6");
            ma.bankId().setValue("007");
            ma.branchTransitNumber().setValue("65985");
            ma.accountNumber().setValue("154587459");
            Persistence.service().persist(ma);
        }
        {
            VistaMerchantAccount ma = EntityFactory.create(VistaMerchantAccount.class);
            ma.accountType().setValue(VistaMerchantAccount.AccountType.Equifax);
            ma.merchantTerminalId().setValue("BIRCHWT6");
            ma.bankId().setValue("007");
            ma.branchTransitNumber().setValue("65985");
            ma.accountNumber().setValue("154587459");
            Persistence.service().persist(ma);
        }
        {
            DefaultEquifaxFee fee = EntityFactory.create(DefaultEquifaxFee.class);
            fee.recommendationReportPerApplicantFee().setValue(new BigDecimal("19.99"));
            fee.recommendationReportSetUpFee().setValue(new BigDecimal("0"));
            fee.fullCreditReportPerApplicantFee().setValue(new BigDecimal("19.11"));
            fee.fullCreditReportSetUpFee().setValue(new BigDecimal("150"));
            Persistence.service().persist(fee);
        }
        {
            DefaultPaymentFees fee = EntityFactory.create(DefaultPaymentFees.class);
            fee.ccVisaFee().setValue(new BigDecimal("0.015"));
            fee.visaDebitFee().setValue(new BigDecimal("0.0177"));
            fee.ccMasterCardFee().setValue(new BigDecimal("0.0222"));
            fee.ccDiscoverFee().setValue(null);
            fee.ccAmexFee().setValue(null);
            fee.eChequeFee().setValue(new BigDecimal("1.50"));
            fee.directBankingFee().setValue(new BigDecimal("1.50"));
            fee.interacCaledonFee().setValue(new BigDecimal("1.50"));
            fee.interacPaymentPadFee().setValue(new BigDecimal("19.99"));
            fee.interacVisaFee().setValue(new BigDecimal("0.75"));
            Persistence.service().persist(fee);
        }
        {
            DefaultEquifaxLimit limit = EntityFactory.create(DefaultEquifaxLimit.class);
            limit.dailyRequests().setValue(50);
            limit.dailyReports().setValue(50);
            Persistence.service().persist(limit);
        }
        {
            ServerSideFactory.create(EncryptedStorageFacade.class).preloaderTestKey();
        }
        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
