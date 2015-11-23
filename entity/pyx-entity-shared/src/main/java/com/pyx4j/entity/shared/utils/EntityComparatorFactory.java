/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-11-05
 * @author vlads
 */
package com.pyx4j.entity.shared.utils;

import java.util.Comparator;

import com.pyx4j.commons.CompareHelper;
import com.pyx4j.entity.annotations.BusinessEqualValue;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.shared.utils.EntityGraph.EntityGraphEqualOptions;

public class EntityComparatorFactory {

    public static <E extends IEntity> Comparator<E> createStringViewComparator() {
        return new Comparator<E>() {
            @Override
            public int compare(E paramT1, E paramT2) {
                return paramT1.getStringView().compareTo(paramT2.getStringView());
            }
        };
    }

    public static <E extends IEntity> Comparator<E> createPrimaryKeyComparator() {
        return new Comparator<E>() {
            @Override
            public int compare(E paramT1, E paramT2) {
                return CompareHelper.compareTo(paramT1.getPrimaryKey(), paramT2.getPrimaryKey());
            }
        };
    }

    /**
     * @see BusinessEqualValue
     */
    public static <E extends IEntity> Comparator<E> createBusinessEqualComparator() {
        return new Comparator<E>() {
            @Override
            public int compare(E paramT1, E paramT2) {
                if (paramT1.businessEquals(paramT2)) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };
    }

    public static <E extends IEntity> Comparator<E> createMemberComparator(IObject<?>... protoValues) {
        return createMemberComparator(Path.asPath(protoValues).toArray(new Path[protoValues.length]));
    }

    public static <E extends IEntity> Comparator<E> createMemberComparator(final Path... paths) {
        return new Comparator<E>() {
            @Override
            public int compare(E paramT1, E paramT2) {
                for (Path path : paths) {
                    int cmp = getValue(path, paramT1).compareTo(getValue(path, paramT2));
                    if (cmp != 0) {
                        return cmp;
                    }
                }
                return 0;
            }

            String getValue(Path path, E entity) {
                IObject<?> valueMember = entity.getMember(path);
                if (valueMember instanceof IEntity) {
                    return valueMember.getStringView();
                } else {
                    return valueMember.getStringView();
                }
            }
        };
    }

    /**
     * Creates comparator to check if two entities are equals
     *
     * @param options
     * @return 0 if entity values are equals; -1 otherwise.
     */
    public static <E extends IEntity> Comparator<E> createFullEntityEqualsComparator(final EntityGraphEqualOptions options) {
        return new Comparator<E>() {
            @Override
            public int compare(E paramT1, E paramT2) {
                if (EntityGraph.fullyEqual(paramT1, paramT2, options)) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };
    }

}
