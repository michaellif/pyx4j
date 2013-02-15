/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.rpc;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface TenantSureCcTaransactionsReportLineDTO extends IEntity {

    IPrimitive<Date> date();

    IPrimitive<String> insuranceCertificateNumber();

    IPrimitive<String> tenant();

    IPrimitive<String> creditCardType();

    IPrimitive<String> creditCardNumber();

    IPrimitive<BigDecimal> amount();

    IPrimitive<String> transactionReferenceNumber();
}
