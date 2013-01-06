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
package com.propertyvista.admin.server.preloader;

import java.math.BigDecimal;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.domain.vista2pmc.DefaultEquifaxFee;
import com.propertyvista.admin.domain.vista2pmc.TenantSureMerchantAccount;
import com.propertyvista.admin.domain.vista2pmc.VistaMerchantAccount;

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
            ma.merchantTerminalId().setValue("BIRCHWT6");
            ma.bankId().setValue("000");
            ma.branchTransitNumber().setValue("00000");
            ma.accountNumber().setValue("000000000000");
            Persistence.service().persist(ma);
        }
        {
            DefaultEquifaxFee efxFeee = EntityFactory.create(DefaultEquifaxFee.class);
            efxFeee.recommendationReportPerApplicantFee().setValue(new BigDecimal("19.99"));
            efxFeee.recommendationReportSetUpFee().setValue(new BigDecimal("0"));
            efxFeee.fullCreditReportPerApplicantFee().setValue(new BigDecimal("19.11"));
            efxFeee.fullCreditReportSetUpFee().setValue(new BigDecimal("150"));
            Persistence.service().persist(efxFeee);
        }
        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
