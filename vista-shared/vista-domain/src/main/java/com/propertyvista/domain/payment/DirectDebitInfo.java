/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 31, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.payment;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@Table(prefix = "payment")
@DiscriminatorValue("DirectDebit")
public interface DirectDebitInfo extends PaymentDetails {

    //Name of the payer if provided by the source location code telebanking operator
    @Caption(name = "Customer name")
    IPrimitive<String> nameOn();

    //If the source location (e.g. TDBANK) sends a trace number for their incoming file, then that trace number is referenced here. Each source location can have a different trace number format.
    IPrimitive<String> traceNumber();

    //Location code assigned to each source (e.g. BMO is 1001)
    IPrimitive<String> locationCode();
}
