/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 5, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.domain.scheduler;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.domain.VistaNamespace;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "scheduler", namespace = VistaNamespace.adminNamespace)
public interface RunData extends IEntity {

    @Detached
    @Owner
    @Indexed
    @JoinColumn
    Run execution();

    IPrimitive<RunDataStatus> status();

    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> started();

    IPrimitive<String> errorMessage();

    @ReadOnly
    Pmc pmc();

    @Caption(name = "Statistics")
    @Owned(forceCreation = true, cascade = {})
    RunStats stats();

    @Format("MM/dd/yyyy HH:mm")
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();
}
