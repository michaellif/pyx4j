/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-18
 * @author ArtyomB
 */
package com.propertyvista.dto.leaseapplicationdocument;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.dto.LeaseAgreementDocumentLegalTermTenantDTO;

/** Represents data needed to create printout of Lease Application */
@Transient
public interface LeaseApplicationDocumentDataDTO extends IEntity {

    IPrimitive<String> landlordName();

    IPrimitive<String> landlordAddress();

    IPrimitive<String> landlordLogo();

    IPrimitive<LogicalDate> submissionDate();

    IPrimitive<String> leaseId();

    IList<LeaseAgreementDocumentLegalTermTenantDTO> applicants();

    IList<LeaseApplicationDocumentDataSectionsDTO> sections();

    IPrimitive<byte[]> background();

    IPrimitive<String> name();

    IPrimitive<LogicalDate> date();

}
