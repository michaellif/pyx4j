/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 17, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.operations.domain.legal;

import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.operations.domain.legal.VistaTerms.VistaTermsV;

@Table(namespace = VistaNamespace.operationsNamespace)
public interface VistaTerms extends IVersionedEntity<VistaTermsV> {

    public enum Target {

        PMC, Tenant, PmcCaledonTemplate, PmcCaldedonSolePropetorshipSection, PmcPaymentPad, TenantSurePreAuthorizedPaymentsAgreement, TenantPrivacy;

    }

    IPrimitive<Target> target();

    @Table(namespace = VistaNamespace.operationsNamespace)
    public interface VistaTermsV extends IVersionData<VistaTerms> {
        @Owned
        IList<LegalDocument> document();
    }
}
