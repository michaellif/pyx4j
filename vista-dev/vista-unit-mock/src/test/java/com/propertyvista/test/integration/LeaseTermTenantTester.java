/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.test.integration;

import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class LeaseTermTenantTester extends Tester {

    private final LeaseTermTenant tenant;

    public LeaseTermTenantTester(LeaseTermTenant tenant) {
        this.tenant = tenant;
    }

    public LeaseTermTenantTester firstName(String value) {
        assertEquals("First Name", value, tenant.leaseParticipant().customer().person().name().firstName().getValue());
        return this;
    }

    public LeaseTermTenantTester lastName(String value) {
        assertEquals("Last Name", value, tenant.leaseParticipant().customer().person().name().lastName().getValue());
        return this;
    }

    public LeaseTermTenantTester role(Role value) {
        assertEquals("Last Name", value, tenant.role().getValue());
        return this;
    }

    public LeaseTermTenantTester relationship(PersonRelationship relationship) {
        assertEquals("Relationship", relationship, tenant.relationship().getValue());
        return this;
    }

    public LeaseTermTenantTester email(String value) {
        assertEquals("E-mails", value, tenant.leaseParticipant().customer().person().email().getValue());
        return this;
    }

}
