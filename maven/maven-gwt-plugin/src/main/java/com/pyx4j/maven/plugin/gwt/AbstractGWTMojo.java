/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 20-Oct-06
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.maven.plugin.gwt;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;

public abstract class AbstractGWTMojo extends AbstractMojo {

    /**
     * A single GWT Module to compile
     * 
     * @parameter
     */
    protected String module;

    /**
     * A set of GWT Modules to compile
     * 
     * @parameter
     */
    protected String[] modules;

    /**
     * -logLevel The level of logging detail: ERROR, WARN, INFO, TRACE, DEBUG, SPAM, or
     * ALL
     * 
     * @parameter default-value="WARN";
     */
    protected String logLevel;

    /**
     * -war(-out) The directory to write output files into
     * 
     * @parameter default-value="${project.build.directory}";
     */
    protected File webappDirectory;

    /**
     * -gen The directory into which generated files will be written for review
     * 
     * @parameter default-value="${project.build.directory}/gwt-generated";
     */
    protected File generated;

    /**
     * -style Script output style: OBF[USCATED], PRETTY, or DETAILED
     * 
     * @parameter default-value="OBFUSCATED";
     */
    protected String style;

    /**
     * Specifies the number of local workers to use when compiling per mutations.
     * 
     * We use System.setProperty "gwt.jjs.maxThreads".
     * 
     * @parameter default-value="1";
     */
    protected int localWorkers;

    /**
     * -workDir The compiler's working directory for internal use
     * 
     * @parameter default-value="${project.build.directory}/gwt-work";
     */
    protected File workDir;

    /**
     * -extra The directory into which report files will be written for review
     * 
     * @parameter default-value="${project.build.directory}/gwt-extra";
     */
    protected File extra;
}
