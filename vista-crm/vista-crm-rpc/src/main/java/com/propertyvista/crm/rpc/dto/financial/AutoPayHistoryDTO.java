/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.financial;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.payment.AutopayAgreement;

@Transient
@ExtendsBO
public interface AutoPayHistoryDTO extends AutopayAgreement {

    @ReadOnly
    @Format("#,##0.00")
    @Editor(type = EditorType.moneylabel)
    @Caption(name = "Price total")
    IPrimitive<BigDecimal> price();

    @ReadOnly
    @Format("#,##0.00")
    @Editor(type = EditorType.moneylabel)
    @Caption(name = "Payment total")
    IPrimitive<BigDecimal> payment();

    IPrimitive<String> auditDetails();
}
