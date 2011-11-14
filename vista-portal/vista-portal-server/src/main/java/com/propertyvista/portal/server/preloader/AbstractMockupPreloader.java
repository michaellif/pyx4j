/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;

public abstract class AbstractMockupPreloader extends BaseVistaDevDataPreloader {

    @Override
    public final String create() {
        if (((config().minimizePreloadTime)) || (!(config().mockupData))) {
            return null;
        } else {
            return createMockup();
        }
    }

    public abstract String createMockup();

    protected <T extends IEntity> void persistArray(Iterable<T> entityIterable) {
        ((EntityPersistenceServiceRDB) Persistence.service()).persistListOneLevel(entityIterable, false);
    }

    protected <T extends IEntity> void persistArrayWithId(Iterable<T> entityIterable) {
        ((EntityPersistenceServiceRDB) Persistence.service()).persistListOneLevel(entityIterable, true);
    }

}
