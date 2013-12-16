/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.maintenance;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

public interface MaintenanceRequestCategory extends IEntity {

    @Owner
    @JoinColumn
    @Indexed
    @ReadOnly
    MaintenanceRequestCategory parent();

    @Transient
    IPrimitive<Integer> level();

    @ReadOnly
    IPrimitive<IssueElementType> type();

    @ToString(index = 0)
    IPrimitive<String> name();

    @Owned
    @OrderBy(PrimaryKey.class)
    @Detached(level = AttachLevel.Detached)
    IList<MaintenanceRequestCategory> subCategories();

    // ----------------------------------
    // to speed up loading in multi-root setup
    @Indexed
    @ReadOnly
    MaintenanceRequestCategory root();
}
