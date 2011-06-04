/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.Company;
import com.propertyvista.domain.Email;
import com.propertyvista.domain.OrganizationContact;
import com.propertyvista.domain.OrganizationContacts;
import com.propertyvista.domain.Phone;
import com.propertyvista.domain.tenant.Tenant;

public class Persister {
    private final static Logger log = LoggerFactory.getLogger(Persister.class);

    public static void persistCompany(Company company) {
        log.debug("Persisting company");
        for (Phone phone : company.phones()) {
            persist(phone);
        }
        for (Email email : company.emails()) {
            persist(email);
        }
        for (OrganizationContacts contacts : company.contacts()) {
            persist(contacts.companyRole());
            for (OrganizationContact contact : contacts.contactList()) {
                persist(contact.contactRole());
                persist(contact.person());
                persist(contact);
            }
            persist(contacts);
        }
        persist(company);
    }

    public static void persistTenant(Tenant tenant) {
        log.debug("Persisting tenant {}", tenant.person().name());
        persist(tenant.person());
        persistCompany(tenant.company());
        persist(tenant);
    }

    private static void persist(IEntity entity) {
        log.debug("Persisting {}", entity.getEntityMeta().getEntityClass().getName());
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }

}
