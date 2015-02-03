/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-09
 * @author vlads
 */
package com.propertyvista.operations.server.preloader;

import java.util.EnumSet;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.operations.domain.scheduler.PmcProcessOptions;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.ScheduleType;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.scheduler.TriggerPmcSelectionType;
import com.propertyvista.operations.domain.scheduler.TriggerSchedule;

public class TriggerPreloader extends AbstractDataPreloader {

    private static final String NIGHTLY_HOUR = "02:00:00";

    @Override
    public String create() {

        for (PmcProcessType pmcProcessType : EnumSet.allOf(PmcProcessType.class)) {
            Trigger trigger = EntityFactory.create(Trigger.class);

            trigger.scheduleSuspended().setValue(Boolean.TRUE);
            trigger.triggerType().setValue(pmcProcessType);
            trigger.name().setValue(pmcProcessType.getDescription());

            if (pmcProcessType.hasOption(PmcProcessOptions.GlobalOnly)) {
                trigger.populationType().setValue(TriggerPmcSelectionType.none);
            } else {
                trigger.populationType().setValue(TriggerPmcSelectionType.allPmc);
            }

            if (pmcProcessType.equals(PmcProcessType.resetDemoPMC)) {
                if (ApplicationMode.isDemo()) {
                    trigger.scheduleSuspended().setValue(Boolean.FALSE);
                    trigger.schedules().add(createNightlySchedule());
                } else {
                    continue;
                }
            }

            Persistence.service().persist(trigger);

            if (isTriggerScheduleActive(trigger)) {
                ServerSideFactory.create(OperationsTriggerFacade.class).scheduleTrigger(trigger);
            }
        }

        return null;
    }

    private static boolean isTriggerScheduleActive(Trigger trigger) {
        return !trigger.scheduleSuspended().getValue().booleanValue();
    }

    private TriggerSchedule createNightlySchedule() {
        TriggerSchedule nightlySchedule = EntityFactory.create(TriggerSchedule.class);

        nightlySchedule.repeatType().setValue(ScheduleType.Daily);
        nightlySchedule.repeatEvery().setValue(1);
        nightlySchedule.time().setValue(java.sql.Time.valueOf(NIGHTLY_HOUR));
        nightlySchedule.startsOn().setValue(new LogicalDate());

        return nightlySchedule;
    }

    @Override
    public String delete() {
        return null;
    }

}
