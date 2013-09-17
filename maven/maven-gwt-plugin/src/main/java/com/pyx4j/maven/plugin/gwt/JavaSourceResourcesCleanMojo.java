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
 * Created on Nov 6, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.maven.plugin.gwt;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Remove java source code and GWT module descriptors from the build outputDirectory.
 * 
 * Used for better GWT eclipse projects sync
 * 
 */
@Mojo(name = "remove-source-resources", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class JavaSourceResourcesCleanMojo extends AbstractMojo {

    /**
     * Allow to skip execution of this Mojo
     */
    @Parameter(defaultValue = "${project.jar-source-4gwt}")
    private boolean active;

    /**
     * The output directory into which to copy the resources.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    /**
     * Source directory containing the java files to be copied.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${project.build.sourceDirectory}")
    private String sourceDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!active) {
            getLog().debug("inactive");
            return;
        }

        DirectoryScanner outScanner = new DirectoryScanner();
        outScanner.setBasedir(new File(sourceDirectory));
        outScanner.setIncludes(new String[] { "**/*.java", "**/*.gwt.xml" });
        outScanner.scan();

        String[] includedFiles = outScanner.getIncludedFiles();
        int count = 0;
        for (String source : includedFiles) {
            File srcOut = new File(outputDirectory, source);
            if (srcOut.exists()) {
                if (!srcOut.delete()) {
                    throw new MojoExecutionException("Unable to remove " + srcOut.getAbsolutePath());
                } else {
                    count++;
                }
            }
        }
        if (count != 0) {
            getLog().info("Removed " + count + " resources");
        }
    }

}
