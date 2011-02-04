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
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPath;
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
import com.pyx4j.entity.shared.meta.EntityMeta;

public class EntityImplGenerator {

    private static final Logger log = LoggerFactory.getLogger(EntityImplGenerator.class);

    public static final String MARKER_RESOURCE_NAME = "META-INF/MANIFEST.MF";

    public static final String ALREADY_GENERATED_MARKER_RESOURCE_NAME = "META-INF/domain-generated.txt";

    private static EntityImplGenerator instance;

    private static boolean implementationsCreated = false;

    private ClassPool pool;

    private final List<ClassPath> pathToClose = new Vector<ClassPath>();

    private final boolean webapp;

    private CtClass ctClassObject;

    private CtClass ctClassIEntity;

    private CtClass ctClassIObject;

    private EntityImplGenerator(boolean webapp) {
        this.webapp = webapp;
    }

    private synchronized void initClassPool() throws NotFoundException {
        if (pool == null) {
            pool = ClassPool.getDefault();
            appendClassPath(webapp);
            ctClassIEntity = pool.get(IEntity.class.getName());
            ctClassIObject = pool.get(IObject.class.getName());
            ctClassObject = pool.get(Object.class.getName());
        }
    }

    public static synchronized EntityImplGenerator instance() {
        if (instance == null) {
            return createInstance(true);
        } else {
            return instance;
        }
    }

    public static void release() {
        if (instance != null) {
            for (ClassPath classPath : instance.pathToClose) {
                classPath.close();
            }
            instance.pool = null;
            instance = null;
        }
    }

    public static EntityImplGenerator createInstance(boolean webapp) {
        if (instance == null) {
            instance = new EntityImplGenerator(webapp);
        } else if (instance.webapp != webapp) {
            log.error("chaning weapp classpath configuration at runtime");
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
        int jarCount = 0;
        while (urls.hasMoreElements()) {
            String u = urls.nextElement().toExternalForm();
            String pathname = u.substring(0, u.lastIndexOf(MARKER_RESOURCE_NAME) - 2);
            //log.debug("path {}", pathname);
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
                pathToClose.add(pool.appendClassPath(pathname));
                jarCount++;
            } catch (NotFoundException e) {
                log.error("Can't append path", e);
            }
        }
        if (jarCount == 0) {
            log.warn("No jars found in ContextClassLoader webapp={}", webapp);
            // Allow to work as eclipse plugin.
            pathToClose.add(pool.appendClassPath(new ClassClassPath(IEntity.class)));
        }
    }

    public static void generate(boolean webapp) {
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
        EntityImplGenerator gen = createInstance(webapp);
        for (String c : classes) {
            gen.generateImplementation(c);
        }
        log.info("Created {} IEntity implementations in {} msec", classes.size(), System.currentTimeMillis() - start);

    }

    public static synchronized void generateOnce(boolean webapp) {
        if (!implementationsCreated) {
            generate(webapp);
            implementationsCreated = true;
        }
    }

