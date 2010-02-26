/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PathSearch;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.geo.GeoCell;
import com.pyx4j.geo.GeoCircle;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.geo.GeoUtils;
import com.pyx4j.security.shared.SecurityController;

public class EntityServicesImpl {

    private static final Logger log = LoggerFactory.getLogger(EntityServicesImpl.class);

    public static class SaveImpl implements EntityServices.Save {

        @Override
        public IEntity execute(IEntity request) {
            if (request.getPrimaryKey() == null) {
                SecurityController.assertPermission(EntityPermission.permissionCreate(request.getObjectClass()));
            } else {
                SecurityController.assertPermission(EntityPermission.permissionUpdate(request.getObjectClass()));
            }
            PersistenceServicesFactory.getPersistenceService().persist(request);
            return request;
        }
    }

    public static class QueryImpl implements EntityServices.Query {

        @SuppressWarnings("unchecked")
        @Override
        public Vector execute(EntityQueryCriteria request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            List<IEntity> rc = PersistenceServicesFactory.getPersistenceService().query(request);
            Vector<IEntity> v = new Vector<IEntity>();
            for (IEntity ent : rc) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
                v.add(ent);
            }
            return v;
        }
    }

    public static class SearchImpl implements EntityServices.Search {

        @SuppressWarnings("unchecked")
        @Override
        public Vector execute(EntitySearchCriteria<?> request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));

            Class<IEntity> entityClass = ServerEntityFactory.entityClass(request.getDomainName());
            EntityMeta meta = EntityFactory.getEntityMeta(entityClass);

            boolean limitToOneIndex = true; // For GAE
            boolean hasInequalityFilter = false;

            // TODO use groups in EntitySearchCriteria
            Set<MemberMeta> processed = new HashSet<MemberMeta>();

            //TODO temp solution for filtering by distance
            Integer areaRadius = null;
            GeoPoint geoPointFrom = null;
            String pathWithGeoPointData = null;
            //////////////////////////////////////////////

            EntityQueryCriteria criteria = new EntityQueryCriteria(entityClass);
            for (Map.Entry<PathSearch, Serializable> me : request.getFilters().entrySet()) {
                if (me.getValue() == null) {
                    continue;
                }
                PathSearch path = me.getKey();
                if (path.getPathMembers().size() > 1) {
                    // TODO
                    log.warn("Ignore path {} not implemented", path);
                    continue;
                }
                MemberMeta mm = meta.getMemberMeta(path);
                if (processed.contains(mm)) {
                    continue;
                }
                if (String.class.isAssignableFrom(mm.getValueClass())) {
                    String str = me.getValue().toString().trim();
                    if (!CommonsStringUtils.isStringSet(str)) {
                        continue;
                    }
                    Indexed index = mm.getAnnotation(Indexed.class);
                    //If indexed by keywords
                    if ((index != null) && (index.keywordLenght() > 0)) {
                        Set<String> keys = IndexString.getIndexValues(index.keywordLenght(), str);
                        for (String key : keys) {
                            // TODO use SECONDARY_PRROPERTY_SUFIX or identify use of index in any other way
                            criteria.add(new PropertyCriterion(mm.getFieldName() + "-s", Restriction.EQUAL, key));
                        }
                        if (str.length() > index.keywordLenght()) {
                            //TODO use secondary filter
                        }
                    } else {
                        // Simple like implementation
                        if (hasInequalityFilter && limitToOneIndex) {
                            // TODO Add to in memory filters
                            continue;
                        }
                        char firstChar = str.charAt(0);
                        if (Character.isLetter(firstChar) && Character.isLowerCase(firstChar)) {
                            str = str.replaceFirst(String.valueOf(firstChar), String.valueOf(Character.toUpperCase(firstChar)));
                        }
                        String from = str;
                        String to = from + "z";
                        criteria.add(new PropertyCriterion(mm.getFieldName(), Restriction.GREATER_THAN_OR_EQUAL, from));
                        criteria.add(new PropertyCriterion(mm.getFieldName(), Restriction.LESS_THAN, to));
                        hasInequalityFilter = true;
                    }
                } else if (GeoPoint.class.isAssignableFrom(mm.getValueClass())) {
                    pathWithGeoPointData = path.getPathString();
                    areaRadius = (Integer) request.getValue(new PathSearch(pathWithGeoPointData, "radius"));
                    geoPointFrom = (GeoPoint) request.getValue(new PathSearch(pathWithGeoPointData, "from"));
                    if ((areaRadius != null) && (geoPointFrom != null)) {
                        List<String> keys = GeoCell.getBestCoveringSet(new GeoCircle(geoPointFrom, areaRadius.intValue() * 1000));
                        criteria.add(new PropertyCriterion(mm.getFieldName() + "-s", Restriction.IN, (Serializable) keys));
                    }
                    processed.add(mm);
                } else {
                    log.warn("Search by class {} not implemented", mm.getValueClass());
                }
            }

            List<IEntity> rc = PersistenceServicesFactory.getPersistenceService().query(criteria);
            int maxResults = Integer.MAX_VALUE;
            int firstResult = -1;
            if (request.getPageSize() > 0) {
                maxResults = request.getPageSize();
                firstResult = request.getPageSize() * (request.getPageNumber() - 1);
            }

            Vector<IEntity> v = new Vector<IEntity>();
            int count = 0;
            for (IEntity ent : rc) {

                if (pathWithGeoPointData != null) {
                    GeoPoint geoPoint = (GeoPoint) ent.getValue(new Path(pathWithGeoPointData));
                    if (GeoUtils.distance(geoPoint, geoPointFrom) > areaRadius) {
                        continue;
                    }
                }

                if (count < firstResult) {
                    count++;
                    continue;
                }
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
                v.add(ent);
                count++;
                if (v.size() >= maxResults) {
                    break;
                }
            }
            return v;
        }
    }

    public static class RetrieveImpl implements EntityServices.Retrieve {

        @SuppressWarnings("unchecked")
        @Override
        public IEntity execute(EntityQueryCriteria request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            IEntity ent = PersistenceServicesFactory.getPersistenceService().retrieve(request);
            if (ent != null) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
            }
            return ent;
        }
    }

    public static class RetrieveByPKImpl implements EntityServices.RetrieveByPK {

        @SuppressWarnings("unchecked")
        @Override
        public IEntity execute(EntityCriteriaByPK request) {
            SecurityController.assertPermission(new EntityPermission(request.getDomainName(), EntityPermission.READ));
            IEntity ent = PersistenceServicesFactory.getPersistenceService().retrieve(ServerEntityFactory.entityClass(request.getDomainName()),
                    request.getPrimaryKey());
            if (ent != null) {
                SecurityController.assertPermission(EntityPermission.permissionRead(ent.getObjectClass()));
            }
            return ent;
        }
    }

}
