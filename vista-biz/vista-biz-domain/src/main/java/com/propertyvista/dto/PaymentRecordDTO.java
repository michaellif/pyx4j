/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-02
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.SecurityEnabled;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

@Transient
@SecurityEnabled
@ExtendsBO(PaymentRecord.class)
public interface PaymentRecordDTO extends PaymentDataDTO, PaymentRecord {

    IList<LeaseTermParticipant<? extends LeaseParticipant<?>>> participants();

    IPrimitive<Boolean> rejectedWithNSF();

    @Editor(type = EditorType.label)
    @Caption(name = "Yardi Batch #")
    IPrimitive<String> externalBatchNumber();

    @Editor(type = EditorType.label)
    @Caption(name = "Yardi Reversal Batch #")
    IPrimitive<String> externalBatchNumberReversal();

}
