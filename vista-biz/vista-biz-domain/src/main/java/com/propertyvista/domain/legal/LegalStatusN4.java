/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;

@DiscriminatorValue("LegalStatusN4")
public interface LegalStatusN4 extends LegalStatus {

    /**
     * Maximum possible outstanding balance of rent related charges that cancels this N4 notice, i.e. if equals <code>$0.00</code> tenant is supposed to pay
     * everything he owes, if <code>$100.00</code> tenant will need pay everything but the last $100 to cancel this notice.
     */
    @Editor(type = EditorType.money)
    @NotNull
    IPrimitive<BigDecimal> cancellationThreshold();

    /** This is the last date until which tenant is supposed pay (or leave), after that date an eviction process can be initiated. */
    @NotNull
    IPrimitive<LogicalDate> terminationDate();

}
