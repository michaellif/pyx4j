/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto.financial;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IList;

import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.dto.PaymentRecordDTO;

@Transient
public interface PaymentDTO extends PaymentRecordDTO {

    IList<PaymentInfoDTO> currentAutoPayments();

    @Owned
    @Detached
    @Caption(name = "I agree to the service fee being charged and have read the applicable terms and conditions")
    CustomerSignature convenienceFeeSignature();
}
