/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 16, 2014
 * @author stanp
 */
package com.propertyvista.yardi.mock.model.domain;

import java.util.EnumSet;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

public interface YardiTenant extends IEntity {

    public enum Type {
        GUEST, APPLICANT, CUSTOMER, CURRENT_RESIDENT, FORMER_RESIDENT, FUTURE_RESIDENT, PROSPECT, OTHER;

        public boolean isProspect() {
            return EnumSet.of(GUEST, PROSPECT, OTHER).contains(this);
        }

        public boolean isApplicant() {
            return EnumSet.of(APPLICANT, FUTURE_RESIDENT).contains(this);
        }

        public boolean isResident() {
            return EnumSet.of(CUSTOMER, CURRENT_RESIDENT, FORMER_RESIDENT, FUTURE_RESIDENT).contains(this);
        }
    }

    IPrimitive<String> guestId();

    IPrimitive<String> prospectId();

    IPrimitive<String> tenantId();

    IPrimitive<Type> type();

    IPrimitive<String> firstName();

    IPrimitive<String> lastName();

    IPrimitive<String> email();

    IPrimitive<Boolean> responsibleForLease();
}
