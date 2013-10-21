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
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.legal.N4FormFieldsData;
import com.propertyvista.domain.legal.N4LandlordsData;
import com.propertyvista.domain.legal.N4LeaseData;
import com.propertyvista.domain.tenant.lease.Lease;

public interface N4GenerationFacade {

    byte[] generateN4Letter(N4FormFieldsData formData);

    N4FormFieldsData populateFormData(N4LeaseData leaseData, N4LandlordsData landlordsData);

    N4LeaseData prepareN4LeaseData(Lease leaseId, LogicalDate noticeDate);
}
