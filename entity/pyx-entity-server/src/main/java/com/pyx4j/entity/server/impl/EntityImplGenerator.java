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

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.impl.SharedEntityHandler;

public class EntityImplGenerator {

    private static final Logger log = LoggerFactory.getLogger(EntityImplGenerator.class);

    public static final String MARKER_RESOURCE_NAME = "META-INF/MANIFEST.MF";

    private static EntityImplGenerator instance;

    private final ClassPool pool;

    private EntityImplGenerator() {
        pool = ClassPool.getDefault();
        appendGaeClassPath();
    }

    public static synchronized EntityImplGenerator instance() {
        if (instance == null) {
            instance = new EntityImplGenerator();
        }
        return instance;
    }

    /**
     * Required only to work on GAE
     */
    private void appendGaeClassPath() {
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
        List<String> classes = EntityClassFinder.findEntityClasses();
        if (classes.size() == 0) {
            log.warn("IEntity classes not found");
            return;
        }
        log.debug("found IEntity {} ", classes);
        for (String c : classes) {
            EntityImplGenerator.instance().generateImplementation(c);
        }
        log.info("Created {} IEntity implementations", classes.size());

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

    public <T extends IEntity<?>> Class<T> generateImplementation(Class<T> interfaceClass) {
        String interfaceName = interfaceClass.getName();
        String name = interfaceName + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
        try {
            CtClass cc = pool.makeClass(name);
            cc.setSuperclass(pool.get(SharedEntityHandler.class.getName()));
            cc.addInterface(pool.get(interfaceName));
            // Constructors
            CtConstructor defaultConstructor = new CtConstructor(null, cc);
            defaultConstructor.setBody("super(" + interfaceName + ".class);");
            cc.addConstructor(defaultConstructor);

            CtClass ctStringClass = pool.get(String.class.getName());

            CtConstructor memberConstructor = new CtConstructor(new CtClass[] { pool.get(IEntity.class.getName()), ctStringClass }, cc);
            memberConstructor.setBody("super(" + interfaceName + ".class, $1, $2);");
            cc.addConstructor(memberConstructor);

            // Abstract methods
            CtMethod lazyCreateMember = new CtMethod(pool.get(IObject.class.getName()), "lazyCreateMember", new CtClass[] { ctStringClass }, cc);
            lazyCreateMember.setBody("return " + EntityImplReflectionHelper.class.getName() + ".lazyCreateMember(this, $1);");
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

            return cc.toClass();
        } catch (CannotCompileException e) {
            log.error("Impl compile error", e);
            throw new Error("Can't create class " + name);
        } catch (NotFoundException e) {
            log.error("Impl  construction error", e);
            throw new Error("Can't create class " + name);
        }
    }
}
