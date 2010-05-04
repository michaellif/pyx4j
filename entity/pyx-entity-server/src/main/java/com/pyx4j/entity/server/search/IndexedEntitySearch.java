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
 * Created on Feb 28, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.search;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IndexString;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
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

public class IndexedEntitySearch {

    private static final Logger log = LoggerFactory.getLogger(IndexedEntitySearch.class);

    protected final EntitySearchCriteria<?> searchCriteria;

    protected final Class<? extends IEntity> entityClass;

    protected final EntityMeta meta;

    protected boolean limitToOneIndex = true; // For GAE

    protected boolean hasInequalityFilter = false;

    protected EntityQueryCriteria<?> queryCriteria;

    protected List<InMemoryFilter> inMemoryFilters = new Vector<InMemoryFilter>();

    public IndexedEntitySearch(EntitySearchCriteria<?> searchCriteria) {
        this.searchCriteria = searchCriteria;
        this.entityClass = ServerEntityFactory.entityClass(searchCriteria.getDomainName());
        this.meta = EntityFactory.getEntityMeta(entityClass);
    }

    public void buildQueryCriteria() {
        queryCriteria = EntityQueryCriteria.create(entityClass);

        // TODO use groups in EntitySearchCriteria
        Set<MemberMeta> processed = new HashSet<MemberMeta>();
        IEntityPersistenceService srv = PersistenceServicesFactory.getPersistenceService();

        nextFilter: for (Map.Entry<PathSearch, Serializable> me : searchCriteria.getFilters().entrySet()) {
            if (me.getValue() == null) {
                continue;
            }
            PathSearch path = me.getKey();
            final int pathLength = path.getPathMembers().size();
            boolean inMemoryFilterOnly = false;
            if (pathLength > 1) {
                EntityMeta em = meta;
                MemberMeta mm = null;
                int count = 0;
                for (String memberName : path.getPathMembers()) {
                    //TODO ICollection support
                    if (mm != null) {
                        Class<?> valueClass = mm.getValueClass();
                        if (!(IEntity.class.isAssignableFrom(valueClass))) {
                            throw new RuntimeException("Invalid member in path " + memberName);
                        } else {
                            em = EntityFactory.getEntityMeta((Class<? extends IEntity>) valueClass);
                        }
                    }
                    mm = em.getMemberMeta(memberName);
                    count++;
                    if (pathLength == count) {
                        break;
                    }
                    if ((ICollection.class.isAssignableFrom(mm.getObjectClass()))) {
                        log.debug("path {} not implemented in storage", path);
                        inMemoryFilters.add(new CollectionInMemoryFilter(path, count, me.getValue()));
                        continue nextFilter;
                    }
                    if (!mm.isEmbedded()) {
                        log.debug("path {} not implemented in storage", path);
                        inMemoryFilterOnly = true;
                        break;
                    }
                }
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
                        if (inMemoryFilterOnly) {
                            inMemoryFilters.add(new StringInMemoryFilter(path, key));
                        } else {
                            queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.EQUAL, key));
                        }
                    }
                    //use secondary filter if required
                    for (String word : str.split(IndexString.KEYWORD_SPLIT_PATTERN)) {
                        word = word.toLowerCase();
                        if (word.length() > index.keywordLenght()) {
                            inMemoryFilters.add(new StringInMemoryFilter(path, word));
                        }
                    }
                } else {
                    // Simple like implementation
                    if (inMemoryFilterOnly || (hasInequalityFilter && limitToOneIndex)) {
                        // Add to in memory filters
                        inMemoryFilters.add(new StringInMemoryFilter(path, str));
                    } else {
                        // Database value should be capitalized for this to work.
                        char firstChar = str.charAt(0);
                        if (Character.isLetter(firstChar) && Character.isLowerCase(firstChar)) {
                            str = str.replaceFirst(String.valueOf(firstChar), String.valueOf(Character.toUpperCase(firstChar)));
                        }
                        String from = str;
                        String to = from + "z";
                        String propertyName = srv.getPropertyName(meta, path);
                        queryCriteria.add(new PropertyCriterion(propertyName, Restriction.GREATER_THAN_OR_EQUAL, from));
                        queryCriteria.add(new PropertyCriterion(propertyName, Restriction.LESS_THAN, to));
                        hasInequalityFilter = true;
                    }
                }
            } else if (Date.class.isAssignableFrom(mm.getValueClass())) {
                Date day = (Date) searchCriteria.getValue(new PathSearch(path.getPathString(), "day"));
                if (day != null) {
                    queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.EQUAL, TimeUtils.dayStart(day)));
                }
            } else if (GeoPoint.class.isAssignableFrom(mm.getValueClass())) {
                String pathWithGeoPointData = path.getPathString();
                Integer areaRadius = (Integer) searchCriteria.getValue(new PathSearch(pathWithGeoPointData, "radius"));
                GeoPoint geoPointFrom = (GeoPoint) searchCriteria.getValue(new PathSearch(pathWithGeoPointData, "from"));
                if ((areaRadius != null) && (geoPointFrom != null)) {
                    List<String> keys = GeoCell.getBestCoveringSet(new GeoCircle(geoPointFrom, areaRadius.intValue()));
                    log.debug("GEO search {}km; {} keys", areaRadius, keys.size());
                    queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.IN, (Serializable) keys));
                    inMemoryFilters.add(new GeoDistanceInMemoryFilter(new Path(pathWithGeoPointData), geoPointFrom, areaRadius.doubleValue()));
                }
                processed.add(mm);
            } else if (Enum.class.isAssignableFrom(mm.getValueClass())) {
                queryCriteria.add(new PropertyCriterion(srv.getPropertyName(meta, path), Restriction.EQUAL, me.getValue()));
            } else if (mm.isEntity()) {
                queryCriteria.add(new PropertyCriterion(srv.getPropertyName(meta, path), Restriction.EQUAL, ((IEntity) me.getValue()).getPrimaryKey()));
            } else {
                log.warn("Search by class {} not implemented", mm.getValueClass());
            }
        }
    }

    public SearchResultIterator<IEntity> getResult() {
        final ICursorIterator<? extends IEntity> unfiltered = PersistenceServicesFactory.getPersistenceService().query(null, queryCriteria);
        final int maxResults;
        final int firstResult;
        if (searchCriteria.getPageSize() > 0) {
            firstResult = searchCriteria.getPageSize() * (searchCriteria.getPageNumber());
            maxResults = firstResult + searchCriteria.getPageSize();
        } else {
            maxResults = Integer.MAX_VALUE;
            firstResult = -1;
        }

        return new SearchResultIterator<IEntity>() {

            int count = 0;

            IEntity next;

            IEntity last;

            IEntity more;

            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }
                if (count >= maxResults) {
                    return false;
                }

                // TODO This loop should be avoided using Cursor from previous query.
                while ((count < firstResult) && unfiltered.hasNext()) {
                    if (accept(unfiltered.next())) {
                        count++;
                    }
                }

                while (unfiltered.hasNext()) {
                    IEntity ent = unfiltered.next();
                    if (accept(ent)) {
                        next = ent;
                        break;
                    }
                }
                return (next != null);
            }

            @Override
            public boolean hasMoreData() {
                if (count < maxResults) {
                    return false;
                }
                if (more == null) {
                    while (unfiltered.hasNext()) {
                        IEntity ent = unfiltered.next();
                        if (accept(ent)) {
                            more = ent;
                            break;
                        }
                    }
                }
                return (more != null);
            }

            @Override
            public IEntity next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                count++;
                try {
                    return next;
                } finally {
                    last = next;
                    next = null;
                }
            }

            @Override
            public void remove() {
                if (last == null) {
                    throw new NoSuchElementException();
                }
                PersistenceServicesFactory.getPersistenceService().delete(last);
                last = null;
            }
        };

    }

    protected boolean accept(IEntity entity) {
        for (InMemoryFilter filter : inMemoryFilters) {
            if (!filter.accept(entity)) {
                return false;
            }
        }
        return true;
    }
}
