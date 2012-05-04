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
package com.propertyvista.admin.domain.proc;

import java.util.Date;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(namespace = VistaNamespace.adminNamespace)
public interface Execution extends IEntity {

    @ReadOnly
    @Indexed
    @Owner
    Process process();

    @Indexed
    IPrimitive<ExecutionStatus> status();

    @Timestamp(Timestamp.Update.Created)
    @ReadOnly
    @Format("MM/dd/yyyy h:mm a")
    IPrimitive<Date> created();

    @Caption(name = "Statistics")
    ExecutionStats stats();
}
