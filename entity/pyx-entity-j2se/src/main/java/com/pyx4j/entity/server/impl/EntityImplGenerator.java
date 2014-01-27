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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.config.server.ClassFinder;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.impl.SharedEntityHandler;
import com.pyx4j.entity.core.meta.EntityMeta;

public class EntityImplGenerator {

    private static final Logger log = LoggerFactory.getLogger(EntityImplGenerator.class);

    public static final String MARKER_RESOURCE_NAME = "META-INF/MANIFEST.MF";

    public static final String ALREADY_GENERATED_MARKER_RESOURCE_NAME = "META-INF/domain-generated.txt";

    static final Map<String, int[]> javaVersion = new HashMap<String, int[]>();

    static {
        javaVersion.put("1.0", new int[] { 45, 3 });
        javaVersion.put("1.1", new int[] { 45, 3 });
        javaVersion.put("1.2", new int[] { 46, 3 });
        javaVersion.put("1.3", new int[] { 47, 0 });
        javaVersion.put("1.4", new int[] { 48, 0 });
        javaVersion.put("1.5", new int[] { 49, 0 });
        javaVersion.put("1.6", new int[] { 50, 0 });
        javaVersion.put("6", new int[] { 50, 0 });
        javaVersion.put("1.7", new int[] { 51, 0 });
        javaVersion.put("7", new int[] { 51, 0 });
    }

    private static EntityImplGenerator instance;

    private static boolean implementationsCreated = false;

    private final ClassLoader classLoader;

    private ClassPool pool;

    private final List<ClassPath> pathToClose = new Vector<ClassPath>();

    private final boolean webapp;

    private CtClass ctClassObject;

    private CtClass ctClassIEntity;

    private CtClass ctClassIObject;

    private EntityImplGenerator(boolean webapp) {
        this.webapp = webapp;
        classLoader = Thread.currentThread().getContextClassLoader();
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

    public ClassLoader getContextClassLoader() {
        return classLoader;
    }

    public static synchronized EntityImplGenerator instance() {
        if (instance == null) {
            return createInstance(true);
        } else {
            return instance;
        }
    }

    ClassPool getClassPool() throws NotFoundException {
        initClassPool();
        return pool;
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
            log.error("changing webapp classpath configuration at runtime");
        }
        return instance;
    }

