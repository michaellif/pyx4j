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
 * Created on 2011-05-10
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.maven.plugin.gwt;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.util.DirectoryScanner;

abstract class AbstractSourceJarMojo extends AbstractMojo {

    private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };

    private static final String[] DEFAULT_INCLUDES = new String[] { "**/*.java", "**/*.gwt.xml" };

    /**
     * List of files to include. Specified as fileset patterns which are relative to the
     * input directory whose contents is being packaged into the JAR. Defaults to .java
     * and .gwt.xml files.
     */
    @Parameter
    private String[] includes;

    /**
     * List of files to exclude. Specified as fileset patterns which are relative to the
     * input directory whose contents is being packaged into the JAR.
     * 
     */
    @Parameter
    private String[] excludes;

    /**
     * Name of the generated JAR.
     */
    @Parameter(required = true, defaultValue = "${project.build.finalName}")
    private String finalName;

    protected abstract String getClassifier();

    /**
     * Directory containing the generated JAR.
     */
    @Parameter(required = true, defaultValue = "${project.build.directory}")
    private File outputDirectory;

    /**
     * The Jar archiver.
     */
    @Component(role = org.codehaus.plexus.archiver.Archiver.class, hint = "jar")
    private JarArchiver jarArchiver;

    /**
     * The Maven project.
     */
    @Component
    private MavenProject project;

    /**
     * The archive configuration to use. See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver Reference</a>.
     */
    @Parameter
    private final MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    @Component
    private MavenSession session;

    protected abstract List<String> getCompileSourceRoots();

    protected abstract boolean active();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!active()) {
            getLog().debug("inactive");
            return;
        }
        String jarName = finalName;
        if (getClassifier() != null) {
            jarName += "-" + getClassifier();
        }
        File jarFile = new File(outputDirectory, jarName + ".jar");
        File origJarFile = new File(outputDirectory, jarName + "-b4gwt.jar");
        if (origJarFile.exists()) {
            if (!origJarFile.delete()) {
                throw new MojoExecutionException("Error removing " + origJarFile);
            }
        }
        if (!jarFile.renameTo(origJarFile)) {
            throw new MojoExecutionException("Error renaming " + jarFile + " to " + origJarFile);
        }

        MavenArchiver archiver = new MavenArchiver();

        archiver.setArchiver(jarArchiver);
        archiver.setOutputFile(jarFile);
        archive.setForced(false);
        try {
            archive.setAddMavenDescriptor(false);

            // Avoid annoying messages in log "com/package/Ccc.java already added, skipping"
            List<String> jarExcludes = new Vector<String>();

            // Add Sources first since they may already be present in jar from previous run and changed.
            for (String sourceDirectory : getCompileSourceRoots()) {
                File dir = new File(sourceDirectory);
                DirectoryScanner srcScanner = new DirectoryScanner();
                srcScanner.setBasedir(dir);
                srcScanner.setIncludes(getIncludes());
                srcScanner.setExcludes(getExcludes());
                srcScanner.scan();

                String[] includedFiles = srcScanner.getIncludedFiles();
                for (String source : includedFiles) {
                    jarArchiver.addFile(new File(dir, source), source);
                    jarExcludes.add(source);
                }
            }

            jarArchiver.addArchivedFileSet(origJarFile, null, jarExcludes.toArray(new String[jarExcludes.size()]));
            archiver.createArchive(session, project, archive);
        } catch (Exception e) {
            throw new MojoExecutionException("Error assembling JAR", e);
        }
    }

    private String[] getIncludes() {
        if (includes != null && includes.length > 0) {
            return includes;
        }
        return DEFAULT_INCLUDES;
    }

    private String[] getExcludes() {
        if (excludes != null && excludes.length > 0) {
            return excludes;
        }
        return DEFAULT_EXCLUDES;
    }

}
