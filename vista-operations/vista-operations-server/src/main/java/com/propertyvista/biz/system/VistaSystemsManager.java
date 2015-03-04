/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 4, 2015
 * @author vlads
 */
package com.propertyvista.biz.system;

import com.pyx4j.config.server.events.ServerEventBus;
import com.pyx4j.config.server.events.SystemMaintenanceStateChangeEvent;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.quartz.SchedulerHelper;

import com.propertyvista.config.VistaDeployment;

public final class VistaSystemsManager implements SystemMaintenanceStateChangeEvent.Handler {

    private VistaSystemsManager() {
        ServerEventBus.addHandler(SystemMaintenanceStateChangeEvent.class, this);
    }

    private static class SingletonHolder {
        public static final VistaSystemsManager INSTANCE = new VistaSystemsManager();
    }

    private static VistaSystemsManager instance() {
        return SingletonHolder.INSTANCE;
    }

    public static synchronized void init() {
        instance();
    }

    @Override
    public void onMaintenanceStateChange(SystemMaintenanceStateChangeEvent event) {
        if (SystemMaintenance.isSystemMaintenance()) {
            SchedulerHelper.setActive(false);
        } else {
            SchedulerHelper.setActive(!VistaDeployment.isVistaStaging());
        }
    }
}
