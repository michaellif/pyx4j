/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 30, 2011
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface SortEntity extends IEntity {
    IPrimitive<String> propertyName();

    IPrimitive<Boolean> descending();
}
