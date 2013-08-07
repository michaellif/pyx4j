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

import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

public interface MaintenanceRequestMetadata extends IEntity {

    // @Owned won't work as the Category has itself as the Owner to build the tree structure
    MaintenanceRequestCategory rootCategory();

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<MaintenanceRequestStatus> statuses();

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<MaintenanceRequestPriority> priorities();

    @Transient
    IList<MaintenanceRequestCategoryLevel> categoryLevels();
}
