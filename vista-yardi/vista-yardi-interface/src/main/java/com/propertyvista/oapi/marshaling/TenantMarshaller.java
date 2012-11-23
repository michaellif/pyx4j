/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.oapi.model.TenantIO;

public class TenantMarshaller implements Marshaller<LeaseParticipant<?>, TenantIO> {

    @Override
    public TenantIO unmarshal(LeaseParticipant<?> participant) {
        TenantIO tenantIO = new TenantIO();
        tenantIO.firstName = participant.customer().person().name().firstName().getValue();
        tenantIO.lastName = participant.customer().person().name().lastName().getValue();
        tenantIO.middleName = participant.customer().person().name().middleName().getValue();
        tenantIO.sex = participant.customer().person().sex().getValue().name();
        tenantIO.phone = participant.customer().person().homePhone().getValue();
        tenantIO.email = participant.customer().person().email().getValue();
        return tenantIO;
    }

    @Override
    public LeaseParticipant<?> marshal(TenantIO v) throws Exception {
        return null;
    }

}
