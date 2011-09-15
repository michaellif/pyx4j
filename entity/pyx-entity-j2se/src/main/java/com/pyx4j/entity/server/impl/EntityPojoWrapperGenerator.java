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
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pyx4j.commons.EnglishGrammar;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.server.pojo.IPojo;
import com.pyx4j.entity.server.pojo.IPojoImpl;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.xml.LogicalDateXmlAdapter;

public class EntityPojoWrapperGenerator {

    private static EntityPojoWrapperGenerator instance;

    private final ClassLoader classLoader;

    private final ClassPool pool;

    private EntityPojoWrapperGenerator() {
        classLoader = EntityImplGenerator.instance().getContextClassLoader();
        try {
            pool = EntityImplGenerator.instance().getClassPool();
        } catch (NotFoundException e) {
            throw new Error("Can't ininitate ClassPool", e);
        }
    }

    public static synchronized EntityPojoWrapperGenerator instance() {
        if (instance == null) {
            return instance = new EntityPojoWrapperGenerator();
        } else {
            return instance;
        }
    }

    public static <T extends IEntity> Class<IPojo<T>> getPojoClass(Class<T> clazz) {
        return getPojoClass(EntityFactory.getEntityMeta(clazz));
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEntity> Class<IPojo<T>> getPojoClass(EntityMeta entityMeta) {
        try {
            return (Class<IPojo<T>>) Class.forName(instance().getPojoCtClass(entityMeta).getName(), true, instance().classLoader);
        } catch (ClassNotFoundException e) {
            throw new Error("Can't create POJO class for " + entityMeta.getEntityClass(), e);
        }
    }

    private static String getBeanName(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
    }

    private static String getSingular(String fieldName) {
        if (fieldName.endsWith("s")) {
            if (fieldName.endsWith("ies")) {
                return fieldName.substring(0, fieldName.length() - 3) + "y";
            } else {
                return fieldName.substring(0, fieldName.length() - 1);
            }
        } else {
            return fieldName;
        }
    }

    public String getXMLName(Class<?> memberClass) {
        String name = EnglishGrammar.deCapitalize(memberClass.getSimpleName());
        if (name.endsWith("IO")) {
            name = name.substring(0, name.length() - 2);
        }
        return name;
    }

    @SuppressWarnings("unchecked")
    private <T extends IEntity> CtClass getPojoCtClass(EntityMeta entityMeta) {
        String entityClassName = entityMeta.getEntityClass().getName();
        String name = entityClassName + "Pojo";
        try {
            return pool.get(name);
        } catch (NotFoundException e) {
            // If not found then create one
        }
        try {
            CtClass implClass = pool.makeClass(name);
            implClass.getClassFile().setVersionToJava5();
            XmlRootElement xmlRootElement = entityMeta.getAnnotation(XmlRootElement.class);
            if (xmlRootElement != null) {
                addAnnotationValue(implClass, XmlRootElement.class, "name", xmlRootElement.name());
            }
            //addAnnotationValue(implClass, XmlType.class, "name", entityMeta.getEntityClass().getSimpleName());
            addXmlTypeAnnotationValue(implClass, "name", getXMLName(entityMeta.getEntityClass()), entityMeta.getMemberNames());

            implClass.setSuperclass(pool.get(IPojoImpl.class.getName()));

            CtConstructor defaultConstructor = new CtConstructor(null, implClass);
            defaultConstructor.setBody("super(" + entityClassName + ".class);");
            implClass.addConstructor(defaultConstructor);

            CtConstructor fromEntityConstructor = new CtConstructor(new CtClass[] { pool.get(entityClassName) }, implClass);
            fromEntityConstructor.setBody("super($1);");
            implClass.addConstructor(fromEntityConstructor);

            // add field with default 1L value.
            CtField field = new CtField(CtClass.longType, "serialVersionUID", implClass);
            field.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
            implClass.addField(field, "1L");

            for (String memberName : entityMeta.getMemberNames()) {
                MemberMeta memberMeta = entityMeta.getMemberMeta(memberName);
                CtClass ctValueClass;
                switch (memberMeta.getObjectClassType()) {
                case Entity:
                    ctValueClass = getPojoCtClass(EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getValueClass()));
                    break;
                case EntityList:
                    ctValueClass = getPojoCtClass(EntityFactory.getEntityMeta((Class<IEntity>) memberMeta.getValueClass()));
                    ctValueClass = pool.get(ctValueClass.getName() + "[]");
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
                memberGet.setBody(createGetBody(ctValueClass, memberMeta, entityClassName));
                implClass.addMethod(memberGet);

                if (LogicalDate.class.getName().equals(ctValueClass.getName())) {
                    addAnnotationValue(memberGet, XmlSchemaType.class, "name", "date");
                    addAnnotationValue(memberGet, XmlJavaTypeAdapter.class, "value", LogicalDateXmlAdapter.class);
                }

                if (memberMeta.getObjectClassType() == ObjectClassType.EntityList) {
                    addAnnotationValue(memberGet, XmlElement.class, "name", getXMLName(memberMeta.getValueClass()));
                    addAnnotationValue(memberGet, XmlElementWrapper.class, "name", memberName);
                }

                CtMethod memberSet = new CtMethod(CtClass.voidType, "set" + beanMemberName, new CtClass[] { ctValueClass }, implClass);
                memberSet.setBody(createSetBody(memberMeta, entityClassName));
                implClass.addMethod(memberSet);
            }

            implClass.toClass(classLoader, null);

            if (ServerSideConfiguration.isStartedUnderEclipse()) {
                try {
                    implClass.writeFile("target/entity-pojo-gen-classes");
                } catch (IOException e) {
                }
            }

            return implClass;
        } catch (CannotCompileException e) {
            throw new Error("Can't create class " + name, e);
        } catch (NotFoundException e) {
            throw new Error("Can't create class " + name, e);
        }
    }

    private String createGetBody(CtClass ctValueClass, MemberMeta memberMeta, String entityClassName) {
        switch (memberMeta.getObjectClassType()) {
        case Primitive:
            // Need cast to return type to avoid class loading problems
            return "return (" + ctValueClass.getName() + ") ((" + entityClassName + ")super.entity)." + memberMeta.getFieldName() + "().getValue();";
        case Entity: {
            StringBuilder b = new StringBuilder("{");
            b.append("return (" + ctValueClass.getName() + ")");
            b.append(ServerEntityFactory.class.getName()).append(".getPojo(");
            b.append("((" + entityClassName + ")super.entity)." + memberMeta.getFieldName() + "()");
            b.append(");}");
            return b.toString();
        }
        case EntityList: {
            StringBuilder b = new StringBuilder("{");
            b.append("return (" + ctValueClass.getName() + ") toArray(");
            b.append(" new ").append(ctValueClass.getName().replace("[]", "[0]")).append(", ");
            b.append("((" + entityClassName + ")super.entity)." + memberMeta.getFieldName() + "()");
            b.append(");}");
            return b.toString();
        }
        default:
            //TODO
            return " throw new Error(\"Getter for " + memberMeta.getObjectClassType() + " Not implmented yet\");";
        }
    }

    private String createSetBody(MemberMeta memberMeta, String entityClassName) {
        switch (memberMeta.getObjectClassType()) {
        case Primitive:
            return "((" + entityClassName + ") super.entity)." + memberMeta.getFieldName() + "().setValue($1);";
        case EntityList: {
            StringBuilder b = new StringBuilder("{");
            b.append("fromArray($1, ");
            b.append("((" + entityClassName + ")super.entity)." + memberMeta.getFieldName() + "()");
            b.append(");}");
            return b.toString();
        }
        default:
            return " throw new Error(\"Seter for " + memberMeta.getObjectClassType() + " Not implmented yet\");";
        }
    }

    private void addAnnotationValue(CtClass implClass, Class<?> annotationClass, String name, String value) throws NotFoundException {
        ConstPool constPool = implClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = (AnnotationsAttribute) implClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (attr == null) {
            attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        }
        Annotation annotation = new Annotation(constPool, pool.get(annotationClass.getName()));
        annotation.addMemberValue(name, new StringMemberValue(value, constPool));

        // Set proper default values
        if (annotationClass == XmlType.class) {
            ArrayMemberValue a = new ArrayMemberValue(constPool);
            MemberValue[] m = new MemberValue[] { new StringMemberValue("", constPool) };
            a.setValue(m);
            annotation.addMemberValue("propOrder", a);

            annotation.addMemberValue("namespace", new StringMemberValue("##default", constPool));
            annotation.addMemberValue("factoryClass", new ClassMemberValue(XmlType.class.getName() + "$DEFAULT", constPool));
        }
        attr.addAnnotation(annotation);
        implClass.getClassFile().addAttribute(attr);
    }

    private void addXmlTypeAnnotationValue(CtClass implClass, String name, String value, List<String> propOrder) throws NotFoundException {
        ConstPool constPool = implClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = (AnnotationsAttribute) implClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        if (attr == null) {
            attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        }
        Annotation annotation = new Annotation(constPool, pool.get(XmlType.class.getName()));
        annotation.addMemberValue(name, new StringMemberValue(value, constPool));

        ArrayMemberValue a = new ArrayMemberValue(constPool);
        List<MemberValue> m = new Vector<MemberValue>();
        for (String memberName : propOrder) {
            m.add(new StringMemberValue(memberName, constPool));
        }
        a.setValue(m.toArray(new MemberValue[0]));
        annotation.addMemberValue("propOrder", a);

        annotation.addMemberValue("namespace", new StringMemberValue("##default", constPool));
        annotation.addMemberValue("factoryClass", new ClassMemberValue(XmlType.class.getName() + "$DEFAULT", constPool));

        attr.addAnnotation(annotation);
        implClass.getClassFile().addAttribute(attr);
    }

    private void addAnnotationValue(CtMethod member, Class<?> annotationClass, String name, String value) throws NotFoundException {
        CtClass implClass = member.getDeclaringClass();
        ConstPool constPool = implClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = (AnnotationsAttribute) member.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        if (attr == null) {
            attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        }
        Annotation annotation = new Annotation(constPool, pool.get(annotationClass.getName()));
        annotation.addMemberValue(name, new StringMemberValue(value, constPool));

        if (annotationClass == XmlSchemaType.class) {
            annotation.addMemberValue("namespace", new StringMemberValue("http://www.w3.org/2001/XMLSchema", constPool));
            annotation.addMemberValue("type", new ClassMemberValue(XmlSchemaType.class.getName() + "$DEFAULT", constPool));
        } else if (annotationClass == XmlElement.class) {
            annotation.addMemberValue("namespace", new StringMemberValue("##default", constPool));
            annotation.addMemberValue("defaultValue", new StringMemberValue("\u0000", constPool));
            annotation.addMemberValue("type", new ClassMemberValue(XmlElement.class.getName() + "$DEFAULT", constPool));
        }

        attr.addAnnotation(annotation);
        member.getMethodInfo().addAttribute(attr);
    }

    private void addAnnotationValue(CtMethod member, Class<?> annotationClass, String name, Class<?> value) throws NotFoundException {
        CtClass implClass = member.getDeclaringClass();
        ConstPool constPool = implClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = (AnnotationsAttribute) member.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        if (attr == null) {
            attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        }
        Annotation annotation = new Annotation(constPool, pool.get(annotationClass.getName()));
        annotation.addMemberValue(name, new ClassMemberValue(value.getName(), constPool));

        if (annotationClass == XmlJavaTypeAdapter.class) {
            annotation.addMemberValue("type", new ClassMemberValue(XmlJavaTypeAdapter.class.getName() + "$DEFAULT", constPool));
        }

        attr.addAnnotation(annotation);
        member.getMethodInfo().addAttribute(attr);
    }

}
