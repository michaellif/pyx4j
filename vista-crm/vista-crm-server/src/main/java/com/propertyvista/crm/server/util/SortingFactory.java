/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 4, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;

import com.propertyvista.domain.dashboard.gadgets.util.CustomComparator;

/**
 * Sorting engine for times when customized sorting of resuls is required.
 */
public class SortingFactory<X extends IEntity> {
    @SuppressWarnings("rawtypes")
    private final Map<String, Comparator> propertyToComparatorMap;

    @SuppressWarnings("rawtypes")
    public SortingFactory(Class<X> clazz) {
        Map<String, Comparator> temp = new HashMap<String, Comparator>();
        IEntity dtoProto = EntityFactory.getEntityPrototype(clazz);

        for (String memberName : dtoProto.getEntityMeta().getMemberNames()) {
            String propertyName = dtoProto.getMember(memberName).getPath().toString();
            CustomComparator customComparatorAnnotation = dtoProto.getMember(memberName).getMeta().getAnnotation(CustomComparator.class);
            Comparator propertyComparator = null;
            if (customComparatorAnnotation != null) {
                try {
                    propertyComparator = customComparatorAnnotation.clazz().newInstance();
                } catch (InstantiationException e) {
                    // TODO how to access logger?
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                temp.put(propertyName, propertyComparator);
            }

        }
        propertyToComparatorMap = Collections.unmodifiableMap(temp);
    }

    /**
     * Filter the given sorting criteria: extract the criteria for DTO properties that are mapped to DBO properties.
     * The properties that don't have mapping are skipped.
     * 
     * @param sortingCriteria
     *            list of criteria as was set up for DTO.
     * @return sorting criteria for mapped DBO fields.
     */
    public static List<Sort> extractSortCriteriaForDboProperties(List<Sort> sortingCriteria, Map<String, String> dto2dboPropertyMap) {
        List<Sort> extractedSorts = new LinkedList<Sort>();

        Iterator<Sort> i = sortingCriteria.iterator();
        while (i.hasNext()) {
            Sort sortingCriterion = i.next();
            String dboProperty = dto2dboPropertyMap.get(sortingCriterion.getPropertyName());
            if (dboProperty != null) {
                i.remove();
                extractedSorts.add(new Sort(dboProperty, sortingCriterion.isDescending()));
            }
        }

        return extractedSorts;
    }

    public Comparator<? super X> createDtoComparator(List<Sort> sortingCriteria) {
        return new CombinedComparator(sortingCriteria);
    }

    public void sortDto(List<X> unsortedList, List<Sort> sortCriteria) {
        List<Sort> relevantSortCriteria = new LinkedList<Sort>();

        for (Sort sortCriterion : sortCriteria) {
            relevantSortCriteria.add(sortCriterion);
        }

        Collections.sort(unsortedList, createDtoComparator(relevantSortCriteria));
    }

    // TODO consider optimizing performance by usage of arrays instead of lists...
    private class CombinedComparator implements Comparator<X> {
        @SuppressWarnings("rawtypes")
        final List<Comparator> comps;

        final List<Path> sortCriteria;

        @SuppressWarnings("rawtypes")
        public CombinedComparator(List<Sort> relevantSortCriteria) {
            comps = new ArrayList<Comparator>(relevantSortCriteria.size());
            sortCriteria = new ArrayList<Path>();

            for (Sort sortCriterion : relevantSortCriteria) {
                final Comparator cmp = propertyToComparatorMap.get(sortCriterion.getPropertyName());
                if (cmp == null) {
                    // TODO maybe throw exception/log error (someone is trying to sort something that has no associated comparator)
                    continue;
                }
                if (!sortCriterion.isDescending()) {
                    comps.add(new Comparator() {

                        @SuppressWarnings("unchecked")
                        @Override
                        public int compare(Object paramT1, Object paramT2) {
                            return -cmp.compare(paramT1, paramT2);
                        }

                    });
                } else {
                    comps.add(cmp);
                }
                sortCriteria.add(new Path(sortCriterion.getPropertyName()));
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compare(X paramT1, X paramT2) {
            @SuppressWarnings("rawtypes")
            Iterator<Comparator> ci = comps.iterator();
            Iterator<Path> si = sortCriteria.iterator();
            while (ci.hasNext()) {
                Path sortCriterion = si.next();
                @SuppressWarnings("rawtypes")
                Comparator cmp = ci.next();
                Object val1 = paramT1.getMember(sortCriterion).getValue();
                Object val2 = paramT2.getMember(sortCriterion).getValue();
                int result = cmp.compare(val1, val2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }
    }
}
