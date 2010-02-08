/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 6, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;

public class EntityImplGenerator {

    private static final Logger log = LoggerFactory.getLogger(EntityImplGenerator.class);

    public static final String MARKER_RESOURCE_NAME = "META-INF/MANIFEST.MF";

    public static final String ALREADY_GENERATED_MARKER_RESOURCE_NAME = "META-INF/domain-generated.txt";

    private static EntityImplGenerator instance;

    private final ClassPool pool;

    private EntityImplGenerator(boolean webapp) {
        pool = ClassPool.getDefault();
        appendClassPath(webapp);
    }

    public static synchronized EntityImplGenerator instance() {
        if (instance == null) {
            instance = new EntityImplGenerator(true);
        }
        return instance;
    }

    public static void main(String[] args) {
        File target;
        if ((args != null) && (args.length > 0)) {
            target = new File(args[0]);
        } else {
            target = new File(new File("target"), "classes");
        }

        long start = System.currentTimeMillis();
        List<String> classes = EntityClassFinder.findEntityClasses();
        if (classes.size() == 0) {
            log.warn("IEntity classes not found");
            return;
        }
        log.debug("found IEntity {} ", classes);
        EntityImplGenerator gen = new EntityImplGenerator(false);
        for (String c : classes) {
            CtClass klass = gen.createImplementation(c);
            try {
                klass.writeFile(target.getAbsolutePath());
            } catch (Throwable e) {
                log.error("compile error", e);
                throw new Error("Can't create class " + c);
            }
        }
        File marker = new File(target, ALREADY_GENERATED_MARKER_RESOURCE_NAME);
        if (!marker.getParentFile().isDirectory()) {
            if (!marker.getParentFile().mkdirs()) {
                throw new Error("Can't create directory " + marker.getParentFile());
            }
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(marker)));
            for (String c : classes) {
                out.println(c);
            }
            out.close();
        } catch (IOException e) {
            throw new Error("Can't create marker file");
        } finally {
            if (out != null) {
                out.close();
            }
        }
        log.info("Wrote {} IEntity implementations in {} msec", classes.size(), System.currentTimeMillis() - start);

    }

    /**
     * Required only to work on GAE
     */
    private void appendClassPath(boolean webapp) {
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls;
        try {
            urls = cld.getResources(MARKER_RESOURCE_NAME);
        } catch (IOException e) {
            log.error("Unable to find jar markers", e);
            return;
        }
        while (urls.hasMoreElements()) {
            String u = urls.nextElement().toExternalForm();
            String pathname = u.substring(0, u.lastIndexOf(MARKER_RESOURCE_NAME) - 2);
            String prefix = "jar:file:";
            if (!pathname.startsWith(prefix)) {
                continue;
            }
            if (webapp && (!pathname.contains("WEB-INF"))) {
                continue;
            }
            if (pathname.contains("datanucleus") || pathname.contains("geronimo") || pathname.contains("jdo") || pathname.contains("javassist")
                    || pathname.contains("appengine") || pathname.contains("gwt-servlet")) {
                continue;
            }
            pathname = pathname.substring(prefix.length());
            try {
                log.trace("ClassPool append path {}", pathname);
                pool.appendClassPath(pathname);
            } catch (NotFoundException e) {
                log.error("Can't append path", e);
            }
        }
    }

    public static void generate() {
        if (Thread.currentThread().getContextClassLoader().getResource(ALREADY_GENERATED_MARKER_RESOURCE_NAME) != null) {
            log.debug("IEntity implementations already present");
            return;
        }

        long start = System.currentTimeMillis();
        List<String> classes = EntityClassFinder.findEntityClasses();
        if (classes.size() == 0) {
            log.warn("IEntity classes not found");
            return;
        }
        log.debug("found IEntity {} ", classes);
        for (String c : classes) {
            EntityImplGenerator.instance().generateImplementation(c);
        }
        log.info("Created {} IEntity implementations in {} msec", classes.size(), System.currentTimeMillis() - start);

    }

    @SuppressWarnings("unchecked")
    public Class<IEntity<?>> generateImplementation(String interfaceName) {
        Class<IEntity<?>> interfaceClass;
        try {
            interfaceClass = (Class<IEntity<?>>) Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new Error(interfaceName + " not available");
        }
        return generateImplementation(interfaceClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity<?>> Class<T> generateImplementation(Class<T> interfaceClass) {
        try {
            return createImplementationClass(interfaceClass).toClass();
        } catch (CannotCompileException e) {
            log.error("Impl compile error", e);
            throw new Error("Can't create class " + interfaceClass.getName());
        }
    }

    public CtClass createImplementation(String interfaceName) {
        Class<IEntity<?>> interfaceClass;
        try {
            interfaceClass = (Class<IEntity<?>>) Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new Error(interfaceName + " not available");
        }
        return createImplementationClass(interfaceClass);
    }

    public <T extends IEntity<?>> CtClass createImplementationClass(Class<T> interfaceClass) {
        String interfaceName = interfaceClass.getName();
        String name = interfaceName + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
        try {
            CtClass cc = pool.makeClass(name);
            cc.setSuperclass(pool.get(SharedEntityHandler.class.getName()));
            cc.addInterface(pool.get(interfaceName));
            // Constructors
            // N.B. transient fields are not initialized during deserialization 
            CtConstructor defaultConstructor = new CtConstructor(null, cc);
            defaultConstructor.setBody("super(" + interfaceName + ".class);");
            cc.addConstructor(defaultConstructor);

            CtClass ctStringClass = pool.get(String.class.getName());

            CtConstructor memberConstructor = new CtConstructor(new CtClass[] { pool.get(IEntity.class.getName()), ctStringClass }, cc);
            memberConstructor.setBody("super(" + interfaceName + ".class, $1, $2);");
            cc.addConstructor(memberConstructor);

            // add field with default 1L value.
            CtField field = new CtField(CtClass.longType, "serialVersionUID", cc);
            field.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
            cc.addField(field, "1L");

            // Override getObjectClass with proper value since transient value is null in super 
            CtMethod getObjectClassOverride = new CtMethod(pool.get(Class.class.getName()), "getObjectClass", null, cc);
            getObjectClassOverride.setBody("return " + interfaceName + ".class;");
            cc.addMethod(getObjectClassOverride);

            // Abstract methods
            CtMethod lazyCreateMember = new CtMethod(pool.get(IObject.class.getName()), "lazyCreateMember", new CtClass[] { ctStringClass }, cc);
            lazyCreateMember.setBody("return " + EntityImplReflectionHelper.class.getName() + ".lazyCreateMember(" + interfaceName + ".class, this, $1);");
            cc.addMethod(lazyCreateMember);

            // Members access
            for (Method method : interfaceClass.getMethods()) {
                if (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().isAssignableFrom(IEntity.class)) {
                    continue;
                }
                Class<?> type = method.getReturnType();
                if (type == Void.class) {
                    continue;
                }
                CtMethod member = new CtMethod(pool.get(type.getName()), method.getName(), null, cc);
                member.setBody("return (" + type.getName() + ")getMember(\"" + method.getName() + "\");");
                cc.addMethod(member);
            }

            return cc;
        } catch (CannotCompileException e) {
            log.error("Impl compile error", e);
            throw new Error("Can't create class " + name);
        } catch (NotFoundException e) {
            log.error("Impl construction error", e);
            throw new Error("Can't create class " + name);
        }
    }
}