    public static void main(String[] args) {
        File target;
        String classVersion = "1.7";
        if ((args != null) && (args.length > 0)) {
            target = new File(args[0]);
            if (args.length > 1) {
                classVersion = args[1];
            }
        } else {
            target = new File(new File("target"), "classes");
        }

        long start = System.currentTimeMillis();
        List<String> classes = EntityClassFinder.getEntityClassesNames();
        if (classes.size() == 0) {
            log.warn("IEntity classes not found");
            return;
        }
        log.debug("found IEntity {} ", classes);
        EntityImplGenerator gen = new EntityImplGenerator(false);
        for (String c : classes) {
            CtClass klass = gen.createImplementation(c);

            if (classVersion != null) {
                int[] majorMinor = javaVersion.get(classVersion);
                if (majorMinor == null) {
                    throw new RuntimeException("Unknown classVersion " + classVersion);
                }
                ClassFile cf = klass.getClassFile();
                cf.setMajorVersion(majorMinor[0]);
                cf.setMinorVersion(majorMinor[1]);
            }

            try {
                @SuppressWarnings("unchecked")
                Class<? extends IEntity> entityClass = klass.toClass(gen.getContextClassLoader(), null);
                EntityImplValidator.validate(entityClass);
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
        Enumeration<URL> urls;
        try {
            urls = classLoader.getResources(MARKER_RESOURCE_NAME);
        } catch (IOException e) {
            log.error("Unable to find jar markers", e);
            ClassFinder.debugClassLoader("Resources ", classLoader);
            return;
        }
        int jarCount = 0;
        while (urls.hasMoreElements()) {
            String u;
            try {
                u = URLDecoder.decode(urls.nextElement().toExternalForm(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("invalid encoding", e);
                continue;
            }
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
                log.debug("Can't append path {}", pathname, e);
            }
        }
        if (jarCount == 0) {
            ClassFinder.debugClassLoader("Context ", classLoader);
            log.debug("No jars found in ContextClassLoader webapp={}", webapp);
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
        List<String> classes = EntityClassFinder.getEntityClassesNames();
        if (classes.size() == 0) {
            log.warn("IEntity classes not found");
            return;
        }
        log.debug("found IEntity {} ", classes);
        EntityImplGenerator gen = createInstance(webapp);
        boolean first = true;
        for (String c : classes) {
            Class<?> cl = gen.generateImplementation(c);
            if (first) {
                first = false;
                ClassFinder.debugClassLoader("IEntity Impl", cl);
            }
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
    private Class<IEntity> generateImplementation(String interfaceName) {
        Class<IEntity> interfaceClass;
        try {
            interfaceClass = (Class<IEntity>) Class.forName(interfaceName, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new Error(interfaceName + " not available");
        }
        return generateImplementation(interfaceClass);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends IEntity> Class<T> generateImplementation(Class<T> interfaceClass) {
        // synchronization
        try {
            return (Class<T>) Class.forName(implClassName(interfaceClass), true, getContextClassLoader());
        } catch (ClassNotFoundException continueToCreationOfClass) {
        }

        try {
            return createImplementationClass(interfaceClass).toClass(getContextClassLoader(), null);
        } catch (CannotCompileException e) {
            log.error("Impl compile error", e);
            throw new Error("Can't create class " + interfaceClass.getName());
        }
    }

    public static String implClassName(Class<?> interfaceClass) {
        return interfaceClass.getPackage().getName() + "." + implClassName(GWTJava5Helper.getSimpleName(interfaceClass));
    }

    private static String implClassName(String interfaceSimpleClassName) {
        return interfaceSimpleClassName.replace('$', '_') + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
    }

    @SuppressWarnings("unchecked")
    private CtClass createImplementation(String interfaceName) {
        Class<IEntity> interfaceClass;
        try {
            interfaceClass = (Class<IEntity>) Class.forName(interfaceName, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new Error(interfaceName + " not available");
        }
        return createImplementationClass(interfaceClass);
    }

    private <T extends IEntity> CtClass createImplementationClass(Class<T> interfaceClass) {
        //        if (interfaceClass.getAnnotation(AbstractEntity.class) != null) {
        //            throw new Error(interfaceClass.getName() + " is AbstractEntity");
        //        }

        String interfaceName = interfaceClass.getName();
        String name = implClassName(interfaceClass);
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

            // Override getObjectClass with proper value since transient value is null in super during java deserialization
            CtMethod getObjectClassOverride = new CtMethod(pool.get(Class.class.getName()), "getObjectClass", null, implClass);
            getObjectClassOverride.setBody("return " + interfaceName + ".class;");
            implClass.addMethod(getObjectClassOverride);

            // Abstract methods
            CtMethod lazyCreateMember = new CtMethod(pool.get(IObject.class.getName()), "lazyCreateMember", new CtClass[] { ctStringClass }, implClass);
            lazyCreateMember.setBody("return " + EntityImplReflectionHelper.class.getName() + ".lazyCreateMember(" + interfaceName + ".class, this, $1);");
            implClass.addMethod(lazyCreateMember);

            List<CtMethod> allMethodsSortedByDeclaration = getAllMethodsSortedByDeclaration(interfaceCtClass);

            StringBuilder membersNamesStringArray = new StringBuilder();
            // Members access, Use CtClass to get the list of Methods ordered by declaration order.
            for (CtMethod method : allMethodsSortedByDeclaration) {
                CtClass type = method.getReturnType();
                if (type == CtClass.voidType) {
                    throw new Error("Can't create void member '" + method.getName() + "' for class " + name);
                }
                // Do not redeclare PK
                if (method.getName().equals(IEntity.PRIMARY_KEY)) {
                    continue;
                }
                assertOwnership(interfaceCtClass, method);
                //System.out.println("Creating " + method.getName() + " of " + method.getDeclaringClass().getName());
                CtMethod member = new CtMethod(type, method.getName(), null, implClass);
                member.setBody("return (" + type.getName() + ")getMember(\"" + method.getName() + "\");");
                implClass.addMethod(member);
                addAnnotation(member, Override.class);
                if (membersNamesStringArray.length() > 0) {
                    membersNamesStringArray.append(", ");
                }
                membersNamesStringArray.append("\"").append(method.getName()).append("\"");
            }

            CtMethod getMembersMethod = new CtMethod(pool.get(String[].class.getName()), "getMembers", null, implClass);
            if (membersNamesStringArray.length() == 0) {
                getMembersMethod.setBody("{ return new String[0]; }");
            } else {
                getMembersMethod.setBody("{ return new String[] {" + membersNamesStringArray + "}; }");
            }
            implClass.addMethod(getMembersMethod);

            //Static for optimization
            CtField entityMetaField = new CtField(pool.get(EntityMeta.class.getName()), "entityMeta", implClass);
            entityMetaField.setModifiers(Modifier.PRIVATE | Modifier.STATIC);
            implClass.addField(entityMetaField);

            CtMethod getEntityMetaMethod = new CtMethod(pool.get(EntityMeta.class.getName()), "getEntityMeta", null, implClass);
            getEntityMetaMethod.setBody("{ if (entityMeta == null) { entityMeta = super.getEntityMeta(); } return entityMeta; }");
            implClass.addMethod(getEntityMetaMethod);

            if (ServerSideConfiguration.isStartedUnderEclipse()) {
                try {
                    implClass.writeFile("target/entity-gen-classes");
                } catch (IOException e) {
                }
            }
            return implClass;
        } catch (CannotCompileException e) {
            log.error("Impl " + interfaceName + " compile error", e);
            throw new Error("Can't create class " + name);
        } catch (ClassNotFoundException e) {
            log.error("Impl " + interfaceName + " construction error", e);
            throw new Error("Can't create class " + name);
        } catch (NotFoundException e) {
            log.error("Impl " + interfaceName + " construction error", e);
            throw new Error("Can't create class " + name);
        }
    }

    List<CtMethod> getAllMethodsSortedByDeclaration(CtClass interfaceCtClass) throws NotFoundException {
        List<String> allMethodsNames = new Vector<String>();
        List<CtMethod> allMethodsSortedByDeclaration = new Vector<CtMethod>();
        for (CtMethod method : interfaceCtClass.getDeclaredMethods()) {
//            if (method.getDeclaringClass().equals(ctClassObject) || (method.getDeclaringClass().equals(ctClassIEntity))
//                    || (method.getDeclaringClass().equals(ctClassIObject))) {
//                continue;
//            }
            allMethodsSortedByDeclaration.add(method);
            allMethodsNames.add(method.getName());
        }
        List<CtMethod> allSuperMethods = new ArrayList<CtMethod>();
        for (CtClass itf : getInterfacesSortedByDeclaration(interfaceCtClass)) {
            for (CtMethod method : itf.getDeclaredMethods()) {
                if ((!allMethodsNames.contains(method.getName()))) {
                    allSuperMethods.add(method);
                    allMethodsNames.add(method.getName());
                }
            }
        }
        if (false) {
            for (CtMethod method : interfaceCtClass.getMethods()) {
                if (method.getDeclaringClass().equals(ctClassObject) || (method.getDeclaringClass().equals(ctClassIEntity))
                        || (method.getDeclaringClass().equals(ctClassIObject))) {
                    continue;
                }
                if ((!allMethodsSortedByDeclaration.contains(method)) && (!allSuperMethods.contains(method))) {
                    allSuperMethods.add(method);
                }
            }
        }
        allSuperMethods.addAll(allMethodsSortedByDeclaration);
        return allSuperMethods;
    }

    private List<CtClass> getInterfacesSortedByDeclaration(CtClass interfaceCtClass) throws NotFoundException {
        List<CtClass> list = new ArrayList<CtClass>();
        for (CtClass itf : interfaceCtClass.getInterfaces()) {
            if ((itf == ctClassObject) || (itf == ctClassIEntity) || (itf == ctClassIObject)) {
                continue;
            } else {
                if (!list.contains(itf)) {
                    list.add(itf);
                    for (CtClass ic : getInterfacesSortedByDeclaration(itf)) {
                        if (!list.contains(ic)) {
                            list.add(ic);
                        }
                    }
                }
            }
        }
        Collections.reverse(list);
        return list;
    }

    private void assertOwnership(CtClass interfaceCtClass, CtMethod method) throws ClassNotFoundException, NotFoundException {
        //TODO move to meta generator where GenericArtumentTypes are avalablel
        if (true) {
            return;
        }
        Owner owner = (Owner) method.getAnnotation(Owner.class);
        if (owner == null) {
            return;
        }
        CtClass ownerType = method.getReturnType();
        boolean ownedReferenceFound = false;
        for (CtMethod ownerMethod : getAllMethodsSortedByDeclaration(ownerType)) {
            if (ownerMethod.getReturnType().equals(interfaceCtClass)) {
                Owned owned = (Owned) ownerMethod.getAnnotation(Owned.class);
                if (owned == null) {
                    throw new AssertionError("Missing @Owned annotation on member '" + ownerMethod.getName() + "' in " + ownerType.getName());
                }
                ownedReferenceFound = true;
            } else {
                //TODO Polymorphic case
            }

        }
        if (!ownedReferenceFound) {
            //throw new AssertionError("Missing @Owned member of type " + interfaceCtClass.getName() + " in " + ownerType.getName());
        }
    }

    private void addAnnotation(CtMethod member, Class<?> annotationClass) throws NotFoundException {
        CtClass implClass = member.getDeclaringClass();
        ConstPool constPool = implClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = (AnnotationsAttribute) member.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
        if (attr == null) {
            attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        }
        Annotation annotation = new Annotation(constPool, pool.get(annotationClass.getName()));
        attr.addAnnotation(annotation);
        member.getMethodInfo().addAttribute(attr);
    }
}
