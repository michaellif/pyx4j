/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-19
 * @author ArtyomB
 */
package com.propertyvista.biz.legal;

import java.util.Collection;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.legal.errors.FormFillError;
import com.propertyvista.domain.legal.n4.N4DeliveryMethod;
import com.propertyvista.domain.legal.n4.pdf.N4PdfBatchData;
import com.propertyvista.domain.legal.n4.pdf.N4PdfFormData;
import com.propertyvista.domain.legal.n4.pdf.N4PdfLeaseData;
import com.propertyvista.domain.tenant.lease.Lease;

public interface N4GenerationFacade {

    byte[] generateN4Letter(N4PdfFormData formData);

    N4PdfFormData prepareFormData(N4PdfLeaseData leaseData, N4PdfBatchData batchData) throws FormFillError;

    N4PdfLeaseData prepareN4LeaseData(Lease leaseId, LogicalDate noticeDate, N4DeliveryMethod deliveryMethod, Collection<ARCode> acceptedARCodes);
}
