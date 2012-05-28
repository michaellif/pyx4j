/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@AbstractEntity
public interface AbstractMerchantAccount extends IEntity {

    @Length(8)
    @RpcTransient
    IPrimitive<String> merchantTerminalId();

    @Length(3)
    @ToString
    IPrimitive<String> bankId();

    @Length(5)
    @ToString
    IPrimitive<String> branchTransitNumber();

    @Length(12)
    @ToString
    IPrimitive<String> accountNumber();

    // filed editable by CRM
    /**
     * Caledon: Description to appear on client's statement. Typically a merchant's business name.
     */
    @Length(60)
    @Caption(description = "Description to appear on client's statement. Typically a merchant's business name.")
    IPrimitive<String> chargeDescription();
}
