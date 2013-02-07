/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.financial.yardi;

import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.dto.LeaseYardiFinancialInfoDTO;

// TODO VISTA-2492: probably needs to refactored and merged with ARFacade.getTransactionHistory() 
public interface FinancialStatusFacade {

    LeaseYardiFinancialInfoDTO getFinancialStatus(YardiBillingAccount billingAccountIdentityStub);

}
