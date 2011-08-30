/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Aug 30, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import java.io.IOException;
import java.util.Locale;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import javax.xml.bind.annotation.XmlRootElement;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class EntityPojoWrapperGenerator {

    private static EntityPojoWrapperGenerator instance;

    private static boolean implementationsCreated = false;

    private final ClassLoader classLoader;

    private final ClassPool pool;

    private EntityPojoWrapperGenerator() {
        classLoader = Thread.currentThread().getContextClassLoader();
        pool = ClassPool.getDefault();
    }

    public static synchronized EntityPojoWrapperGenerator instance() {
        if (instance == null) {
            return instance = new EntityPojoWrapperGenerator();
        } else {
            return instance;
        }
    }

    public static <T extends IEntity> Class<?> getPojoClass(Class<T> clazz) {
        return instance().createPojo(EntityFactory.getEntityMeta(clazz));
    }

    public Class<?> createPojo(EntityMeta entityMeta) {
        try {
            return getPojoCtClass(entityMeta).toClass(classLoader, null);
        } catch (CannotCompileException e) {
            throw new Error("Can't create POJO class for " + entityMeta.getEntityClass(), e);
        }
    }

    public static String getBeanName(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
    }

    @SuppressWarnings("unchecked")
    private <T extends IEntity> CtClass getPojoCtClass(EntityMeta entityMeta) {
        String name = entityMeta.getEntityClass().getName() + "Pojo";
        try {
            return pool.get(name);
        } catch (NotFoundException e) {
            // If not found then create one
        }
        try {
            CtClass implClass = pool.makeClass(name);
            implClass.getClassFile().setVersionToJava5();
            addAnnotationValue(implClass, XmlRootElement.class, "name", entityMeta.getEntityClass().getSimpleName());

            for (String memberName : entityMeta.getMemberNames()) {
                MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
                CtClass ctValueClass;
                switch (memberMeta.getObjectClassType()) {
                case Entity:
                    ctValueClass = getPojoCtClass(EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getValueClass()));
                    break;
                case EntityList:
                    ctValueClass = getPojoCtClass(EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getValueClass()));
                    break;
                case Primitive:
                    ctValueClass = pool.get(memberMeta.getValueClass().getName());
                    break;
                default:
                    //TODO
                    continue;
                }

                String beanMemberName = getBeanName(memberName);

                CtMethod memberGet = new CtMethod(ctValueClass, "get" + beanMemberName, null, implClass);
                memberGet.setBody("return null;");
                implClass.addMethod(memberGet);

                CtMethod memberSet = new CtMethod(CtClass.voidType, "set" + beanMemberName, new CtClass[] { ctValueClass }, implClass);
                memberSet.setBody(";");
                implClass.addMethod(memberSet);
            }

            try {
                implClass.writeFile("gen");
            } catch (IOException e) {
            }

            return implClass;
        } catch (CannotCompileException e) {
            throw new Error("Can't create class " + name, e);
        } catch (NotFoundException e) {
            throw new Error("Can't create class " + name, e);
        }
    }

    private void addAnnotationValue(CtClass implClass, Class<?> annotationClass, String name, String value) throws NotFoundException {
        ConstPool constPool = implClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation(constPool, pool.get(annotationClass.getName()));
        annotation.addMemberValue(name, new StringMemberValue(value, constPool));
        attr.setAnnotation(annotation);
        implClass.getClassFile().addAttribute(attr);
    }
}
