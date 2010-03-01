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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.server.IndexString;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.ServerEntityFactory;
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

        for (Map.Entry<PathSearch, Serializable> me : searchCriteria.getFilters().entrySet()) {
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
                        queryCriteria.add(new PropertyCriterion(PersistenceServicesFactory.getPersistenceService().getIndexedPropertyName(mm),
                                Restriction.EQUAL, key));
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
                    if (hasInequalityFilter && limitToOneIndex) {
                        // Add to in memory filters
                        inMemoryFilters.add(new StringInMemoryFilter(path, str));
                        continue;
                    }
                    char firstChar = str.charAt(0);
                    if (Character.isLetter(firstChar) && Character.isLowerCase(firstChar)) {
                        str = str.replaceFirst(String.valueOf(firstChar), String.valueOf(Character.toUpperCase(firstChar)));
                    }
                    String from = str;
                    String to = from + "z";
                    queryCriteria.add(new PropertyCriterion(mm.getFieldName(), Restriction.GREATER_THAN_OR_EQUAL, from));
                    queryCriteria.add(new PropertyCriterion(mm.getFieldName(), Restriction.LESS_THAN, to));
                    hasInequalityFilter = true;
                }
            } else if (GeoPoint.class.isAssignableFrom(mm.getValueClass())) {
                String pathWithGeoPointData = path.getPathString();
                Integer areaRadius = (Integer) searchCriteria.getValue(new PathSearch(pathWithGeoPointData, "radius"));
                GeoPoint geoPointFrom = (GeoPoint) searchCriteria.getValue(new PathSearch(pathWithGeoPointData, "from"));
                if ((areaRadius != null) && (geoPointFrom != null)) {
                    List<String> keys = GeoCell.getBestCoveringSet(new GeoCircle(geoPointFrom, areaRadius.intValue()));
                    log.debug("GEO search {}km; {} keys", areaRadius, keys.size());
                    queryCriteria.add(new PropertyCriterion(PersistenceServicesFactory.getPersistenceService().getIndexedPropertyName(mm), Restriction.IN,
                            (Serializable) keys));
                    inMemoryFilters.add(new GeoDistanceInMemoryFilter(new Path(pathWithGeoPointData), geoPointFrom, areaRadius.doubleValue()));
                }
                processed.add(mm);
            } else {
                log.warn("Search by class {} not implemented", mm.getValueClass());
            }
        }
    }

    public Iterable<IEntity> getResult() {

        List<? extends IEntity> rc = PersistenceServicesFactory.getPersistenceService().query(queryCriteria);

        final Iterator<? extends IEntity> unfiltered = rc.iterator();
        final int maxResults;
        final int firstResult;
        if (searchCriteria.getPageSize() > 0) {
            maxResults = searchCriteria.getPageSize();
            firstResult = searchCriteria.getPageSize() * (searchCriteria.getPageNumber() - 1);
        } else {
            maxResults = Integer.MAX_VALUE;
            firstResult = -1;
        }

        return new Iterable<IEntity>() {
            @Override
            public Iterator<IEntity> iterator() {
                return new Iterator<IEntity>() {

                    int count = 0;

                    IEntity next;

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
                    public IEntity next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        count++;
                        try {
                            return next;
                        } finally {
                            next = null;
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
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
