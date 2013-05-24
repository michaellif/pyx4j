/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-24
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.payment.PreauthorizedPayment;

@Transient
public interface PreauthorizedPaymentDTO extends PreauthorizedPayment {

    @Owned
    IList<PreauthorizedPaymentCoveredItemDTO> coveredItemsDTO();
}
