/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.financial.moneyin.batch;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.PaymentRecord;

@Transient
public interface DepositSlipCheckDetailsRecordDTO extends IEntity {

    IPrimitive<PaymentRecord.PaymentStatus> status();

    IPrimitive<String> unit();

    IPrimitive<String> tenantId();

    IPrimitive<String> tenantName();

    IPrimitive<String> checkNumber();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    /** this is date of the check: what is called in wikipedia 'date of issue' */
    IPrimitive<LogicalDate> date();

}
