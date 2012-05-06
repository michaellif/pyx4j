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

import java.util.List;

/**
 * Add java source and GWT module descriptors as resources to project jar
 * 
 * @goal source-jar
 * @phase package
 * @threadSafe
 */
public class JavaSourceJarMojo extends AbstractSourceJarMojo {

    /**
     * Allow to skip execution of this Mojo
     * 
     * @parameter expression = "${project.jar-source-4gwt}" default-value="false"
     * @required
     */
    private boolean active;

    /**
     * Source directory containing the java files to be copied.
     * 
     * @parameter default-value="${project.compileSourceRoots}"
     * @required
     * @readonly
     */
    private List<String> compileSourceRoots;

    @Override
    protected String getClassifier() {
        return null;
    }

    @Override
    protected boolean active() {
        return active;
    }

    @Override
    protected List<String> getCompileSourceRoots() {
        return compileSourceRoots;
    }

}
