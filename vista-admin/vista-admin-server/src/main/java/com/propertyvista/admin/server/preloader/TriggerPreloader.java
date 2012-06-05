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
 * @version $Id$
 */
package com.propertyvista.admin.server.preloader;

import java.util.EnumSet;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.domain.scheduler.PmcProcessType;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.domain.scheduler.TriggerPmcSelectionType;

public class TriggerPreloader extends AbstractDataPreloader {

    @Override
    public String create() {

        for (PmcProcessType pmcProcessType : EnumSet.allOf(PmcProcessType.class)) {
            Trigger trigger = EntityFactory.create(Trigger.class);
            trigger.triggerType().setValue(pmcProcessType);
            trigger.name().setValue(pmcProcessType.toString());
            trigger.populationType().setValue(TriggerPmcSelectionType.allPmc);
            Persistence.service().persist(trigger);
        }

        return null;
    }

    @Override
    public String delete() {
        return null;
    }

}
