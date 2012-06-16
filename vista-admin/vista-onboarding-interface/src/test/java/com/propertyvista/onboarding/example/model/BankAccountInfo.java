/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 15, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.onboarding.example.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BankAccountInfo {
    @NotNull
    public String onboardingBankAccountId;

    @Size(max = 8)
    public String terminalId;

    @NotNull
    @Size(max = 8)
    public String bankId;

    @NotNull
    @Size(max = 5)
    public String branchTransitNumber;

    @NotNull
    @Size(max = 12)
    public String accountNumber;

    @Size(max = 60)
    public String chargeDescription;
}
