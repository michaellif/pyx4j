/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.tenant.prospect;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.shared.IHasFile;

import com.propertyvista.domain.blob.LeaseApplicationDocumentBlob;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

/**
 * This holds the a document that contains the Application agreement of a customer. <br>
 * 
 * It can be created in two ways:
 * <ul>
 * <li>Uploaded by a CRM user: in that case it's considered 'Signed by Ink'</li>
 * <li>Digitally signed by prospect tenant via PTApp: in that case it's considered 'Digitally Signed' which is not 'Signed by Ink'</li>
 * </ul>
 */
public interface LeaseApplicationDocument extends IHasFile<LeaseApplicationDocumentBlob> {

    @Owner
    @ReadOnly
    @Detached
    @Indexed
    @JoinColumn
    @MemberColumn(notNull = true)
    Lease lease();

    @Caption(name = "Uploaded By")
    @ReadOnly
    @MemberColumn(notNull = true)
    CrmUser uploader();

    @NotNull
    @ReadOnly
    @MemberColumn(notNull = true)
    IPrimitive<Boolean> isSignedByInk();

    @NotNull
    @ReadOnly
    @MemberColumn(notNull = true)
    Customer signedBy();

    @NotNull
    @ReadOnly
    IPrimitive<LeaseTermParticipant.Role> signedByRole();

}
