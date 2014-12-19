/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 2, 2014
 * @author vlads
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IHasFile;

import com.propertyvista.domain.blob.LeaseTermAgreementDocumentBlob;
import com.propertyvista.domain.security.CrmUser;

public interface LeaseTermAgreementDocument extends IHasFile<LeaseTermAgreementDocumentBlob> {

    @Owner
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    @MemberColumn(notNull = true)
    LeaseTerm leaseTerm();

    IPrimitive<Boolean> isSignedByInk();

    // The same as in payment who he was at the time of document creation
    IList<LeaseTermParticipant<?>> signedParticipants();

    @Caption(name = "Signed Employee / Uploader")
    CrmUser signedEmployeeUploader();
}
