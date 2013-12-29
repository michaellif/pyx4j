/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 28, 2012
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.server.adapters;

import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.Locker.Type;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.shared.adapters.LockerCountersAdapter;

public class LockerCountersAdapterImpl implements LockerCountersAdapter {

    @Override
    public boolean allowModifications(Locker entity, MemberMeta meta, Object valueOrig, Object valueNew) {
        // comparing enums so != is OK
        LockerArea locker = Persistence.service().retrieve(LockerArea.class, entity.lockerArea().getPrimaryKey());
        if (valueNew != null && valueOrig != valueNew) {
            if (valueOrig != null) {
                Locker.Type typeOrig = (Type) valueOrig;
                Integer i = 0;
                switch (typeOrig) {
                case large:
                    i = locker.largeLockers().getValue();
                    locker.largeLockers().setValue(i - 1);
                    break;
                case regular:
                    i = locker.regularLockers().getValue();
                    locker.regularLockers().setValue(i - 1);
                    break;
                case small:
                    i = locker.smallLockers().getValue();
                    locker.smallLockers().setValue(i - 1);
                    break;
                }
            }

            if (valueNew != null) {
                Locker.Type typeNew = (Type) valueNew;
                Integer i = 0;
                switch (typeNew) {
                case large:
                    i = locker.largeLockers().getValue();
                    locker.largeLockers().setValue(i + 1);
                    break;
                case regular:
                    i = locker.regularLockers().getValue();
                    locker.regularLockers().setValue(i + 1);
                    break;
                case small:
                    i = locker.smallLockers().getValue();
                    locker.smallLockers().setValue(i + 1);
                    break;
                }
                if (valueOrig == null) {
                    i = locker.totalLockers().getValue();
                    locker.totalLockers().setValue(i + 1);
                }
            }
//        } else if (valueOrig == null) {

        }
        Persistence.service().persist(locker);
        return true;
    }
}
