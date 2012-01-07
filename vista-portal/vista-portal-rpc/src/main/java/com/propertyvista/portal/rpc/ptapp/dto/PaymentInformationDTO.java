/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-23
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.rpc.ptapp.dto;

import com.propertyvista.portal.domain.ptapp.PaymentInformation;
import com.pyx4j.entity.annotations.Transient;

@Transient
public interface PaymentInformationDTO extends PaymentInformation {

    LegalTermsDescriptorDTO oneTimePaymentTerms();

    LegalTermsDescriptorDTO recurrentPaymentTerms();
}
