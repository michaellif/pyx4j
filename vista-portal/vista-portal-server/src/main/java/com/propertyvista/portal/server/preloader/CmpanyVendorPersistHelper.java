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

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.company.OrganizationContact;
import com.propertyvista.domain.company.OrganizationContacts;
import com.propertyvista.domain.property.vendor.Maintenance;
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.domain.property.vendor.Warranty;

public class CmpanyVendorPersistHelper extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(CmpanyVendorPersistHelper.class);

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Company.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {
        return "";
    }

    public static void persistCompany(Company company) {
        log.debug("Persisting company");

        Persistence.service().persist(company.phones());
        Persistence.service().persist(company.emails());

        for (OrganizationContacts contacts : company.contacts()) {
            for (OrganizationContact contact : contacts.contactList()) {
                Persistence.service().persist(contact.person());
            }
            Persistence.service().persist(contacts.companyRole());
            Persistence.service().persist(contacts.contactList());
        }
        Persistence.service().persist(company.contacts());
        Persistence.service().persist(company);
    }

    public static void persistVendor(Vendor vendor) {
        log.debug("Persisting vendor");
        persistCompany(vendor);
        Persistence.service().persist(vendor);
    }

    public static void persistWarranty(Warranty warranty) {
        log.debug("Persisting warranty");
        persistVendor(warranty.contract().contractor());
        Persistence.service().persist(warranty);
    }

    public static void persistMaintenance(Maintenance maintenance) {
        log.debug("Persisting maintenance");
        persistVendor(maintenance.contract().contractor());
        Persistence.service().persist(maintenance);
    }
}