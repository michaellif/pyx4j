/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.IBoundToApplication;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ApplicationEntityServiceImpl extends EntityServicesImpl {

    protected static I18n i18n = I18n.get(ApplicationEntityServiceImpl.class);

    public static <E extends IEntity & IBoundToApplication> void saveApplicationEntity(E entity) {
        // app specific security stuff
        final Application application = PtAppContext.getCurrentUserApplication();
        if ((!entity.application().isNull()) && (!entity.application().equals(application))) {
            throw new SecurityViolationException("Permission denied");
        }
        EntityGraph.applyRecursivelyAllObjects(entity, new EntityGraph.ApplyMethod() {
            @Override
            public void apply(IEntity entity) {
                if (entity instanceof IBoundToApplication) {
                    ((IBoundToApplication) entity).application().set(application);
                }
            }
        });
        secureSave(entity);
    }

    protected <T extends IBoundToApplication> T retrieveApplicationEntity(Class<T> clazz) {
        EntityQueryCriteria<T> criteria = EntityQueryCriteria.create(clazz);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        return secureRetrieve(criteria);
    }

    protected <T extends IBoundToApplication> void retrieveApplicationEntity(T entity) {
        retrieveApplicationEntity(entity, PtAppContext.getCurrentUserApplication());
    }

    protected <T extends IBoundToApplication> void retrieveApplicationEntity(T entity, Application application) {
        @SuppressWarnings("unchecked")
        EntityQueryCriteria<T> criteria = (EntityQueryCriteria<T>) EntityQueryCriteria.create(entity.getValueClass());
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        entity.set(secureRetrieve(criteria));
    }
}
