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
 * Created on Oct 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.i18n.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.StrictPatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import com.pyx4j.scanner.DirectoryScanner;
import com.pyx4j.scanner.JarFileScanner;
import com.pyx4j.scanner.Scanner;
import com.pyx4j.scanner.ScannerEntry;

/**
 * 
 * Extracts i18n text from classes dependencies.
 * 
 * @goal extract
 * @phase prepare-package
 * @requiresDependencyResolution compile
 * @threadSafe
 */
public class ExtractMojo extends AbstractMojo {

    /**
     * Dependencies to ignore.
     * The artifact syntax is defined by AbstractStrictPatternArtifactFilter.
     * 
     * The artifact pattern syntax is of the form:
     * 
     * <pre>
     * [groupId]:[artifactId]:[type]:[version]
     * </pre>
     * 
     * <p>
     * Where each pattern segment is optional and supports full and partial <code>*</code> wildcards. An empty pattern segment is treated as an implicit
     * wildcard.
     * </p>
     * 
     * <p>
     * For example, <code>org.apache.*</code> would match all artifacts whose group id started with <code>org.apache.</code>, and <code>:::*-SNAPSHOT</code>
     * would match all snapshot artifacts.
     * </p>
     * 
     * @parameter
     */
    public List<String> excludes = null;

    /**
     * Dependencies to include.
     * The artifact syntax is defined by AbstractStrictPatternArtifactFilter.
     * 
     * @parameter
     */
    public List<String> includes = null;

    /**
     * Dependency artifacts scope: "compile", test", "runtime" or "system";
     * 
     * @parameter expression="compile"
     */
    private String scope;

//TODO  public List<String> classesInclude;

//TODO    public List<String> classesExclude;

    /**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        @SuppressWarnings("rawtypes")
        List dependancy;
        if (Artifact.SCOPE_COMPILE.equals(scope)) {
            dependancy = this.project.getCompileArtifacts();
        } else if (Artifact.SCOPE_TEST.equals(scope)) {
            dependancy = this.project.getTestArtifacts();
        } else if (Artifact.SCOPE_RUNTIME.equals(scope)) {
            dependancy = this.project.getRuntimeArtifacts();
        } else if (Artifact.SCOPE_SYSTEM.equals(scope)) {
            dependancy = this.project.getSystemArtifacts();
        } else {
            throw new MojoExecutionException("Unsupported scope " + scope);
        }

        StrictPatternExcludesArtifactFilter excludeFilter = null;
        if ((excludes != null) && excludes.size() > 0) {
            getLog().debug("excludes:" + excludes);
            excludeFilter = new StrictPatternExcludesArtifactFilter(excludes);
        }
        StrictPatternIncludesArtifactFilter includeFilter = null;
        if ((includes != null) && includes.size() > 0) {
            getLog().debug("includes:" + includes);
            includeFilter = new StrictPatternIncludesArtifactFilter(includes);
        }

        ConstantExtractor extractor = new ConstantExtractor();
        for (@SuppressWarnings("rawtypes")
        Iterator i = dependancy.iterator(); i.hasNext();) {
            Artifact artifact = (Artifact) i.next();
            if ((excludeFilter != null) && !excludeFilter.include(artifact)) {
                continue;
            }
            if ((includeFilter != null) && !includeFilter.include(artifact)) {
                continue;
            }
            processArtifact(extractor, artifact);
        }
    }

    private File getArtifactFile(Artifact artifact) throws MojoExecutionException {
        if (artifact.getClassifier() != null) {
            return artifact.getFile();
        }
        String refId = artifact.getGroupId() + ":" + artifact.getArtifactId();
        MavenProject artifactProject = (MavenProject) project.getProjectReferences().get(refId);
        if (artifactProject != null) {
            return new File(artifactProject.getBuild().getOutputDirectory());
        } else {
            File file = artifact.getFile();
            if ((file == null) || (!file.exists())) {
                throw new MojoExecutionException("Dependency Resolution Required " + artifact);
            }
            return file;
        }
    }

    private void processArtifact(ConstantExtractor extractor, Artifact artifact) throws MojoExecutionException {
        getLog().debug("processing " + artifact);
        System.out.println("--" + artifact);

        File file = getArtifactFile(artifact);
        Scanner scanner = null;
        try {
            if (file.isDirectory()) {
                scanner = new DirectoryScanner(file);
            } else {
                scanner = new JarFileScanner(file);
            }
            for (ScannerEntry source : scanner.getEntries()) {
                if (!source.isDirectory() && source.getName().endsWith(".class")) {
                    InputStream in = source.getInputStream();
                    try {
                        extractor.readClass(in);
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error sacanning dependency artifact " + artifact, e);
        } catch (AnalyzerException e) {
            throw new MojoExecutionException("Error reading dependency artifact " + artifact, e);
        } finally {
            IOUtils.closeQuietly(scanner);
        }
    }
}