    @SuppressWarnings("unchecked")
    public Class<IEntity> generateImplementation(String interfaceName) {
        Class<IEntity> interfaceClass;
        try {
            interfaceClass = (Class<IEntity>) Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new Error(interfaceName + " not available");
        }
        return generateImplementation(interfaceClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntity> Class<T> generateImplementation(Class<T> interfaceClass) {
        try {
            return createImplementationClass(interfaceClass).toClass();
        } catch (CannotCompileException e) {
            log.error("Impl compile error", e);
            throw new Error("Can't create class " + interfaceClass.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public CtClass createImplementation(String interfaceName) {
        Class<IEntity> interfaceClass;
        try {
            interfaceClass = (Class<IEntity>) Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new Error(interfaceName + " not available");
        }
        return createImplementationClass(interfaceClass);
    }

    public <T extends IEntity> CtClass createImplementationClass(Class<T> interfaceClass) {
        //        if (interfaceClass.getAnnotation(AbstractEntity.class) != null) {
        //            throw new Error(interfaceClass.getName() + " is AbstractEntity");
        //        }

        String interfaceName = interfaceClass.getName();
        String name = interfaceName + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
        try {
            initClassPool();
            CtClass interfaceCtClass = pool.get(interfaceName);
            CtClass implClass = pool.makeClass(name);
            implClass.setSuperclass(pool.get(SharedEntityHandler.class.getName()));
            implClass.addInterface(interfaceCtClass);
            // Constructors
            // N.B. transient fields are not initialized during deserialization 
            CtConstructor defaultConstructor = new CtConstructor(null, implClass);
            defaultConstructor.setBody("super(" + interfaceName + ".class, null, null);");
            implClass.addConstructor(defaultConstructor);

            CtClass ctStringClass = pool.get(String.class.getName());

            CtConstructor memberConstructor = new CtConstructor(new CtClass[] { pool.get(IObject.class.getName()), ctStringClass }, implClass);
            memberConstructor.setBody("super(" + interfaceName + ".class, $1, $2);");
            implClass.addConstructor(memberConstructor);

            // add field with default 1L value.
            CtField field = new CtField(CtClass.longType, "serialVersionUID", implClass);
            field.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
            implClass.addField(field, "1L");

            // Override getObjectClass with proper value since transient value is null in super 
            CtMethod getObjectClassOverride = new CtMethod(pool.get(Class.class.getName()), "getObjectClass", null, implClass);
            getObjectClassOverride.setBody("return " + interfaceName + ".class;");
            implClass.addMethod(getObjectClassOverride);

            // Abstract methods
            CtMethod lazyCreateMember = new CtMethod(pool.get(IObject.class.getName()), "lazyCreateMember", new CtClass[] { ctStringClass }, implClass);
            lazyCreateMember.setBody("return " + EntityImplReflectionHelper.class.getName() + ".lazyCreateMember(" + interfaceName + ".class, this, $1);");
            implClass.addMethod(lazyCreateMember);

            List<CtMethod> allMethodsSortedByDeclaration = new Vector<CtMethod>();
            for (CtMethod method : interfaceCtClass.getDeclaredMethods()) {
                if (method.getDeclaringClass().equals(ctClassObject) || (method.getDeclaringClass().equals(ctClassIEntity))
                        || (method.getDeclaringClass().equals(ctClassIObject))) {
                    continue;
                }
                allMethodsSortedByDeclaration.add(method);
            }
            for (CtMethod method : interfaceCtClass.getMethods()) {
                if (method.getDeclaringClass().equals(ctClassObject) || (method.getDeclaringClass().equals(ctClassIEntity))
                        || (method.getDeclaringClass().equals(ctClassIObject))) {
                    continue;
                }
                if (!allMethodsSortedByDeclaration.contains(method)) {
                    allMethodsSortedByDeclaration.add(method);
                }
            }

            StringBuilder membersNamesStringArray = new StringBuilder();
            // Members access, Use CtClass to get the list of Methods ordered by declaration order.
            for (CtMethod method : allMethodsSortedByDeclaration) {
                CtClass type = method.getReturnType();
                if (type == CtClass.voidType) {
                    throw new Error("Can't create void memeber " + method.getName() + " for class " + name);
                }
                //System.out.println("Creating " + method.getName() + " of " + method.getDeclaringClass().getName());
                CtMethod member = new CtMethod(type, method.getName(), null, implClass);
                member.setBody("return (" + type.getName() + ")getMember(\"" + method.getName() + "\");");
                implClass.addMethod(member);
                if (membersNamesStringArray.length() > 0) {
                    membersNamesStringArray.append(", ");
                }
                membersNamesStringArray.append("\"").append(method.getName()).append("\"");
            }

            CtMethod getMemebersMethod = new CtMethod(pool.get(String[].class.getName()), "getMemebers", null, implClass);
            if (membersNamesStringArray.length() == 0) {
                getMemebersMethod.setBody("{ return new String[0]; }");
            } else {
                getMemebersMethod.setBody("{ return new String[] {" + membersNamesStringArray + "}; }");
            }
            implClass.addMethod(getMemebersMethod);

            //Static for optimization
            CtField entityMetaField = new CtField(pool.get(EntityMeta.class.getName()), "entityMeta", implClass);
            entityMetaField.setModifiers(Modifier.PRIVATE | Modifier.STATIC);
            implClass.addField(entityMetaField);

            CtMethod getEntityMetaMethod = new CtMethod(pool.get(EntityMeta.class.getName()), "getEntityMeta", null, implClass);
            getEntityMetaMethod.setBody("{ if (entityMeta == null) { entityMeta = super.getEntityMeta(); } return entityMeta; }");
            implClass.addMethod(getEntityMetaMethod);

            return implClass;
        } catch (CannotCompileException e) {
            log.error("Impl " + interfaceName + " compile error", e);
            throw new Error("Can't create class " + name);
        } catch (NotFoundException e) {
            log.error("Impl " + interfaceName + " construction error", e);
            throw new Error("Can't create class " + name);
        }
    }
}
