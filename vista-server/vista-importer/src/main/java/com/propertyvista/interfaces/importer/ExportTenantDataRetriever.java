/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.interfaces.importer.converter.TenantConverter;
import com.propertyvista.interfaces.importer.model.TenantIO;
import com.propertyvista.server.domain.security.CustomerUserCredential;

public class ExportTenantDataRetriever {

    public TenantIO getModel(LeaseTermTenant leaseTermTenant) {
        Persistence.ensureRetrieve(leaseTermTenant, AttachLevel.Attached);

        TenantIO tenantIO = new TenantConverter().createTO(leaseTermTenant);

        if (!leaseTermTenant.leaseParticipant().customer().user().isNull()) {
            CustomerUserCredential credential = Persistence.service().retrieve(CustomerUserCredential.class,
                    leaseTermTenant.leaseParticipant().customer().user().getPrimaryKey());
            tenantIO.vistaPasswordHash().setValue(credential.credential().getValue());
        }

        tenantIO.autoPayAgreements().addAll(new ExportAutoPayAgreementDataRetriever().getModel(leaseTermTenant));

        return tenantIO;
    }

}
