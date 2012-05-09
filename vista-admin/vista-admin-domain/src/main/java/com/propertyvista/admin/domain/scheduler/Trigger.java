/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 3, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.scheduler;

import java.util.Date;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "scheduler", namespace = VistaNamespace.adminNamespace)
public interface Trigger extends IEntity {

    @NotNull
    @ToString
    IPrimitive<String> name();

    @NotNull
    @ReadOnly
    @ToString
    IPrimitive<PmcProcessType> triggerType();

    /**
     * Configuration parameters for each particular trigger type.
     * // Future may not need this for today
     */
    @ReadOnly
    @Owned(forceCreation = true)
    TriggerDetails triggerDetails();

    @NotNull
    IPrimitive<TriggerPmcSelectionType> populationType();

    @Owned
    IList<TriggerPmc> population();

    @Owned
    IList<TriggerSchedule> schedules();

    @Timestamp(Timestamp.Update.Created)
    @ReadOnly
    @Format("MM/dd/yyyy h:mm a")
    IPrimitive<Date> created();

    @Owned(cascade = {})
    @Detached(level = AttachLevel.Detached)
    ISet<Run> executions();

    @Owned
    IList<Notification> notifications();

}
