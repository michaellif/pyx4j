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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class EntityDTOHelper<DBO extends IEntity, DTO extends IEntity> {

    private final List<PropertyMapper> mappers;

    public EntityDTOHelper(Class<DBO> dboClass, Class<DTO> dtoClass, List<PropertyMapper> mappers) {
        this.mappers = mappers;
    }

    public Collection<Criterion> convertDTOSearchCriteria(Collection<Criterion> dtoSearchCriteria) {
        Collection<Criterion> dboFilters = new ArrayList<Criterion>();
        for (Criterion cr : dtoSearchCriteria) {
            if (cr instanceof PropertyCriterion) {
                PropertyCriterion propertyCriterion = (PropertyCriterion) cr;
                Path dboPath = dboPath(new Path(propertyCriterion.getPropertyPath()));
                if (dboPath == null) {
                    throw new IllegalStateException("DTO property " + propertyCriterion.getPropertyPath().toString() + " is not bound to DBO property");
                }
                dboFilters.add(new PropertyCriterion(dboPath.toString(), propertyCriterion.getRestriction(), propertyCriterion.getValue()));

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
        if (dtoSortingCriteria != null && !dtoSortingCriteria.isEmpty()) {

            for (Sort s : dtoSortingCriteria) {
                Path dboPath = dboPath(new Path(s.getPropertyPath()));
                if (dboPath == null) {
                    throw new IllegalStateException("DTO property " + s.getPropertyPath() + " is not bound to DBO property");
                }
                dboSortingCriteria.add(new Sort(dboPath.toString(), s.isDescending()));
            }
        }
        return dboSortingCriteria;
    }

    private Path dboPath(Path dtoPath) {
        Path dboPath = null;
        for (PropertyMapper mapper : mappers) {
            dboPath = mapper.getDboMemberPath(dtoPath);
            if (dboPath != null) {
                break;
            }
        }
        return dboPath;
    }

    public static interface PropertyMapper {

        Path getDboMemberPath(Path dtoMemberPath);

    }
}
