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
package com.propertyvista.portal.server.pt.services;

import org.xnap.commons.i18n.I18n;

import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.IBoundToApplication;
import com.propertyvista.portal.server.pt.PtUserDataAccess;

import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.i18n.shared.I18nFactory;

public class ApplicationEntityServicesImpl extends EntityServicesImpl {

    protected static I18n i18n = I18nFactory.getI18n();

    protected void applyApplication(IEntity entity) {
        // app specific security stuff

        final Application application = PtUserDataAccess.getCurrentUserApplication();
        EntityGraph.applyRecursively(entity, new EntityGraph.ApplyMethod() {
            @Override
            public void apply(IEntity entity) {
                if (entity instanceof IBoundToApplication) {
                    ((IBoundToApplication) entity).application().set(application);
                }
            }
        });

    }

    protected <T extends IBoundToApplication> T findApplicationEntity(Class<T> clazz) {
        EntityQueryCriteria<T> criteria = EntityQueryCriteria.create(clazz);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        return secureRetrieve(criteria);
    }

    protected <T extends IBoundToApplication> void retrieveApplicationEntity(T entity) {
        @SuppressWarnings("unchecked")
        EntityQueryCriteria<T> criteria = (EntityQueryCriteria<T>) EntityQueryCriteria.create(entity.getValueClass());
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        entity.set(secureRetrieve(criteria));
    }
}
