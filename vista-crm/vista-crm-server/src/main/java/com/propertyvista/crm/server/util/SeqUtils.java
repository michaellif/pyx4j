/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.util.ArrayList;
import java.util.Collection;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.Path;

public class SeqUtils {
    public interface CombiningFunction<T> {
        /** must never return <code>null</code> */
        public <X extends T> X combine(X a, X b);

        public T initialValue();
    }

    public static class Sum<T extends IEntity> implements CombiningFunction<T> {
        final Path[] doubleProperties;

        final Path[] integerProperties;

        final Class<T> entityClass;

        final T proto;

        public Sum(Class<T> entityClass, Collection<Path> properties) {
            this.entityClass = entityClass;
            this.proto = EntityFactory.getEntityPrototype(entityClass);

            ArrayList<Path> doubleProperties = new ArrayList<Path>(properties.size());
            ArrayList<Path> integerProperties = new ArrayList<Path>(properties.size());

            for (Path propertyPath : properties) {
                Class<?> klass = proto.getMember(propertyPath).getValueClass();
                if (klass.isAssignableFrom(Double.class)) {
                    doubleProperties.add(propertyPath);
                } else if (klass.isAssignableFrom(Integer.class)) {
                    integerProperties.add(propertyPath);
                } else {
                    throw new IllegalArgumentException("Path must point to property that is instance of a Number: " + propertyPath.toString());
                }
            }
            this.doubleProperties = doubleProperties.isEmpty() ? null : doubleProperties.toArray(new Path[doubleProperties.size()]);
            this.integerProperties = integerProperties.isEmpty() ? null : integerProperties.toArray(new Path[integerProperties.size()]);
        }

        /**
         * @return a = a + b (in place)
         */
        @Override
        public <X extends T> X combine(X a, X b) {
            if (doubleProperties != null) {
                for (Path property : doubleProperties) {
                    a.setValue(property, ((Double) a.getMember(property).getValue() + (Double) b.getMember(property).getValue()));
                }
            }
            if (integerProperties != null) {
                for (Path property : integerProperties) {
                    a.setValue(property, ((Integer) a.getMember(property).getValue() + (Integer) b.getMember(property).getValue()));
                }
            }
            return a;
        }

        @Override
        public T initialValue() {
            T value = EntityFactory.create(entityClass);
            if (doubleProperties != null) {
                for (Path property : doubleProperties) {
                    value.setValue(property, 0.0);
                }
            }
            if (integerProperties != null) {
                for (Path property : integerProperties) {
                    value.setValue(property, 0);
                }
            }
            return value;
        }
    }

    public static <X> X foldl(CombiningFunction<X> f, X initalValue, Iterable<X> sequence) {
        X result = initalValue;
        for (X value : sequence) {
            result = f.combine(result, value);
        }
        return result;
    }

    public static <X> X foldl(CombiningFunction<X> f, Iterable<X> sequence) {
        return foldl(f, f.initialValue(), sequence);
    }

    // TODO add map

    // TODO add filter
}
