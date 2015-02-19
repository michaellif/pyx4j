/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 19, 2015
 * @author vlads
 */
package com.propertyvista.config;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.server.admin.SystemMaintenance;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.VistaApplicationsSystemMaintenanceState;
import com.propertyvista.operations.domain.VistaSystemMaintenanceState;

public final class VistaSystemMaintenance {

    private static final Map<Pmc, VistaApplicationsSystemMaintenanceState> applications = new HashMap<>();

    public static VistaSystemMaintenanceState getGlobalState() {
        return ((VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo());
    }

    public static VistaApplicationsSystemMaintenanceState getApplicationsState() {
        VistaApplicationsSystemMaintenanceState state = EntityFactory.create(VistaApplicationsSystemMaintenanceState.class);

        VistaApplicationsSystemMaintenanceState systemDefault = getGlobalState().applications();
        VistaApplicationsSystemMaintenanceState pmcState = applications.get(VistaDeployment.getCurrentPmc());

        combinePmcAndSystem(state.crmLoginDisabled(), pmcState, systemDefault);
        combinePmcAndSystem(state.crmPaymentsDisabled(), pmcState, systemDefault);
        combinePmcAndSystem(state.tenantsLoginDisabled(), pmcState, systemDefault);
        combinePmcAndSystem(state.tenantsPaymentsDisabled(), pmcState, systemDefault);

        return state;
    }

    public static VistaApplicationsSystemMaintenanceState getApplicationsState(Pmc pmc) {
        return applications.get(pmc);
    }

    public static void setApplicationsState(Pmc pmc, VistaApplicationsSystemMaintenanceState state) {
        if (state.isEmpty()) {
            applications.remove(pmc);
        } else {

        }
    }

    @SuppressWarnings("unchecked")
    private static void combinePmcAndSystem(IPrimitive<Boolean> dst, IEntity pmcSettings, IEntity systemSettings) {
        if (pmcSettings != null) {
            Boolean pmcValue = ((IPrimitive<Boolean>) pmcSettings.getMember(dst.getFieldName())).getValue();
            if (pmcValue != null) {
                if (pmcValue == Boolean.FALSE) {
                    dst.setValue(false);
                } else {
                    dst.setValue(true);
                }
                return;
            }
        }
        Boolean systemValue = ((IPrimitive<Boolean>) systemSettings.getMember(dst.getFieldName())).getValue();
        if (systemValue != null) {
            dst.setValue(systemValue);
        } else {
            dst.setValue(false);
        }
    }
}
