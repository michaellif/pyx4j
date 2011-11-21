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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.meta.MemberMeta;

import com.propertyvista.domain.dashboard.gadgets.util.CustomComparator;

/**
 * This is supposed to be thread safe sort engine/comparator factory for transient fields. Maybe I'm going to write detailed usage information when it works
 */
public class TransientPropertySortEngine<X extends IEntity> {
    @SuppressWarnings("rawtypes")
    private final Map<String, Comparator> transientProperties;

    @SuppressWarnings("rawtypes")
    public TransientPropertySortEngine(Class<X> clazz) {
        Map<String, Comparator> temp = new HashMap<String, Comparator>();
        IEntity proto = EntityFactory.getEntityPrototype(clazz);

        for (String memberName : proto.getEntityMeta().getMemberNames()) {
            String propertyName;
            Comparator propertyComparator = null;
            MemberMeta memberMeta = proto.getMember(memberName).getMeta();

            if (memberMeta.getAnnotation(Transient.class) != null) {
                IObject<?> member = proto.getMember(memberName);
                propertyName = member.getPath().toString();
                CustomComparator customComparatorAnnotation = memberMeta.getAnnotation(CustomComparator.class);
                if (customComparatorAnnotation != null) {
                    try {
                        propertyComparator = customComparatorAnnotation.clazz().newInstance();
                    } catch (InstantiationException e) {
                        // TODO do something (log maybe) (ask Vlad how to access the logger)
                    } catch (IllegalAccessException e) {
                        // TODO do something (log maybe) (ask Vlad how to access the logger)
                    }
                }
                temp.put(propertyName, propertyComparator);
            }
        }
        transientProperties = Collections.unmodifiableMap(temp);
    }

    public Comparator<? super X> getComparator(List<Sort> sortCriteria) {
        return new CombinedComparator(sortCriteria);
    }

    /**
     * Filter the given sorting criteria: remove all the criteria for transient fields and return them.
     * 
     * @param sortingCriteria
     *            list of criteria that is to be filtered
     * @return sorting criteria for transient members
     */
    public List<Sort> extractSortCriteriaForTransientProperties(List<Sort> sortingCriteria) {
        List<Sort> extractedSorts = new LinkedList<Sort>();

        Iterator<Sort> i = sortingCriteria.iterator();
        while (i.hasNext()) {
            Sort s = i.next();
            if (getTransientProperties().containsKey(s.getPropertyName())) {
                i.remove();
                extractedSorts.add(s);
            }
        }
        return extractedSorts;
    }

    @SuppressWarnings("rawtypes")
    public void sort(List<X> unsortedList, List<Sort> sortCriteria) {
        List<Sort> relevantSortCriteria = new LinkedList<Sort>();
        List<Comparator> comparators = new LinkedList<Comparator>();

        for (Sort sortCriterion : sortCriteria) {
            Comparator comparator = getTransientProperties().get(sortCriterion.getPropertyName());
            if (comparator != null) {
                relevantSortCriteria.add(sortCriterion);
                comparators.add(comparator);
            }
        }

        Collections.sort(unsortedList, getComparator(relevantSortCriteria));
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Comparator> getTransientProperties() {
        return transientProperties;
    }

    private class CombinedComparator implements Comparator<X> {
        @SuppressWarnings("rawtypes")
        final List<Comparator> comps;

        final List<Sort> sortCriteria;

        @SuppressWarnings("rawtypes")
        public CombinedComparator(List<Sort> relevantSortCriteria) {
            comps = new LinkedList<Comparator>();
            sortCriteria = relevantSortCriteria;

            for (Sort sortCriterion : sortCriteria) {
                final Comparator cmp = getTransientProperties().get(sortCriterion.getPropertyName());
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
        public int compare(X paramT1, X paramT2) {
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
