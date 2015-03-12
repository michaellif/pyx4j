/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-19
 * @author ArtyomB
 */
package com.propertyvista.dto.leaseapplicationdocument;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

@Transient
public interface LeaseApplicationDocumentDataAboutYouSectionDTO extends IEntity {

    // Personal Information:
    IPrimitive<String> firstName();

    IPrimitive<String> lastName();

    IPrimitive<String> middleName();

    IPrimitive<String> namePrefix();

    IPrimitive<String> nameSuffix();

    IPrimitive<String> gender();

    IPrimitive<LogicalDate> birthDate();

    // Contact Information:
    IPrimitive<String> homePhone();

    IPrimitive<String> mobilePhone();

    IPrimitive<String> workPhone();

    IPrimitive<String> email();

    // Identification Documents

    IList<LeaseApplicationDocumentDataIdentificationDocumentDTO> identificationDocuments();

    // Header Information

    IPrimitive<String> landlordLogo();

    IPrimitive<String> leaseId();

    IPrimitive<LogicalDate> submissionDate();

}
