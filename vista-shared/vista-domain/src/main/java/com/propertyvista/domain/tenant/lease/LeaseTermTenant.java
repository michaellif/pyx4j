/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.tenant.PersonRelationship;

@ToStringFormat("{0} - {1}{2,choice,null#|!null#, {2}}")
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
@DiscriminatorValue("Tenant")
public interface LeaseTermTenant extends LeaseTermParticipant<Tenant> {

    public enum SignatureType {
        ink, digital
    }

    @NotNull
    @ToString(index = 2)
    @Caption(description = "Relation to the Main Applicant")
    IPrimitive<PersonRelationship> relationship();

    /**
     * null means no signature
     */
    IPrimitive<SignatureType> signatureType();

    @Owned
    IList<SignedLeaseLegalTerm> legalTerms();
}
