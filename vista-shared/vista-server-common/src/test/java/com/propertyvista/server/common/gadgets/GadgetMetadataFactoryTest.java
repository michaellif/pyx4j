/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.gadgets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class GadgetMetadataFactoryTest extends TestCase {

    public void testGadgetMetadataNamingConvention() {
        if (false) {
            StringBuilder badGadgetMetadataNamesBuilder = new StringBuilder();
            for (Class<? extends GadgetMetadata> gadgetMetadataClass : getGadgetMetadataClassesFromClassPath()) {
                if (!gadgetMetadataClass.getSimpleName().endsWith("GadgetMetadata")) {
                    badGadgetMetadataNamesBuilder.append(gadgetMetadataClass.getName()).append("\n");
                }
            }
            String badNames = badGadgetMetadataNamesBuilder.toString();
            Assert.assertTrue("These are evil gadgets that don't follow the naming convention:\n " + badNames, badNames.length() == 0);
        }
    }

    public void testDefaultSettingsInitializorsExisitForEveryGadgetMetadata() {

    }

    private List<Class<? extends GadgetMetadata>> getGadgetMetadataClassesFromClassPath() {
        String gadgetMetadataPackagePath = GadgetMetadata.class.getPackage().getName().replace('.', File.separatorChar).replace("base", "");

        System.out.println(gadgetMetadataPackagePath);
        List<String> gadgetMetadataClassNames = new ArrayList<String>();
        for (String classPathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (classPathEntry.endsWith("jar")) {
                gadgetMetadataClassNames.addAll(gadgetMetadataNamesFromJar(classPathEntry, gadgetMetadataPackagePath));
            } else {
                gadgetMetadataClassNames.addAll(gadgetMetadataNamesFromDir(classPathEntry, gadgetMetadataPackagePath));
            }
        }

        if (false) {
            // this is here just for testing jars, normally it should find in on disk
            gadgetMetadataClassNames.addAll(gadgetMetadataNamesFromJar(
                    "C:\\work\\m2-repository\\com\\propertyvista\\vista-domain\\1.0.5-SNAPSHOT\\vista-domain-1.0.5-SNAPSHOT.jar", gadgetMetadataPackagePath));
        }

        List<Class<? extends GadgetMetadata>> gadgetMetadataClasses = new ArrayList<Class<? extends GadgetMetadata>>();
        for (String gadgetMetadataClassName : gadgetMetadataClassNames) {
            System.out.println(gadgetMetadataClassName);
            try {

                Class<?> clazz = null;
                if (true) {
                    clazz = ClassLoader.getSystemClassLoader().loadClass(gadgetMetadataClassName);
                } else {

                }
                if (GadgetMetadata.class.isAssignableFrom(clazz)) {
                    gadgetMetadataClasses.add((Class<? extends GadgetMetadata>) clazz);
                }
            } catch (ClassNotFoundException e) {
                throw new Error("class was not found, please clean the project", e);
            }
        }

        return gadgetMetadataClasses;
    }

    private Collection<? extends String> gadgetMetadataNamesFromDir(String classPathEntry, String gadgetMetadataPackagePath) {
        List<String> paths = new ArrayList<String>();
        File root = new File(classPathEntry + File.separatorChar + gadgetMetadataPackagePath);
        gadgetMetadataNamesFromDir(paths, root, 1000);

        List<String> names = new ArrayList<String>();
        for (String path : paths) {
            int i = path.indexOf(gadgetMetadataPackagePath);
            names.add(path.substring(i).replaceFirst("\\.class$", "").replace(File.separatorChar, '.'));
        }
        return names;
    }

    private void gadgetMetadataNamesFromDir(List<String> paths, File rootDir, int maxDepthSafeGuard) {
        if (maxDepthSafeGuard == 0) {
            // maxDepthSafeGuard is for cases where Files follow links
            return;
        }
        File[] files = rootDir.listFiles();
        if (files == null) {
            return;
        } else {
            for (File file : rootDir.listFiles()) {
                if (file.isDirectory()) {
                    gadgetMetadataNamesFromDir(paths, file, maxDepthSafeGuard - 1);
                } else if (file.getPath().endsWith(".class")) {
                    paths.add(file.getAbsolutePath());
                }
            }
        }
    }

    private List<String> gadgetMetadataNamesFromJar(String jarFileName, String gadgetMetadataRootPackagetPath) {
        List<String> names = new ArrayList<String>();
        String prefix = gadgetMetadataRootPackagetPath.replace(File.separatorChar, '/');
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarFileName);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().startsWith(prefix) & entry.getName().endsWith(".class")) {
                    names.add(entry.getName().replaceFirst("\\.class$", "").replace('/', '.'));
                }
            }
        } catch (IOException e) {

        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                }
            }
        }
        return names;
    }
}
