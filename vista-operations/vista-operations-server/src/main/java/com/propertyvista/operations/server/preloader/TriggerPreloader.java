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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.scheduler.PmcProcessOptions;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.ScheduleType;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.scheduler.TriggerPmc;
import com.propertyvista.operations.domain.scheduler.TriggerPmcSelectionType;
import com.propertyvista.operations.domain.scheduler.TriggerSchedule;

public class TriggerPreloader extends AbstractDataPreloader {

    private static final String NIGHTLY_HOUR = "02:00:00";

    private static final DemoData.DemoPmc[] PMCsToReset = { DemoPmc.rockville };

    @Override
    public String create() {

        for (PmcProcessType pmcProcessType : EnumSet.allOf(PmcProcessType.class)) {
            Trigger trigger = EntityFactory.create(Trigger.class);

            trigger.scheduleSuspended().setValue(true);
            trigger.triggerType().setValue(pmcProcessType);
            trigger.name().setValue(pmcProcessType.getDescription());

            if (pmcProcessType.hasOption(PmcProcessOptions.GlobalOnly)) {
                trigger.populationType().setValue(TriggerPmcSelectionType.none);
            } else {
                trigger.populationType().setValue(TriggerPmcSelectionType.allPmc);
            }

            if (pmcProcessType.equals(PmcProcessType.resetDemoPMC)) {
//                if (ApplicationMode.isDemo()) {
                trigger.scheduleSuspended().setValue(false);
                trigger.schedules().add(createNightlySchedule());
                trigger.populationType().setValue(TriggerPmcSelectionType.none); //Instead of 'manual'
                // TODO PMCs are not created when TriggerPreloader is executed. Move default PMCs to reset to ResetDemoPmcProcess?
                // TODO Should PMCs be shown at user interface to be editable?
                trigger.population().addAll(getDefaultResetDemoPMCs(trigger));
//                } else {
//                    continue;
//                }
            }

            Persistence.service().persist(trigger);
        }

        return null;
    }

    private Collection<TriggerPmc> getDefaultResetDemoPMCs(Trigger trigger) {
        List<TriggerPmc> pmcs = new ArrayList<TriggerPmc>(PMCsToReset.length);
        for (DemoPmc pmc : PMCsToReset) {
            TriggerPmc tPmc = EntityFactory.create(TriggerPmc.class);
            tPmc.trigger().set(trigger);
            tPmc.pmc().set(getPmcByName(pmc.name()));
            if (tPmc.pmc() != null) {
                pmcs.add(tPmc);
            }
        }

        return pmcs;
    }

    private Pmc getPmcByName(String name) {
        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.eq(criteria.proto().name(), name);
        return Persistence.service().retrieve(criteria);
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
