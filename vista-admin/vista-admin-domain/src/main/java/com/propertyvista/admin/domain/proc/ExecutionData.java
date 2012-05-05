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
package com.propertyvista.admin.domain.proc;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.EmbeddedEntity;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

/**
 * TODO Rename to ExecutionPmcData after migration
 */
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Table(namespace = VistaNamespace.adminNamespace)
public interface ExecutionData extends IEntity {

    @Detached
    @Owner
    @Indexed
    @JoinColumn
    Execution execution();

    IPrimitive<ExecutionDataStatus> status();

    @Caption(name = "Statistics")
    @EmbeddedEntity
    ExecutionStats stats();
}
