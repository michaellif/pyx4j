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

import com.propertyvista.biz.financial.payment.MerchantTerminalSourceTenantSure;
import com.propertyvista.biz.financial.payment.MerchantTerminalSourceVista;
import com.propertyvista.domain.pmc.fee.AbstractEquifaxFee;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;

public class Vista2PmcFacadeImpl implements Vista2PmcFacade {

    @Override
    public AbstractEquifaxFee getEquifaxFee() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractPaymentFees getPaymentFees() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTenantSureMerchantTerminalId() {
        return new MerchantTerminalSourceTenantSure().getMerchantTerminalId();
    }

    @Override
    public String getVistaMerchantTerminalId() {
        return new MerchantTerminalSourceVista().getMerchantTerminalId();
    }

}
