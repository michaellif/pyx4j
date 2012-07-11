/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 10, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.onboarding.example.model;

import javax.validation.constraints.Size;

public class BankAccountInfoApproval extends BankAccountInfo {

    @Size(max = 8)
    public String terminalId;
}
