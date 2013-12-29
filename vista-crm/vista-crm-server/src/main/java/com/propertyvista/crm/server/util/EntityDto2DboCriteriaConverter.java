/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.utils.EntityBinder;

/**
 * Utility class for converting DTO search/sorting criteria to DBO
 * 
 * @author ArtyomB
 * 
 * @param <DBO>
 * @param <DTO>
 */
public class EntityDto2DboCriteriaConverter<DBO extends IEntity, DTO extends IEntity> {

    private final List<PropertyMapper> mappers;

    /**
     * Creates a mapper from {@link entity DTO binder}
     */
    public static PropertyMapper makeMapper(EntityBinder<?, ?> dtoBinder) {
        return new DtoBinderMapper(dtoBinder);
    }

    /**
     * @param dboClass
     * @param dtoClass
     * @param mappers
     *            mappers that map DTO members to DBO members
     */
    public EntityDto2DboCriteriaConverter(Class<DBO> dboClass, Class<DTO> dtoClass, PropertyMapper... mappers) {
        this(dboClass, dtoClass, Arrays.asList(mappers));
    }

    public EntityDto2DboCriteriaConverter(Class<DBO> dboClass, Class<DTO> dtoClass, List<PropertyMapper> mappers) {
        this.mappers = mappers;
    }

    public Collection<Criterion> convertDTOSearchCriteria(Collection<Criterion> dtoSearchCriteria) {
        Collection<Criterion> dboFilters = new ArrayList<Criterion>();
        if (dtoSearchCriteria == null) {
            return dboFilters;
        }
        for (Criterion cr : dtoSearchCriteria) {
            if (cr instanceof PropertyCriterion) {
                PropertyCriterion propertyCriterion = (PropertyCriterion) cr;
                Path dtoPath = new Path(propertyCriterion.getPropertyPath());
                PropertyMapper mapper = getMapperForPath(dtoPath);
                if (mapper == null) {
                    throw new IllegalStateException("DTO property " + propertyCriterion.getPropertyPath().toString() + " is not bound to DBO property");
                }
                dboFilters.add(new PropertyCriterion(mapper.getDboMemberPath(dtoPath), propertyCriterion.getRestriction(), mapper
                        .convertValue(propertyCriterion.getValue())));

            } else if (cr instanceof OrCriterion) {
                OrCriterion criterion = new OrCriterion();
                criterion.addRight(convertDTOSearchCriteria(((OrCriterion) cr).getFiltersRight()));
                criterion.addLeft(convertDTOSearchCriteria(((OrCriterion) cr).getFiltersLeft()));
                dboFilters.add(criterion);
            } else {
                throw new IllegalArgumentException("Can't convert " + cr.getClass() + " criteria");
            }
        }
        return dboFilters;
    }

    public List<Sort> convertDTOSortingCriteria(List<Sort> dtoSortingCriteria) {
        List<Sort> dboSortingCriteria = new ArrayList<Sort>();
        if (dtoSortingCriteria == null) {
            return dboSortingCriteria;
        }
        if (dtoSortingCriteria != null && !dtoSortingCriteria.isEmpty()) {

            for (Sort s : dtoSortingCriteria) {
                Path dtoPath = new Path(s.getPropertyPath());
                Path dboPath = getMapperForPath(dtoPath).getDboMemberPath(dtoPath);
                if (dboPath == null) {
                    throw new IllegalStateException("DTO property " + s.getPropertyPath() + " is not bound to DBO property");
                }
                dboSortingCriteria.add(new Sort(dboPath.toString(), s.isDescending()));
            }
        }
        return dboSortingCriteria;
    }

    private PropertyMapper getMapperForPath(Path dtoPath) {
        // TODO implement as a map 
        Path dboPath = null;
        for (PropertyMapper mapper : mappers) {
            dboPath = mapper.getDboMemberPath(dtoPath);
            if (dboPath != null) {
                return mapper;
            }
        }
        return null;
    }

    public static interface PropertyMapper {

        Path getDboMemberPath(Path dtoMemberPath);

        Serializable convertValue(Serializable value);
    }

    private static class DtoBinderMapper implements PropertyMapper {

        private final EntityBinder<?, ?> binder;

        public DtoBinderMapper(EntityBinder<?, ?> binder) {
            this.binder = binder;
        }

        @Override
        public Path getDboMemberPath(Path dtoMemberPath) {
            return binder.getBoundDboMemberPath(dtoMemberPath);
        }

        @Override
        public Serializable convertValue(Serializable value) {
            return value;
        }

    }
}
