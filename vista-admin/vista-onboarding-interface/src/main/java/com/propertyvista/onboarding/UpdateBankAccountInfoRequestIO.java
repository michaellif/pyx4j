/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.onboarding;

import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

public interface UpdateBankAccountInfoRequestIO extends RequestIO {

    IPrimitive<String> merchantTerminalId();

    @NotNull
    IPrimitive<String> bankId();

    @NotNull
    IPrimitive<String> branchTransitNumber();

    @NotNull
    IPrimitive<String> accountNumber();
}
