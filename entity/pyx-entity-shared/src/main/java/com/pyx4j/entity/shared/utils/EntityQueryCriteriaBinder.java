/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Nov 12, 2014
 * @author vlads
 */
package com.pyx4j.entity.shared.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.RangeCriterion;

public final class EntityQueryCriteriaBinder<BO extends IEntity, TO extends IEntity> {

    public interface CriteriaValueConverter<TO extends IEntity> {

        public Serializable convertValue(PropertyCriterion toPropertyCriterion);

    }

    public interface CriterionConverter<C extends Criterion> {

        public Criterion convertCriterion(C toCriterion);

    }

    private final EntityBinder<BO, TO> binder;

    private final Map<Path, Path> pathBinding = new HashMap<>();

    private final Map<Path, CriteriaValueConverter<TO>> valueConverterBinding = new HashMap<>();

    private final Map<Class<? extends Criterion>, CriterionConverter<?>> criterionConverterBinding = new HashMap<>();

    public static <BO extends IEntity, TO extends IEntity> EntityQueryCriteriaBinder<BO, TO> create(EntityBinder<BO, TO> binder) {
        return new EntityQueryCriteriaBinder<BO, TO>(binder);
    }

    public EntityQueryCriteriaBinder(EntityBinder<BO, TO> binder) {
        this.binder = binder;
    }

    public void bind(IObject<?> toMember, IObject<?> boMember) {
        bind(toMember.getPath(), boMember.getPath());
    }

    public void bind(Path toPath, Path boPath) {
        pathBinding.put(toPath, boPath);
    }

    public void addCriteriaValueConverter(Path toPath, CriteriaValueConverter<TO> valueConvertor) {
        valueConverterBinding.put(toPath, valueConvertor);
    }

    public <C extends Criterion> void addCriterionConverter(Class<C> toCriterionClass, CriterionConverter<C> criterionConverter) {
        criterionConverterBinding.put(toCriterionClass, criterionConverter);
    }

    public EntityListCriteria<BO> convertListCriteria(EntityListCriteria<TO> toCriteria) {
        EntityListCriteria<BO> boCriteria = EntityListCriteria.create(binder.boClass());
        boCriteria.setPageNumber(toCriteria.getPageNumber());
        boCriteria.setPageSize(toCriteria.getPageSize());
        boCriteria.setVersionedCriteria(toCriteria.getVersionedCriteria());
        convertCriteria(toCriteria, boCriteria);
        return boCriteria;
    }

    public EntityQueryCriteria<BO> convertQueryCriteria(EntityQueryCriteria<TO> toCriteria) {
        EntityQueryCriteria<BO> boCriteria = EntityQueryCriteria.create(binder.boClass());
        boCriteria.setVersionedCriteria(toCriteria.getVersionedCriteria());
        convertCriteria(toCriteria, boCriteria);
        return boCriteria;
    }

    private Path boPath(Path toPath) {
        Path boPath = pathBinding.get(toPath);
        if (boPath == null) {
            boPath = binder.getBoundBOMemberPath(toPath);
        }
        if (boPath == null) {
            throw new Error("Unsupported query property path " + toPath);
        }
        return boPath;
    }

    private void convertCriteria(EntityQueryCriteria<TO> toCriteria, EntityQueryCriteria<BO> boCriteria) {
        if ((toCriteria.getFilters() != null) && (!toCriteria.getFilters().isEmpty())) {
            boCriteria.addAll(convertFilters(toCriteria.getFilters()));
        }
        if ((toCriteria.getSorts() != null) && (!toCriteria.getSorts().isEmpty())) {
            for (Sort s : toCriteria.getSorts()) {
                Path toPath = s.getPropertyPath();
                Path boPath = boPath(toPath);
                if (s.isDescending()) {
                    boCriteria.desc(boPath);
                } else {
                    boCriteria.asc(boPath);
                }
            }
        }
    }

    private Collection<Criterion> convertFilters(Collection<? extends Criterion> toFilters) {
        Collection<Criterion> boFilters = new ArrayList<Criterion>();
        for (Criterion toCriterion : toFilters) {
            Criterion criterion = convertCriterion(toCriterion);
            if (criterion != null) {
                boFilters.add(criterion);
            }
        }
        return boFilters;
    }

    @SuppressWarnings("unchecked")
    protected Criterion convertCriterion(Criterion toCriterion) {
        @SuppressWarnings("rawtypes")
        CriterionConverter converter = criterionConverterBinding.get(toCriterion.getClass());
        if (converter != null) {
            return converter.convertCriterion(toCriterion);
        } else if (toCriterion instanceof PropertyCriterion) {
            PropertyCriterion propertyCriterion = (PropertyCriterion) toCriterion;
            Path toPath = propertyCriterion.getPropertyPath();
            return new PropertyCriterion(boPath(toPath), propertyCriterion.getRestriction(), convertValue(toPath, propertyCriterion));
        } else if (toCriterion instanceof OrCriterion) {
            OrCriterion boCriterion = new OrCriterion();
            boCriterion.addRight(convertFilters(((OrCriterion) toCriterion).getFiltersRight()));
            boCriterion.addLeft(convertFilters(((OrCriterion) toCriterion).getFiltersLeft()));
            return boCriterion;
        } else if (toCriterion instanceof AndCriterion) {
            AndCriterion criterion = new AndCriterion();
            criterion.addAll(convertFilters(((AndCriterion) toCriterion).getFilters()));
            return criterion;
        } else if (toCriterion instanceof RangeCriterion) {
            AndCriterion criterion = new AndCriterion();
            criterion.addAll(convertFilters(((RangeCriterion) toCriterion).getFilters()));
            return criterion;
        } else {
            throw new IllegalArgumentException("Can't convert " + toCriterion.getClass() + " criteria");
        }
    }

    protected Serializable convertValue(Path toPath, PropertyCriterion toPropertyCriterion) {
        CriteriaValueConverter<TO> converter = valueConverterBinding.get(toPath);
        if (converter != null) {
            return converter.convertValue(toPropertyCriterion);
        } else {
            Serializable value = toPropertyCriterion.getValue();
            if (value instanceof Path) {
                return boPath((Path) value);
            } else if (value instanceof Criterion) {
                return convertCriterion((Criterion) value);
            } else {
                return value;
            }
        }
    }
}
