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
 * Created on Nov 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.maven.plugin.gwt;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

/**
 * Copy java source code and GWT module descriptors as resources in the build
 * outputDirectory.
 * 
 */
@Mojo(name = "source-resources", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class JavaSourceResourcesMojo extends AbstractMojo {

    /**
     * Allow to skip execution of this Mojo
     * 
     */
    @Parameter(defaultValue = "${project.jar-source-4gwt}")
    private boolean active;

    /**
     * The output directory into which to copy the resources.
     */
    @Parameter(required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    /**
     * Source directory containing the java files to be copied.
     */
    @Parameter(required = true, defaultValue = "${project.build.sourceDirectory}")
    private String sourceDirectory;

    /**
     * Set whether Sources are filtered to replace tokens with parameterized values or not.
     */
    @Parameter(defaultValue = "false")
    private boolean filtering;

    /**
     * The character encoding scheme to be applied when filtering resources.
     * 
     */
    @Parameter(defaultValue = "${project.build.sourceEncoding}")
    protected String encoding;

    /**
     * Additional file extensions to not apply filtering (already defined are : jpg, jpeg, gif, bmp, png)
     * 
     * @since 2.3
     */
    @Parameter
    protected List<String> nonFilteredFileExtensions;

    @Component
    protected MavenProject project;

    @Component
    protected MavenResourcesFiltering mavenResourcesFiltering;

    @Component
    protected MavenSession session;

    @Override
    public void execute() throws MojoExecutionException {
        if (!active) {
            getLog().debug("inactive");
            return;
        }
        List<String> nonFilteredFileExtensions = Collections.emptyList();
        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution(getResources(), getOutputDirectory(), project, encoding, null,
                nonFilteredFileExtensions, session);
        if (nonFilteredFileExtensions != null) {
            mavenResourcesExecution.setNonFilteredFileExtensions(nonFilteredFileExtensions);
        }
        try {
            mavenResourcesFiltering.filterResources(mavenResourcesExecution);
        } catch (MavenFilteringException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    private List<Resource> getResources() {
        List<Resource> resources = new Vector<Resource>();

        Resource res = new Resource();
        res.setDirectory(sourceDirectory);
        res.setFiltering(filtering);

        res.addInclude("**/*.java");
        res.addInclude("**/*.gwt.xml");

        resources.add(res);

        return resources;
    }
}
