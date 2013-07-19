/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.scheduler;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(prefix = "scheduler", namespace = VistaNamespace.operationsNamespace)
public interface ExecutionReportSection extends IEntity {

    @Owner
    @JoinColumn
    @Indexed(group = { "u,1" }, uniqueConstraint = true)
    ExecutionReport executionReport();

    @Indexed(group = { "u,2" })
    @Length(120)
    IPrimitive<String> name();

    @Indexed(group = { "u,3" })
    @MemberColumn(name = "tp")
    IPrimitive<CompletionType> type();

    IPrimitive<Long> counter();

    IPrimitive<BigDecimal> value();

    @Owned
    // TODO AttachLevel.CountOnly
    @Detached(level = AttachLevel.IdOnly)
    ISet<ExecutionReportMessage> messages();
}
