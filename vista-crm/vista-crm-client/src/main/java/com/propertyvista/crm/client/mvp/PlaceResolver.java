/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.mvp;

import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Locker;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class PlaceResolver {

    private static final Map<Class<? extends IEntity>, IPlaceResolver> entityClassToResolverMap = new HashMap<Class<? extends IEntity>, PlaceResolver.IPlaceResolver>();

    private static final Map<String, Class<? extends IEntity>> entityClassNameToEntityClassMap = new HashMap<String, Class<? extends IEntity>>();

    static {
        register(AptUnit.class, new EntityPlaceResolver() {
            @Override
            protected CrudAppPlace createCrudAppPlace() {
                return new CrmSiteMap.Properties.Unit();
            }
        });
        register(Building.class, new EntityPlaceResolver() {
            @Override
            protected CrudAppPlace createCrudAppPlace() {
                return new CrmSiteMap.Properties.Building();
            }
        });
        register(Complex.class, new EntityPlaceResolver() {
            @Override
            protected CrudAppPlace createCrudAppPlace() {
                return new CrmSiteMap.Properties.Complex();
            }
        });
        register(Floorplan.class, new EntityTabPlaceResolver(3) {
            @Override
            protected CrudAppPlace createEntityViewerPlace() {
                return new CrmSiteMap.Properties.Floorplan();
            }

            @Override
            protected CrudAppPlace createEntityListerPlace() {
                return new CrmSiteMap.Properties.Building();
            }
        });
        register(Locker.class, new EntityTabPlaceResolver(1) {
            @Override
            protected CrudAppPlace createEntityViewerPlace() {
                return new CrmSiteMap.Properties.Locker();
            }

            @Override
            protected CrudAppPlace createEntityListerPlace() {
                return new CrmSiteMap.Properties.LockerArea();
            }
        });
        register(LockerArea.class, new EntityTabPlaceResolver(6) {
            @Override
            protected CrudAppPlace createEntityViewerPlace() {
                return new CrmSiteMap.Properties.LockerArea();
            }

            @Override
            protected CrudAppPlace createEntityListerPlace() {
                return new CrmSiteMap.Properties.Building();
            }
        });

    }

    /**
     * @param entity
     * @return if entity is proto / without id returns lister that shows class of that entity).
     */
    public static CrudAppPlace resolvePlace(IEntity entity) {
        if (entity != null) {
            IPlaceResolver resolver = entityClassToResolverMap.get(entity.getInstanceValueClass());
            if (resolver != null) {
                return resolver.resolve(entity);
            } else {
                throw new Error(SimpleMessageFormat.format("Place for {0} is not defined, add {0} to PlaceResolver", entity.getInstanceValueClass().toString()));
            }
        } else {
            throw new Error("it's imposisble to resolve path when entity is null");
        }

    }

    public static CrudAppPlace resolvePlace(String entityClassName, Key id) {
        IEntity proto = createEntity(entityClassName);
        if (proto != null) {
            IEntity entity = EntityFactory.create(proto.getInstanceValueClass());
            entity.setPrimaryKey(id);
            return resolvePlace(entity);
        } else {
            return resolvePlace((IEntity) null);
        }
    }

    public static CrudAppPlace resolvePlace(String entityClassName) {
        return resolvePlace(createEntity(entityClassName));
    }

    private static IEntity createEntity(String entityClassName) {
        Class<? extends IEntity> entityClass = entityClassNameToEntityClassMap.get(entityClassName);
        if (entityClass != null) {
            return EntityFactory.create(entityClass);
        } else {
            throw new Error(SimpleMessageFormat.format("Place for {0} is not defined, add {0} to PlaceResolver", entityClassName));
        }
    }

    private static void register(Class<? extends IEntity> entityClass, IPlaceResolver resolver) {
        entityClassToResolverMap.put(entityClass, resolver);
        entityClassNameToEntityClassMap.put(entityClass.getName(), entityClass);
    }

    public interface IPlaceResolver {

        CrudAppPlace resolve(IEntity entity);

    }

    public static abstract class EntityPlaceResolver implements IPlaceResolver {

        @Override
        public CrudAppPlace resolve(IEntity entity) {
            CrudAppPlace place = createCrudAppPlace();
            Key entityId = entity.getPrimaryKey();
            if (entityId != null) {
                place.formViewerPlace(entityId);
            }
            return place;
        }

        protected abstract CrudAppPlace createCrudAppPlace();
    }

    public static abstract class EntityTabPlaceResolver implements IPlaceResolver {

        private final int tabIndex;

        public EntityTabPlaceResolver(int tabIndex) {
            this.tabIndex = tabIndex;
        }

        @Override
        public CrudAppPlace resolve(IEntity entity) {
            Key entityId = entity.getPrimaryKey();
            if (entityId != null) {
                CrudAppPlace place = createEntityViewerPlace();
                place.formViewerPlace(entityId, tabIndex);
                return place;
            } else {
                CrudAppPlace place = createEntityListerPlace();
                return place;
            }
        }

        protected abstract CrudAppPlace createEntityListerPlace();

        protected abstract CrudAppPlace createEntityViewerPlace();
    }
}
