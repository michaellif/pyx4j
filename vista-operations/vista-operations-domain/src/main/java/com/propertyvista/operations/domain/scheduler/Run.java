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
package com.propertyvista.operations.domain.scheduler;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.operations.domain.security.OperationsUser;

@ToStringFormat("{0},{1}")
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "scheduler", namespace = VistaNamespace.operationsNamespace)
public interface Run extends IEntity {

    @ReadOnly
    @Indexed
    @Owner
    @JoinColumn
    @MemberColumn(notNull = true, name = "trgr")
    Trigger trigger();

    @ToString(index = 1)
    @Indexed
    IPrimitive<RunStatus> status();

    @ToString(index = 0)
    @Timestamp(Timestamp.Update.Created)
    @ReadOnly
    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> created();

    @Format("MM/dd/yyyy HH:mm")
    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> forDate();

    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> started();

    @Format("MM/dd/yyyy HH:mm")
    IPrimitive<Date> completed();

    OperationsUser startedBy();

    @Caption(name = "Statistics")
    @Owned(forceCreation = true, cascade = {})
    ExecutionReport executionReport();

    @Length(4000)
    IPrimitive<String> errorMessage();

    @Owned
    @Detached(level = AttachLevel.Detached)
    ISet<RunData> runData();

}
