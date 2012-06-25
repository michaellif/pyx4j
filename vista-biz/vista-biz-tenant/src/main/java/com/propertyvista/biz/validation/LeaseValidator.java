/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.EntityGraph.ApplyMethod;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant.Role;

public class LeaseValidator {

    private static final I18n i18n = I18n.get(LeaseValidator.class);

    public List<ValidationFailure> isValidForActivation(Lease leaseStub) {
        // check the following stuff
        List<ValidationFailure> validationFailure = new ArrayList<ValidationFailure>();

        Lease lease = Persistence.service().retrieve(Lease.class, leaseStub.getPrimaryKey());
        Persistence.service().retrieve(lease.version().tenants());
        Persistence.service().retrieve(lease.version().guarantors());

        validationFailure.addAll(validateMandatoryFields(lease));

        if (lease.unit().isEmpty()) {
            validationFailure.add(new CustomValidationFailure("unit is required for activation"));
        }

        // check that we have at least one tenant
        {
            boolean hasOneApplicant = false;
            if (!lease.version().tenants().isEmpty()) {
                for (Tenant tenant : lease.version().tenants()) {
                    if (tenant.role().getValue() == Role.Applicant) {
                        hasOneApplicant = true;
                        break;
                    }
                }
            }
            if (!hasOneApplicant) {
                validationFailure.add(new CustomValidationFailure("at least one tenant is required"));
            }
        }

        // check that we have some kind of service item
        if (lease.version().leaseProducts().serviceItem().isNull()) {
            validationFailure.add(new CustomValidationFailure("service item is required"));
        }
        return validationFailure;
    }

    static Collection<ValidationFailure> validateMandatoryFields(Lease lease) {
        final Collection<ValidationFailure> validationFailures = new ArrayList<ValidationFailure>();

        EntityGraph.applyRecursively(lease, new ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                for (String memberName : entity.getEntityMeta().getMemberNames()) {
                    IObject<?> member = entity.getMember(memberName);
                    MemberMeta memberMeta = member.getMeta();
                    if (memberMeta.isDetached()) {
                        return false;
                    }
                    if ((memberMeta.isValidatorAnnotationPresent(NotNull.class)) && (member.isNull())) {
                        validationFailures.add(new NotNullValidationFailure(member, i18n.tr("Cannot be empty")));
                    }
                    if (memberMeta.isValidatorAnnotationPresent(Length.class) && memberMeta.getValueClass().equals(String.class)) {
                        String value = (String) member.getValue();
                        if ((value != null) && (value.length() > memberMeta.getLength())) {
                            validationFailures.add(new LengthValidationFailure());
                        }
                    }
                }
                return true;
            }
        });
        return validationFailures;
    }
}
