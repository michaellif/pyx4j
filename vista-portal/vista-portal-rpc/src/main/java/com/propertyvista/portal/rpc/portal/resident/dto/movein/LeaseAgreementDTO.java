/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.resident.dto.movein;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.SignedAgreementConfirmationTerm;
import com.propertyvista.domain.tenant.lease.SignedAgreementLegalTerm;

@Transient
public interface LeaseAgreementDTO extends IEntity {

    @Owned
    IList<SignedAgreementLegalTerm> legalTerms();

    @Owned
    IList<SignedAgreementConfirmationTerm> confirmationTerms();

    AptUnit unit();

    LeaseTerm leaseTerm();

}
