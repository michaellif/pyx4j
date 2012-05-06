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
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenFilteringException;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

/**
 * Copy java source code and GWT module descriptors as resources in the build
 * outputDirectory.
 * 
 * @goal source-resources
 * @phase prepare-package
 * @threadSafe
 */
public class JavaSourceResourcesMojo extends AbstractMojo {

    /**
     * Allow to skip execution of this Mojo
     * 
     * @parameter expression = "${project.jar-source-4gwt}" default-value="false"
     * @required
     */
    private boolean active;

    /**
     * The output directory into which to copy the resources.
     * 
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Source directory containing the java files to be copied.
     * 
     * @parameter expression = "${project.build.sourceDirectory}"
     * @required
     */
    private String sourceDirectory;

    /**
     * Set whether Sources are filtered to replace tokens with parameterized values or
     * not.
     * 
     * @parameter expression = "false"
     * @required
     */
    private boolean filtering;

    /**
     * The character encoding scheme to be applied when filtering resources.
     * 
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;

    /**
     * Additional file extensions to not apply filtering (already defined are : jpg, jpeg,
     * gif, bmp, png)
     * 
     * @parameter
     * @since 2.3
     */
    protected List<?> nonFilteredFileExtensions;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * 
     * @component role="org.apache.maven.shared.filtering.MavenResourcesFiltering"
     *            role-hint="default"
     * @required
     */
    protected MavenResourcesFiltering mavenResourcesFiltering;

    /**
     * @parameter default-value="${session}"
     * @readonly
     * @required
     */
    protected MavenSession session;

    @Override
    public void execute() throws MojoExecutionException {
        if (!active) {
            getLog().debug("inactive");
            return;
        }
        MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution(getResources(), getOutputDirectory(), project, encoding, null,
                Collections.EMPTY_LIST, session);
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

    private List<?> getResources() {
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
