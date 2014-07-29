/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-02
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.math.BigDecimal;

import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;
import com.propertyvista.domain.pmc.fee.AbstractPaymentSetup;

public interface Vista2PmcFacade {

    AbstractEquifaxFee getEquifaxFee();

    BigDecimal getPmcPerApplicantFee();

    AbstractPaymentFees getPaymentFees();

    AbstractPaymentSetup getPaymentSetup();

    String getTenantSureMerchantTerminalId();

    String getVistaMerchantTerminalId();

    MerchantAccount calulateMerchantAccountStatus(MerchantAccount merchantAccount);
}
