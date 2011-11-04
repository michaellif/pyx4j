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

import com.propertyvista.domain.dashboard.gadgets.CustomComparator;

/**
 * Sorting engine for DTO classes. Provides various utilities to aid DBO to DTO mapping and sorting of DTO objects.
 */
public class DTOSortingFactory<DTO extends IEntity> {
    @SuppressWarnings("rawtypes")
    private final Map<String, Comparator> dtoPropertyToComparatorMap;

    @SuppressWarnings("rawtypes")
    public DTOSortingFactory(Class<DTO> dtoClazz) {
        Map<String, Comparator> temp = new HashMap<String, Comparator>();
        IEntity dtoProto = EntityFactory.getEntityPrototype(dtoClazz);

        for (String memberName : dtoProto.getEntityMeta().getMemberNames()) {
            String propertyName = dtoProto.getMember(memberName).getPath().toString();
            CustomComparator customComparatorAnnotation = dtoProto.getMember(memberName).getMeta().getAnnotation(CustomComparator.class);
            Comparator propertyComparator = null;
            if (customComparatorAnnotation != null) {
                try {
                    propertyComparator = customComparatorAnnotation.clazz().newInstance();
                } catch (InstantiationException e) {
                    // TODO do something (log maybe) (ask Vlad how to access the logger)
                } catch (IllegalAccessException e) {
                    // TODO do something (log maybe) (ask Vlad how to access the logger)
                }
                temp.put(propertyName, propertyComparator);
            }

        }
        dtoPropertyToComparatorMap = Collections.unmodifiableMap(temp);
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

    public Comparator<? super DTO> createDtoComparator(List<Sort> sortingCriteria) {
        return new CombinedComparator(sortingCriteria);
    }

    public void sortDto(List<DTO> unsortedList, List<Sort> sortCriteria) {
        List<Sort> relevantSortCriteria = new LinkedList<Sort>();

        for (Sort sortCriterion : sortCriteria) {
            relevantSortCriteria.add(sortCriterion);
        }

        Collections.sort(unsortedList, createDtoComparator(relevantSortCriteria));
    }

    private class CombinedComparator implements Comparator<DTO> {
        @SuppressWarnings("rawtypes")
        final List<Comparator> comps;

        final List<Sort> sortCriteria;

        @SuppressWarnings("rawtypes")
        public CombinedComparator(List<Sort> relevantSortCriteria) {
            comps = new ArrayList<Comparator>(relevantSortCriteria.size());
            sortCriteria = relevantSortCriteria;

            for (Sort sortCriterion : sortCriteria) {
                final Comparator cmp = dtoPropertyToComparatorMap.get(sortCriterion.getPropertyName());
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
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compare(DTO paramT1, DTO paramT2) {
            @SuppressWarnings("rawtypes")
            Iterator<Comparator> ci = comps.iterator();
            Iterator<Sort> si = sortCriteria.iterator();
            while (ci.hasNext() & si.hasNext()) {
                Sort sortCriterion = si.next();
                @SuppressWarnings("rawtypes")
                Comparator cmp = ci.next();
                Object val1 = paramT1.getMember(new Path(sortCriterion.getPropertyName())).getValue();
                Object val2 = paramT2.getMember(new Path(sortCriterion.getPropertyName())).getValue();
                int result = cmp.compare(val1, val2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        }
    }
}
