/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-23
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.domain.payment;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface EcheckInfo extends IEntity {

    public enum AccountType {
        Chequing, Saving
    }

    @NotNull
    IPrimitive<String> nameOnAccount();

    @NotNull
    IPrimitive<AccountType> accountType();

    @NotNull
    IPrimitive<String> bankName();

    @NotNull
    @Caption(name = "Routing Number")
    IPrimitive<Integer> routingNo();

    @NotNull
    @Caption(name = "Account Number")
    IPrimitive<Integer> accountNo();

    @NotNull
    @Caption(name = "Check Number")
    IPrimitive<Integer> checkNo();

}
