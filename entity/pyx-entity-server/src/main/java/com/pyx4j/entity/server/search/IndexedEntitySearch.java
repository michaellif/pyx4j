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
import java.util.Collection;
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
import com.pyx4j.config.shared.ApplicationBackend;
import com.pyx4j.config.shared.ApplicationBackend.ApplicationBackendType;
import com.pyx4j.entity.adapters.IndexAdapter;
import com.pyx4j.entity.adapters.index.GeoPointIndexAdapter;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.rpc.GeoCriteria;
import com.pyx4j.entity.server.AdapterFactory;
import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.IndexString;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitiveSet;
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
        this.entityClass = searchCriteria.getEntityClass();
        this.meta = EntityFactory.getEntityMeta(entityClass);
    }

    public void buildQueryCriteria() {
        queryCriteria = EntityQueryCriteria.create(entityClass);
        queryCriteria.setSorts(searchCriteria.getSorts());

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
                    if (mm.isTransient()) {
                        log.debug("transient path {}", path);
                        continue nextFilter;
                    }
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
                            if (index.global() != 0) {
                                queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.EQUAL, String.valueOf(index
                                        .global()) + key));
                            } else {
                                queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.EQUAL, key));
                            }
                        }
                    }
                    //use secondary filter if required
                    List<String> words = IndexString.splitIndexValues(str.toLowerCase());
                    log.debug("keywords {}", words);
                    if (words.size() == 1) {
                        inMemoryFilters.add(new StringInMemoryFilter(path, words.get(0)));
                    } else {
                        if (EditorType.phone.equals(mm.getEditorType())) {
                            inMemoryFilters.add(new StringCompositeOrderedInMemoryFilter(path, words));
                        } else {
                            inMemoryFilters.add(new StringCompositeInMemoryFilter(path, words));
                        }
                    }
                } else {
                    // Simple like implementation
                    if (inMemoryFilterOnly || (hasInequalityFilter && limitToOneIndex)) {
                        // Add to in memory filters
                        inMemoryFilters.add(new StringInMemoryFilter(path, str));
                    } else if (ApplicationBackend.getBackendType() == ApplicationBackendType.RDB) {
                        String propertyName = srv.getPropertyName(meta, path);
                        queryCriteria.add(new PropertyCriterion(propertyName, Restriction.RDB_LIKE, str));
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
                Date day = (Date) searchCriteria.getValue(new PathSearch(mm, path.getPathString(), "day"));
                if (day != null) {
                    queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.EQUAL, TimeUtils.dayStart(day)));
                    // Add in case index criteria is dropped by search 
                    inMemoryFilters.add(new DayInMemoryFilter(path, day));
                } else {
                    Date from = (Date) searchCriteria.getValue(new PathSearch(mm, path.getPathString(), "from"));
                    Date to = (Date) searchCriteria.getValue(new PathSearch(mm, path.getPathString(), "to"));
                    if ((from != null) || (to != null)) {
                        inMemoryFilters.add(new DayRangeInMemoryFilter(path, from, to));
                        if (!hasInequalityFilter) {
                            if (from != null) {
                                queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.GREATER_THAN_OR_EQUAL, TimeUtils
                                        .dayStart(from)));
                                hasInequalityFilter = true;
                            }
                            if (to != null) {
                                queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.LESS_THAN, TimeUtils.dayEnd(to)));
                                hasInequalityFilter = true;
                            }
                        }
                    }
                }
                processed.add(mm);
            } else if (Integer.class.isAssignableFrom(mm.getValueClass())) {
                Serializable value = me.getValue();
                if (value instanceof String) {
                    value = Integer.valueOf((String) value);
                } else if (!(value instanceof Integer)) {
                    log.error("can't conver value to integer {}", value);
                }
                Indexed index = mm.getAnnotation(Indexed.class);
                if ((index != null) && (index.global() != 0)) {
                    queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.EQUAL, String.valueOf(index.global()) + value));
                    inMemoryFilters.add(new PrimitiveInMemoryFilter(path, value));
                } else {
                    queryCriteria.add(new PropertyCriterion(srv.getPropertyName(meta, path), Restriction.EQUAL, value));
                }
            } else if (Long.class.isAssignableFrom(mm.getValueClass())) {
                Serializable value = me.getValue();
                if (value instanceof String) {
                    value = Long.valueOf((String) value);
                } else if (!(value instanceof Long)) {
                    log.error("can't conver value to long {}", value);
                }
                Indexed index = mm.getAnnotation(Indexed.class);
                if ((index != null) && (index.global() != 0)) {
                    queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.EQUAL, String.valueOf(index.global()) + value));
                    inMemoryFilters.add(new PrimitiveInMemoryFilter(path, value));
                } else {
                    queryCriteria.add(new PropertyCriterion(srv.getPropertyName(meta, path), Restriction.EQUAL, value));
                }
            } else if (GeoCriteria.class.isAssignableFrom(mm.getValueClass())) {
                String pathWithGeoPointData = path.getPathString();
                pathWithGeoPointData = pathWithGeoPointData.substring(0, pathWithGeoPointData.length() - ("Criteria".length() + 1)) + Path.PATH_SEPARATOR;
                Path geoPath = new Path(pathWithGeoPointData);
                Integer areaRadius = null;
                GeoPoint geoPointFrom = null;
                GeoCriteria value = (GeoCriteria) me.getValue();
                if (value != null) {
                    geoPointFrom = value.geoPoint().getValue();
                    areaRadius = (Integer) searchCriteria.getValue(new PathSearch(mm, path.getPathString() + "radius" + Path.PATH_SEPARATOR, null));
                }
                if ((areaRadius != null) && (geoPointFrom != null)) {
                    List<String> keys = GeoCell.getBestCoveringSet(new GeoCircle(geoPointFrom, areaRadius.intValue()));
                    GeoPointIndexAdapter indexAdapter = null;
                    if (value.returnAnyLocation().isBooleanTrue()) {
                        keys.add(GeoCell.GEOCELL_ANYLOCATION);
                        MemberMeta geoMemberMeta = meta.getMemberMeta(geoPath);
                        Indexed index = geoMemberMeta.getAnnotation(Indexed.class);
                        for (Class<? extends IndexAdapter<?>> adapterClass : index.adapters()) {
                            if (GeoPointIndexAdapter.class.isAssignableFrom(adapterClass)) {
                                indexAdapter = (GeoPointIndexAdapter) AdapterFactory.getIndexAdapter(adapterClass);
                                break;
                            }
                        }
                    }
                    log.debug("GEO search {}km; {} keys", areaRadius, keys.size());
                    queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, geoPath), Restriction.IN, (Serializable) keys));
                    inMemoryFilters.add(new GeoDistanceInMemoryFilter(geoPath, geoPointFrom, areaRadius.doubleValue(), indexAdapter));
                    hasInequalityFilter = true;
                }
            } else if (GeoPoint.class.isAssignableFrom(mm.getValueClass())) {
                String pathWithGeoPointData = path.getPathString();
                Integer areaRadius = (Integer) searchCriteria.getValue(new PathSearch(mm, pathWithGeoPointData, "radius"));
                GeoPoint geoPointFrom = (GeoPoint) searchCriteria.getValue(new PathSearch(mm, pathWithGeoPointData, "from"));
                if ((areaRadius != null) && (geoPointFrom != null)) {
                    List<String> keys = GeoCell.getBestCoveringSet(new GeoCircle(geoPointFrom, areaRadius.intValue()));
                    log.debug("GEO search {}km; {} keys", areaRadius, keys.size());
                    queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.IN, (Serializable) keys));
                    inMemoryFilters.add(new GeoDistanceInMemoryFilter(new Path(pathWithGeoPointData), geoPointFrom, areaRadius.doubleValue(), null));
                    hasInequalityFilter = true;
                }
                processed.add(mm);
            } else if (Enum.class.isAssignableFrom(mm.getValueClass())) {
                Indexed index = mm.getAnnotation(Indexed.class);
                if ((index != null) && (index.global() != 0)) {
                    Serializable value = me.getValue();
                    if (value instanceof Collection<?>) {
                        Vector<String> values = new Vector<String>();
                        for (Object item : (Collection<?>) value) {
                            values.add(String.valueOf(index.global()) + ((Enum<?>) item).name());
                        }
                        queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.IN, values));
                        inMemoryFilters.add(new PrimitiveInCollectionInMemoryFilter(path, (Collection<?>) value));
                    } else {
                        queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.EQUAL, String.valueOf(index.global())
                                + ((Enum<?>) value).name()));
                        // Add in case index criteria is dropped by search 
                        if (IPrimitiveSet.class.isAssignableFrom(mm.getObjectClass())) {
                            inMemoryFilters.add(new PrimitiveSetInMemoryFilter(path, value));
                        } else {
                            inMemoryFilters.add(new PrimitiveInMemoryFilter(path, value));
                        }
                    }
                } else {
                    queryCriteria.add(new PropertyCriterion(srv.getPropertyName(meta, path), Restriction.EQUAL, me.getValue()));
                }
            } else if (mm.isEntity()) {
                Indexed index = mm.getAnnotation(Indexed.class);
                Long pk = ((IEntity) me.getValue()).getPrimaryKey();
                if ((index != null) && (index.global() != 0)) {
                    inMemoryFilters.add(new EntityInMemoryFilter(path, pk));
                    queryCriteria.add(new PropertyCriterion(srv.getIndexedPropertyName(meta, path), Restriction.EQUAL, String.valueOf(index.global()) + pk));
                } else {
                    queryCriteria.add(new PropertyCriterion(srv.getPropertyName(meta, path), Restriction.EQUAL, pk));
                }
            } else if ((ICollection.class.isAssignableFrom(mm.getObjectClass()))) {
                Long pk = ((IEntity) me.getValue()).getPrimaryKey();
                queryCriteria.add(new PropertyCriterion(srv.getPropertyName(meta, path), Restriction.EQUAL, pk));
            } else {
                log.warn("Search by class {} {} not implemented", mm.getObjectClass(), mm.getValueClass());
            }
        }
        log.debug("will have used {} inMemoryFilters", inMemoryFilters.size());
    }

    public SearchResultIterator<IEntity> getResult(final String encodedCursorReference) {
        final ICursorIterator<? extends IEntity> unfiltered = PersistenceServicesFactory.getPersistenceService().query(encodedCursorReference, queryCriteria);
        final int maxResults;
        final int firstResult;
        if ((searchCriteria.getPageSize() > 0) && (encodedCursorReference == null)) {
            firstResult = searchCriteria.getPageSize() * (searchCriteria.getPageNumber());
            maxResults = firstResult + searchCriteria.getPageSize();
            if (searchCriteria.getPageNumber() > 0) {
                log.warn("CursorReference is missing. Scroll to firstResult:", firstResult);
            }
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

                // we do not need to scroll to first result if we are passing the cursor reference
                if (encodedCursorReference == null) {
                    while ((count < firstResult) && unfiltered.hasNext()) {
                        log.info("Count {} firstResult {}", count, firstResult);
                        if (accept(unfiltered.next())) {
                            count++;
                        }
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
            public String encodedCursorReference() {
                return unfiltered.encodedCursorReference();
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

            @Override
            public void completeRetrieval() {
                unfiltered.completeRetrieval();
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
