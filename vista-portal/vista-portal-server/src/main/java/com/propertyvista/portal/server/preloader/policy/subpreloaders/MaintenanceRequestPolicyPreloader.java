/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 31, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import java.sql.Time;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.maintenance.MaintenanceRequestWindow;
import com.propertyvista.domain.policy.policies.MaintenanceRequestPolicy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class MaintenanceRequestPolicyPreloader extends AbstractPolicyPreloader<MaintenanceRequestPolicy> {

    public MaintenanceRequestPolicyPreloader() {
        super(MaintenanceRequestPolicy.class);
    }

    @Override
    protected MaintenanceRequestPolicy createPolicy(StringBuilder log) {
        MaintenanceRequestPolicy policy = EntityFactory.create(MaintenanceRequestPolicy.class);
        // add default time windows to select by tenant as preferred time
        MaintenanceRequestWindow window = policy.tenantPreferredWindows().$();
        window.timeFrom().setValue(Time.valueOf("08:00:00"));
        window.timeTo().setValue(Time.valueOf("12:00:00"));
        policy.tenantPreferredWindows().add(window);
        window = policy.tenantPreferredWindows().$();
        window.timeFrom().setValue(Time.valueOf("13:00:00"));
        window.timeTo().setValue(Time.valueOf("17:00:00"));
        policy.tenantPreferredWindows().add(window);
        // set scheduling options
        policy.allow24HourSchedule().setValue(false);
        policy.schedulingWindow().timeFrom().setValue(Time.valueOf("08:00:00"));
        policy.schedulingWindow().timeTo().setValue(Time.valueOf("18:00:00"));
        policy.maxAllowedWindowHours().setValue(4);
        policy.minAdvanceNoticeHours().setValue(24);

        return policy;
    }

}
