/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.common.mail.templates;

import java.text.SimpleDateFormat;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.company.CompanyEmail;
import com.propertyvista.domain.company.CompanyPhone;
import com.propertyvista.domain.company.OrganizationContact;
import com.propertyvista.domain.company.OrganizationContacts;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.server.common.mail.templates.model.ApplicationT;
import com.propertyvista.server.common.mail.templates.model.BuildingT;
import com.propertyvista.server.common.mail.templates.model.CompanyT;
import com.propertyvista.server.common.mail.templates.model.LeaseT;
import com.propertyvista.server.common.mail.templates.model.NewPasswordT;
import com.propertyvista.server.common.mail.templates.model.TenantT;

public class EmailTemplateRootObjectLoader {

    public static <T extends IEntity> T loadRootObject(T tObj, CrmUser context) {
        if (tObj == null) {
            throw new Error("Loading object cannot be null");
        }
        CrmUser user = context;
        if (tObj instanceof NewPasswordT) {
            NewPasswordT t = (NewPasswordT) tObj;
            t.requestorName().set(user.name());
            // passwordResetUrl must be set by the service
        }
        return tObj;
    }

    public static <T extends IEntity> T loadRootObject(T tObj, TenantInLease context) {
        if (tObj == null) {
            throw new Error("Loading object cannot be null");
        }
        TenantInLease til = context;
        if (tObj instanceof TenantT) {
            TenantT t = (TenantT) tObj;
            t.name().setValue(Formatter.fullName(til.tenant().person().name()));
        } else if (tObj instanceof NewPasswordT) {
            NewPasswordT t = (NewPasswordT) tObj;
            t.requestorName().set(til.tenant().user().name());
            // passwordResetUrl must be set by the service
        } else if (tObj instanceof BuildingT) {
            BuildingT t = (BuildingT) tObj;
            Building bld = getBuilding(context);
            t.propertyCode().set(bld.propertyCode());
            t.legalName().set(bld.marketing().name());
            t.website().set(bld.contacts().website());
            t.email().set(bld.contacts().email());
        } else if (tObj instanceof ApplicationT) {
            ApplicationT t = (ApplicationT) tObj;
            t.applicant().setValue(Formatter.fullName(til.tenant().person().name()));
            t.refNumber().setValue(til.application().belongsTo().getPrimaryKey().toString());
        } else if (tObj instanceof LeaseT) {
            LeaseT t = (LeaseT) tObj;
            t.applicant().setValue(Formatter.fullName(til.tenant().person().name()));
            t.startDate().setValue(til.lease().leaseFrom().getStringView());
            t.startDateWeekday().setValue(new SimpleDateFormat("EEEE").format(til.lease().leaseFrom().getValue()));
        } else if (tObj instanceof CompanyT) {
            CompanyT t = (CompanyT) tObj;
            Company com = getCompany(context);
            t.name().set(com.name());
            AddressStructured as = com.addresses().get(0);
            t.address().setValue(Formatter.addressShort(as));
            t.phone().set(com.phones().get(0).phone());
            t.website().set(com.website());
            t.email().set(com.emails().get(0).email());
            // set contact info
            for (OrganizationContacts cont : com.contacts()) {
                if (cont.companyRole().name().getValue().equalsIgnoreCase("administrator")) {
                    t.administrator().name().setValue(Formatter.fullName(cont.contactList().get(0).person().name()));
                    t.administrator().phone().set(cont.contactList().get(0).person().workPhone());
                    t.administrator().email().set(cont.contactList().get(0).person().email());
                } else if (cont.companyRole().name().getValue().equalsIgnoreCase("superintendent")) {
                    t.superintendent().name().setValue(Formatter.fullName(cont.contactList().get(0).person().name()));
                    t.superintendent().phone().set(cont.contactList().get(0).person().workPhone());
                    t.superintendent().email().set(cont.contactList().get(0).person().email());
                } else if (cont.companyRole().name().getValue().equalsIgnoreCase("office")) {
                    t.mainOffice().name().setValue(Formatter.fullName(cont.contactList().get(0).person().name()));
                    t.mainOffice().phone().set(cont.contactList().get(0).person().workPhone());
                    t.mainOffice().email().set(cont.contactList().get(0).person().email());
                }
            }
        }
        return tObj;
    }

    private static Building getBuilding(TenantInLease context) {
        if (context == null) {
            throw new Error("Context cannot be null");
        }
        if (Persistence.service().retrieve(context.lease().unit().floorplan().building())) {
            return context.lease().unit().floorplan().building();
        } else {
            throw new Error("Invalid context. No building found.");
        }
    }

    private static Company getCompany(TenantInLease context) {
        if (context == null) {
            throw new Error("Context cannot be null");
        }
        // TODO return real PMC Company object
        Company com = EntityFactory.create(Company.class);
        com.name().setValue("123PMC Inc");
        com.website().setValue("123home.propertyvista.com");
        CompanyPhone phone = EntityFactory.create(CompanyPhone.class);
        phone.phone().setValue("1-888-123HOME");
        com.phones().add(phone);
        CompanyEmail email = EntityFactory.create(CompanyEmail.class);
        email.email().setValue("123home@propertyvista.com");
        com.emails().add(email);
        AddressStructured addr = EntityFactory.create(AddressStructured.class);
        addr.streetNumber().setValue("123");
        addr.streetName().setValue("Main");
        addr.streetType().setValue(StreetType.street);
        addr.city().setValue("Toronto");
        addr.province().name().setValue("Ontario");
        addr.province().code().setValue("ON");
        addr.country().name().setValue("Canada");
        com.addresses().add(addr);
        OrganizationContacts conts = EntityFactory.create(OrganizationContacts.class);
        conts.companyRole().name().setValue("Administrator");
        OrganizationContact cont = EntityFactory.create(OrganizationContact.class);
        cont.person().name().firstName().setValue("John");
        cont.person().name().lastName().setValue("Smith");
        cont.person().email().setValue("john.smith@propertyvista.com");
        cont.person().workPhone().setValue("987-654-3210 x.123");
        conts.contactList().add(cont);
        com.contacts().add(conts);
        return com;
    }

    static class Formatter {
        public static String addressShort(AddressStructured addr) {
            // {123} {Main} {St}, {City}, {PR} {A1B 2C3} 
            String fmt = "{0} {1} {2}, {3}, {4} {5}";
            return SimpleMessageFormat.format(fmt, addr.streetNumber().getStringView(), addr.streetName().getStringView(), addr.streetType().getStringView(),
                    addr.city().getStringView(), addr.province().code().getStringView(), addr.postalCode().getStringView());
        }

        public static String fullName(Name name) {
            // Mr John D Smith Junior
            String fmt = "{0} {1} {2} {3} {4}";
            return SimpleMessageFormat.format(fmt, name.namePrefix().getStringView(), name.firstName().getStringView(), name.middleName().getStringView(), name
                    .lastName().getStringView(), name.nameSuffix().getStringView());
        }
    }
}
