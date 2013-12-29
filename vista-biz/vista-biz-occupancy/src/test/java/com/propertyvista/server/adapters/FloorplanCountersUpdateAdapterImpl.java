/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.adapters;

import com.pyx4j.entity.core.meta.MemberMeta;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.shared.adapters.FloorplanCountersUpdateAdapter;

public class FloorplanCountersUpdateAdapterImpl implements FloorplanCountersUpdateAdapter {

    @Override
    public boolean allowModifications(AptUnit entity, MemberMeta meta, Object valueOrig, Object valueNew) {
        return true;
    }

}
