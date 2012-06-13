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
package com.propertyvista.biz.financial.preload;

import java.util.HashMap;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;

public class LeaseAdjustmentReasonDataModel {

    public enum Reason {
        goodWill("Good Will Credit", LeaseAdjustmentReason.ActionType.credit), accountCharge("Account Charge", LeaseAdjustmentReason.ActionType.charge);

        private String description;

        private LeaseAdjustmentReason.ActionType actionType;

        private Reason(String description, LeaseAdjustmentReason.ActionType actionType) {
            this.description = description;
            this.actionType = actionType;
        }

        public String getDescription() {
            return description;
        }

        public LeaseAdjustmentReason.ActionType getActionType() {
            return actionType;
        }

    }

    public final HashMap<Reason, LeaseAdjustmentReason> reasons;

    public LeaseAdjustmentReasonDataModel(PreloadConfig config) {
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
        adjustmentReason.actionType().setValue(reason.getActionType());
        reasons.put(reason, adjustmentReason);
    }

    public LeaseAdjustmentReason getReason(Reason reason) {
        return reasons.get(reason);
    }

}
