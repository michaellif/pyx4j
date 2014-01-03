/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.yardi.mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Customerinfo;
import com.yardi.entity.mits.Phone;
import com.yardi.entity.mits.YardiCustomer;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.server.EmailValidator;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.operations.domain.scheduler.CompletionType;

public class TenantMapper {

    private final static Logger log = LoggerFactory.getLogger(TenantMapper.class);

    private final ExecutionMonitor executionMonitor;

    private final static Set<String> ignoredEmailLocalParts = new HashSet<String>();

    static {
        ignoredEmailLocalParts.add("noemail");
        ignoredEmailLocalParts.add("nowhere");
        ignoredEmailLocalParts.add("nobody");
    }

    public TenantMapper() {
        this(null);
    }

    public TenantMapper(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public LeaseTermTenant createTenant(YardiCustomer yardiCustomer, List<LeaseTermTenant> tenants) {
        LeaseTermTenant tenant = EntityFactory.create(LeaseTermTenant.class);
        boolean isEmailAlreadyUsed = isEmailAlreadyUsed(retrieveYardiCustomerEmail(yardiCustomer), tenants);

        Customer customer = null;
        if (!isEmailAlreadyUsed) {
            customer = findCustomer(yardiCustomer);
        }
        if (customer == null) {
            customer = EntityFactory.create(Customer.class);
        }

        tenant.leaseParticipant().customer().set(mapCustomer(yardiCustomer, customer));
        tenant.leaseParticipant().participantId().setValue(yardiCustomer.getCustomerID());

        if (yardiCustomer.getLease().isResponsibleForLease()) {
            tenant.role().setValue(isApplicantExists(tenants) ? Role.CoApplicant : Role.Applicant);
        } else {
            tenant.role().setValue(Role.Dependent);
        }

        // set Yardi's email just in case of initial import and non-former leases:
        if (!isEmailAlreadyUsed && customer.id().isNull() && !isFormerLease(yardiCustomer)) {
            setEmail(yardiCustomer, customer);
        }

        return tenant;
    }

    public boolean updateTenant(YardiCustomer yardiCustomer, LeaseTermTenant tenant) {
        tenant.leaseParticipant().customer().set(mapCustomer(yardiCustomer, tenant.leaseParticipant().customer()));

        boolean updated = false;

        Role current = tenant.role().getValue();

        // update CoApplicant <-> Dependent transition:
        if (yardiCustomer.getLease().isResponsibleForLease()) {
            if (tenant.role().getValue() != Role.Applicant) {
                tenant.role().setValue(Role.CoApplicant);
            }
        } else {
            tenant.role().setValue(Role.Dependent);
        }

        updated |= current != tenant.role().getValue();

        return updated;
    }

    public Customer mapCustomer(YardiCustomer yardiCustomer, Customer customer) {
        // TODO translate plane string to our Name.Prefix enum:
        //      customer.person().name().namePrefix().setValue(yardiCustomer.getName().getNamePrefix());
        customer.person().name().firstName().setValue(yardiCustomer.getName().getFirstName());
        customer.person().name().middleName().setValue(yardiCustomer.getName().getMiddleName());
        customer.person().name().lastName().setValue(yardiCustomer.getName().getLastName());
        customer.person().name().maidenName().setValue(yardiCustomer.getName().getMaidenName());
        customer.person().name().nameSuffix().setValue(yardiCustomer.getName().getNameSuffix());

        // set Yardi's phone in case of initial import or corresponding Vista phone is not set:
        for (Phone phone : yardiCustomer.getPhone()) {
            switch (phone.getType()) {
            case CELL:
                if (customer.id().isNull() || customer.person().mobilePhone().isNull()) {
                    setPhone(phone, customer.person().mobilePhone());
                }
                break;
            case FAX:
                break;
            case HOME:
                if (customer.id().isNull() || customer.person().homePhone().isNull()) {
                    setPhone(phone, customer.person().homePhone());
                }
                break;
            case OFFICE:
                if (customer.id().isNull() || customer.person().workPhone().isNull()) {
                    setPhone(phone, customer.person().workPhone());
                }
                break;
            case OTHER:
                break;
            case PERSONAL:
                break;
            default:
                break;
            }
        }

        //TODO - find somewhere...
        //      customer.person().birthDate().setValue(value);

        return customer;
    }

    private boolean isEmailAlreadyUsed(String email, List<LeaseTermTenant> tenants) {
        if (!CommonsStringUtils.isEmpty(email)) {
            for (LeaseTermTenant tenant : tenants) {
                String emailN = tenant.leaseParticipant().customer().person().email().getValue();
                if (!CommonsStringUtils.isEmpty(emailN) && CommonsStringUtils.equals(email, emailN)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Customer findCustomer(YardiCustomer yardiCustomer) {
        String email = retrieveYardiCustomerEmail(yardiCustomer);
        if (!CommonsStringUtils.isEmpty(email) && EmailValidator.isValid(email)) {
            EntityQueryCriteria<Customer> criteria = EntityQueryCriteria.create(Customer.class);
            criteria.eq(criteria.proto().person().email(), EmailValidator.normalizeEmailAddress(email));
            return Persistence.service().retrieve(criteria);
        }
        return null;
    }

    private boolean isApplicantExists(List<LeaseTermTenant> tenants) {
        for (LeaseTermTenant tenant : tenants) {
            if (tenant.role().getValue().equals(Role.Applicant)) {
                return true;
            }
        }
        return false;
    }

    private void setPhone(Phone phone, IPrimitive<String> to) {
        if (CommonsStringUtils.isEmpty(phone.getExtension())) {
            to.setValue(phone.getPhoneNumber());
        } else {
            to.setValue(phone.getPhoneNumber() + " x" + phone.getExtension());
        }
    }

    private String retrieveYardiCustomerEmail(YardiCustomer yardiCustomer) {
        if (true) {
            return yardiCustomer.getCustomerID() + "@pyx4j.com";
        }

        if (!yardiCustomer.getAddress().isEmpty()) {
            String email = yardiCustomer.getAddress().get(0).getEmail();
            if (!CommonsStringUtils.isEmpty(email)) {
                email = EmailValidator.normalizeEmailAddress(email);
                if (isEmailIgnorable(email)) {
                    log.debug("ignoring email {} for customerID : {}", yardiCustomer.getCustomerID(), email);
                } else {
                    return email;
                }
            }
        }
        return null;
    }

    private void setEmail(YardiCustomer yardiCustomer, Customer customer) {
        String email = retrieveYardiCustomerEmail(yardiCustomer);
        if (!CommonsStringUtils.isEmpty(email)) {
            email = email.trim();
            if (EmailValidator.isValid(email)) {
                customer.person().email().setValue(EmailValidator.normalizeEmailAddress(email));
            } else {
                log.warn(">> DataValidation CustomerID : {} >> Invalid Email: [{}] ", yardiCustomer.getCustomerID(), email);
                if (executionMonitor != null) {
                    executionMonitor.addInfoEvent("DataValidation", CompletionType.failed,
                            "Invalid Email: " + email + " for CustomerID " + yardiCustomer.getCustomerID(), null);
                }
            }
        }
    }

    /**
     * Solution for noemail@realstar.ca
     */
    private boolean isEmailIgnorable(String normalizedEmail) {
        String[] emailParts = normalizedEmail.split("@");
        return (emailParts.length > 0) && (ignoredEmailLocalParts.contains(emailParts[0]));
    }

    private boolean isFormerLease(YardiCustomer yardiCustomer) {
        Customerinfo info = yardiCustomer.getType();
        return Customerinfo.FORMER_RESIDENT.equals(info);
    }

}
