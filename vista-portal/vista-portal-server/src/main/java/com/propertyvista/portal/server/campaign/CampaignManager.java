/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 2, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.campaign;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.callfire.CallFire;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.rpc.ptapp.dto.TenantInLeaseListDTO;
import com.propertyvista.server.common.security.DevelopmentSecurity;
import com.propertyvista.server.domain.CampaignHistory;
import com.propertyvista.server.domain.CampaignTrigger;
import com.propertyvista.server.domain.PhoneCallCampaign;

public class CampaignManager {

    private final static Logger log = LoggerFactory.getLogger(CampaignManager.class);

    public static void fireEvent(CampaignTrigger trigger, TenantInLeaseListDTO tenants) {
        for (TenantInLease tenantInfo : tenants.tenants()) {
            TenantInLease.Role status = tenantInfo.role().getValue();

            switch (trigger) {
            case ApplicationCompleated:
                if (TenantInLease.Role.Applicant.equals(status)) {
                    fireEvent(trigger, tenantInfo);
                }
                break;
            default:
                if (TenantInLease.Role.Applicant.equals(status) || TenantInLease.Role.CoApplicant.equals(status)) {
                    fireEvent(trigger, tenantInfo);
                }
            }
        }
    }

    public static void fireEvent(CampaignTrigger trigger, TenantInLease tenant) {
        EntityQueryCriteria<CampaignHistory> criteria = EntityQueryCriteria.create(CampaignHistory.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenant));
        criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), trigger));
        CampaignHistory history = Persistence.service().retrieve(criteria);
        if (history != null) {
            return;
        }
        PhoneCallCampaign phoneCallCampaign = getCampaign(trigger);
        if (phoneCallCampaign == null) {
            return;
        }
        history = EntityFactory.create(CampaignHistory.class);
        history.tenant().set(tenant);
        history.trigger().setValue(trigger);
        history.campaign().set(phoneCallCampaign);
        execute(phoneCallCampaign, tenant);
        Persistence.service().persist(history);
    }

    private static void execute(PhoneCallCampaign phoneCallCampaign, TenantInLease tenant) {
        List<String> numbers = new ArrayList<String>();
        String name = tenant.tenant().person().name().firstName().getValue() + " " + tenant.tenant().person().name().lastName().getValue();
        String number = tenant.tenant().person().homePhone().number().getValue();
        if (ApplicationMode.isDevelopment()) {
            String allowedNumber = DevelopmentSecurity.callNumberFilter(number);
            log.info("We will call {} instead of {}", allowedNumber, number);
            if (allowedNumber == null) {
                return;
            }
            number = allowedNumber;
        }
        numbers.add(number + "," + name);

        boolean rc = CallFire.sendCalls(phoneCallCampaign.campaignid().getValue(), numbers);
        log.info("sendCalls result {}", rc);
    }

    public static PhoneCallCampaign getCampaign(CampaignTrigger trigger) {
        EntityQueryCriteria<PhoneCallCampaign> criteria = EntityQueryCriteria.create(PhoneCallCampaign.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), trigger));
        PhoneCallCampaign phoneCallCampaign = Persistence.service().retrieve(criteria);
        if (phoneCallCampaign == null) {
            return null;
        }
        if (phoneCallCampaign.campaignid().isNull()) {
            // Initialize Campaign in CallFire
            String message = phoneCallCampaign.message().getValue();
            String caller = "14166028523";
            String campaignid = CallFire.createNotificationCampaign(message, caller);
            phoneCallCampaign.campaignid().setValue(campaignid);
            Persistence.service().persist(phoneCallCampaign);
        }
        return phoneCallCampaign;

    }
}
