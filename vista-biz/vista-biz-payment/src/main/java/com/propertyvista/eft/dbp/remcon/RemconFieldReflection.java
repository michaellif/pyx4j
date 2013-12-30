/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.dbp.remcon;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemconFieldReflection {

    private static final Logger log = LoggerFactory.getLogger(RemconFieldReflection.class);

    private static final Map<Class<? extends RemconRecord>, List<Field>> registry = new HashMap<Class<? extends RemconRecord>, List<Field>>();

    public static List<Field> getFileds(Class<? extends RemconRecord> klass) {
        if (registry.isEmpty()) {
            RemconRecordFactory.instance();
        }
        List<Field> list = registry.get(klass);
        if (list == null) {
            throw new Error("RemconRecord class '" + klass.getName() + "' not registered");
        }
        return list;
    }

    // javassist used to ensure the declaration order of fields
    static void initialize(Collection<Class<? extends RemconRecord>> klassCollection) {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(RemconRecord.class));
        try {
            for (Class<? extends RemconRecord> klass : klassCollection) {
                log.debug("initialize filed of {}", klass.getName());
                CtClass ctClass = pool.get(klass.getName());

                List<Field> fieldList = new ArrayList<Field>();

                for (CtField ctField : ctClass.getDeclaredFields()) {
                    Field field = klass.getField(ctField.getName());
                    if (field.getAnnotation(RemconField.class) != null) {
                        fieldList.add(field);
                    }
                }

                registry.put(klass, fieldList);

            }
        } catch (NotFoundException e) {
            throw new Error(e);
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }

    }
}
