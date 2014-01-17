/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.settings;

import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

public interface ILSConfig extends IEntity {
    public enum ILSVendor {
        kijiji, gottarent, emg
    }

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<ILSVendorConfig> vendors();

    @Owned
    @OrderBy(PrimaryKey.class)
    IList<ILSEmailConfig> emailFeeds();

    // --- work-around to avoid SQL errors on an empty entity
    // --- remove ONLY if other IPrimitive added
    @Deprecated
    IPrimitive<String> x();
}
