/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.financial.moneyin.batch;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

@Transient
public interface MoneyInBatchDTO extends IEntity {

    IPrimitive<String> building();

    IPrimitive<String> bankAccountName();

    IPrimitive<String> bankId();

    IPrimitive<String> bankTransitNumber();

    IPrimitive<String> bankAccountNumber();

    IPrimitive<Integer> depositSlipNumber();

    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> totalReceivedAmount();

    IPrimitive<Integer> numberOfReceipts();

    // TODO may be an entity or Enum
    @Caption(name = "Posted")
    IPrimitive<Boolean> isPosted();

    IList<DepositSlipPaymentRecordDTO> payments();

    IPrimitive<LogicalDate> depositDate();

}
