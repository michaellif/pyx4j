/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 6, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.impl;

import java.io.IOException;
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
import com.pyx4j.entity.shared.meta.MemberMeta;

public abstract class EntityImplGenerator {

    private static final Logger log = LoggerFactory.getLogger(EntityImplGenerator.class);

    public static final String MARKER_RESOURCE_NAME = "META-INF/MANIFEST.MF";

    public static void generate() {
        List<String> classes = EntityClassFinder.findEntityClasses();
        log.debug("found IEntity {} ", classes);

        ClassPool pool = ClassPool.getDefault();
        //GAE hack start
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> urls;
        try {
            urls = cld.getResources(MARKER_RESOURCE_NAME);
        } catch (IOException e) {
            log.error("unable to find jar markers", e);
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
                log.debug("ClassPool append path {}", pathname);
                pool.appendClassPath(pathname);
            } catch (NotFoundException e) {
                log.error("Can't append path", e);
            }
        }
        //GAE Hack end

        for (String c : classes) {
            String name = c + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
            try {
                CtClass cc = pool.makeClass(name);
                cc.setSuperclass(pool.get(SharedEntityHandler.class.getName()));
                // Constructors
                CtConstructor defaultConstructor = new CtConstructor(null, cc);
                defaultConstructor.setBody("super(" + c + ".class);");
                cc.addConstructor(defaultConstructor);

                CtClass ctStringClass = pool.get(String.class.getName());

                CtConstructor memberConstructor = new CtConstructor(new CtClass[] { pool.get(IEntity.class.getName()), ctStringClass }, cc);
                memberConstructor.setBody("super(" + c + ".class, $1, $2);");
                cc.addConstructor(memberConstructor);

                // Abstract methods
                CtMethod getMemberMeta = new CtMethod(pool.get(MemberMeta.class.getName()), "getMemberMeta", new CtClass[] { ctStringClass }, cc);
                getMemberMeta.setBody("return " + EntityImplReflectionHelper.class.getName() + ".getMemberMeta(this, $1);");
                cc.addMethod(getMemberMeta);

                CtMethod lazyCreateMember = new CtMethod(pool.get(IObject.class.getName()), "lazyCreateMember", new CtClass[] { ctStringClass }, cc);
                lazyCreateMember.setBody("return " + EntityImplReflectionHelper.class.getName() + ".lazyCreateMember(this, $1);");
                cc.addMethod(lazyCreateMember);

                // Members access

                cc.toClass();
            } catch (CannotCompileException e) {
                log.error("Impl compile error", e);
                throw new Error("Can't create class " + name);
            } catch (NotFoundException e) {
                log.error("Impl  construction error", e);
                throw new Error("Can't create class " + name);
            }

        }
        log.info("Created {} IEntity implementations", classes.size());

    }
}
