/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 28, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface LeaseApplicationActionDTO extends IEntity {

    public enum Action {

        Approve,

        Decline,

        Cancel;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    Lease leaseId();

    IPrimitive<Action> action();

    IPrimitive<String> decisionReason();
}
