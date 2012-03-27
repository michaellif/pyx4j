/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.accounting.preload;

import java.util.HashMap;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class LeaseAdjustmentReasonDataModel {

    public enum Reason {
        goodWill("Good Will");

        private String description;

        private Reason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    public final HashMap<Reason, LeaseAdjustmentReason> reasons;

    public LeaseAdjustmentReasonDataModel() {
        reasons = new HashMap<Reason, LeaseAdjustmentReason>();
    }

    public void generate(boolean persist) {

        for (Reason reason : Reason.values()) {
            generateReason(reason);
        }

        if (persist) {
            Persistence.service().persist(reasons.values());
        }
    }

    private void generateReason(Reason reason) {
        LeaseAdjustmentReason adjustmentReason = EntityFactory.create(LeaseAdjustmentReason.class);
        adjustmentReason.name().setValue(reason.getDescription());
        adjustmentReason.precalculatedTax().setValue(true);
        reasons.put(reason, adjustmentReason);
    }

    public LeaseAdjustmentReason getReason(Reason reason) {
        return reasons.get(reason);
    }

}
