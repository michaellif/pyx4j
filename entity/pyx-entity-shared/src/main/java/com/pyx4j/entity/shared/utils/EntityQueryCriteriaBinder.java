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
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.core.EntityFactory;
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

    public interface CriteriaValueConverter {

        public Serializable convertValue(PropertyCriterion toPropertyCriterion);

    }

    public interface CriterionConverter<C extends Criterion, BO extends IEntity> {

        /**
         * If returns not null Criterion will be added to criteria.
         */
        public Criterion convertCriterion(C toCriterion, EntityQueryCriteria<BO> criteria);

    }

    public interface CriteriaEnhancer<BO extends IEntity> {

        public void enhanceCriteria(PropertyCriterion toCriterion, EntityQueryCriteria<BO> criteria);

    }

    public interface DefaultCriteriaEnhancer<BO extends IEntity> {

        public void enhanceCriteria(EntityQueryCriteria<BO> criteria);

    }

    private final EntityBinder<BO, TO> binder;

    private final Map<Path, Path> pathBinding = new HashMap<>();

    private final Map<Path, CriteriaValueConverter> valueConverterBinding = new HashMap<>();

    private final Map<Class<? extends Criterion>, CriterionConverter<?, BO>> criterionConverterBinding = new HashMap<>();

    private final Map<Path, CriteriaEnhancer<BO>> criteriaEnhancerBinding = new HashMap<>();

    private final List<DefaultCriteriaEnhancer<TO>> defaultTOCriteriaEnhancers = new ArrayList<>();

    private final List<DefaultCriteriaEnhancer<BO>> defaultCriteriaEnhancers = new ArrayList<>();

    private final TO toProto;

    public static <BO extends IEntity, TO extends IEntity> EntityQueryCriteriaBinder<BO, TO> create(EntityBinder<BO, TO> binder) {
        return new EntityQueryCriteriaBinder<BO, TO>(binder);
    }

    public EntityQueryCriteriaBinder(EntityBinder<BO, TO> binder) {
        this.binder = binder;
        this.toProto = EntityFactory.getEntityPrototype(binder.toClass());
    }

    public final TO proto() {
        return toProto;
    }

    /**
     * This is used to create column Sorting
     */
    public final void map(IObject<?> toMember, IObject<?> boMember) {
        bind(toMember.getPath(), boMember.getPath());
    }

    /**
     * @deprecated use map()
     */
    @Deprecated
    public final void bind(IObject<?> toMember, IObject<?> boMember) {
        bind(toMember.getPath(), boMember.getPath());
    }

    public final void bind(Path toPath, Path boPath) {
        pathBinding.put(toPath, boPath);
    }

    /**
     * This Enhancer executed First, to setup default TO Criteria override what had came from UI
     */
    public final void addDefaultTOCriteriaEnhancer(DefaultCriteriaEnhancer<TO> valueConvertor) {
        defaultTOCriteriaEnhancers.add(valueConvertor);
    }

    public final void addCriteriaValueConverter(IObject<?> toMember, CriteriaValueConverter valueConvertor) {
        valueConverterBinding.put(toMember.getPath(), valueConvertor);
    }

    public final <C extends Criterion> void addCriterionConverter(Class<C> toCriterionClass, CriterionConverter<C, BO> criterionConverter) {
        criterionConverterBinding.put(toCriterionClass, criterionConverter);
    }

    /**
     * This Enhancer executed last, after all TO Criteria converted
     */
    public final void addDefaultCriteriaEnhancer(DefaultCriteriaEnhancer<BO> valueConvertor) {
        defaultCriteriaEnhancers.add(valueConvertor);
    }

    public final void addCriteriaEnhancer(IObject<?> toMember, CriteriaEnhancer<BO> valueConvertor) {
        criteriaEnhancerBinding.put(toMember.getPath(), valueConvertor);
    }

    public final EntityListCriteria<BO> convertListCriteria(EntityListCriteria<TO> toCriteria) {
        EntityListCriteria<BO> boCriteria = EntityListCriteria.create(binder.boClass());
        boCriteria.setPageNumber(toCriteria.getPageNumber());
        boCriteria.setPageSize(toCriteria.getPageSize());
        boCriteria.setVersionedCriteria(toCriteria.getVersionedCriteria());
        convertCriteria(toCriteria, boCriteria);
        return boCriteria;
    }

    public final EntityQueryCriteria<BO> convertQueryCriteria(EntityQueryCriteria<TO> toCriteria) {
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
        for (DefaultCriteriaEnhancer<TO> enhancer : defaultTOCriteriaEnhancers) {
            enhancer.enhanceCriteria(toCriteria);
        }

        if ((toCriteria.getFilters() != null) && (!toCriteria.getFilters().isEmpty())) {
            boCriteria.addAll(convertFilters(boCriteria, toCriteria.getFilters()));
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
        for (DefaultCriteriaEnhancer<BO> enhancer : defaultCriteriaEnhancers) {
            enhancer.enhanceCriteria(boCriteria);
        }
    }

    private Collection<Criterion> convertFilters(EntityQueryCriteria<BO> boCriteria, Collection<? extends Criterion> toFilters) {
        Collection<Criterion> boFilters = new ArrayList<Criterion>();
        for (Criterion toCriterion : toFilters) {
            Criterion criterion = convertCriterion(boCriteria, toCriterion);
            if (criterion != null) {
                boFilters.add(criterion);
            }
        }
        return boFilters;
    }

    @SuppressWarnings("unchecked")
    protected final Criterion convertCriterion(EntityQueryCriteria<BO> boCriteria, Criterion toCriterion) {
        @SuppressWarnings("rawtypes")
        CriterionConverter converter = criterionConverterBinding.get(toCriterion.getClass());
        if (converter != null) {
            return converter.convertCriterion(toCriterion, boCriteria);
        } else if (toCriterion instanceof PropertyCriterion) {
            PropertyCriterion propertyCriterion = (PropertyCriterion) toCriterion;
            Path toPath = propertyCriterion.getPropertyPath();

            CriteriaEnhancer<BO> valueConvertor = criteriaEnhancerBinding.get(toPath);
            if (valueConvertor != null) {
                valueConvertor.enhanceCriteria(propertyCriterion, boCriteria);
                return null;
            } else {
                return new PropertyCriterion(boPath(toPath), propertyCriterion.getRestriction(), convertValue(boCriteria, toPath, propertyCriterion));
            }
        } else if (toCriterion instanceof OrCriterion) {
            OrCriterion boCriterion = new OrCriterion();
            boCriterion.addAll(convertFilters(boCriteria, ((OrCriterion) toCriterion).getFilters()));
            return boCriterion;
        } else if (toCriterion instanceof AndCriterion) {
            AndCriterion criterion = new AndCriterion();
            criterion.addAll(convertFilters(boCriteria, ((AndCriterion) toCriterion).getFilters()));
            return criterion;
        } else if (toCriterion instanceof RangeCriterion) {
            AndCriterion criterion = new AndCriterion();
            criterion.addAll(convertFilters(boCriteria, ((RangeCriterion) toCriterion).getFilters()));
            return criterion;
        } else {
            throw new IllegalArgumentException("Can't convert " + toCriterion.getClass() + " criteria");
        }
    }

    protected final Serializable convertValue(EntityQueryCriteria<BO> boCriteria, Path toPath, PropertyCriterion toPropertyCriterion) {
        CriteriaValueConverter converter = valueConverterBinding.get(toPath);
        if (converter != null) {
            return converter.convertValue(toPropertyCriterion);
        } else {
            Serializable value = toPropertyCriterion.getValue();
            if (value instanceof Path) {
                return boPath((Path) value);
            } else if (value instanceof Criterion) {
                return convertCriterion(boCriteria, (Criterion) value);
            } else {
                return value;
            }
        }
    }
}
