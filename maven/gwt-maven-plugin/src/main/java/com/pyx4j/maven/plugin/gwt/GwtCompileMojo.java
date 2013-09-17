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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;

/**
 * 
 * Runs the GWT Java to Javascript Compiler
 * 
 * use phase compile or prepare-package
 */
@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class GwtCompileMojo extends AbstractGWTMojo {

    /**
     * Compiler class name
     * 
     * Use 'com.google.gwt.dev.Compiler' to switch to GWT 2 arguments style.
     * 
     * Use 'com.google.gwt.dev.GWTCompiler' for old GWT 1.5 compiler
     * 
     */
    @Parameter(defaultValue = "com.google.gwt.dev.Compiler")
    private String compilerClass;

    /**
     * Use ThreadedPermutationWorkerFactory or ExternalPermutationWorkerFactory
     */
    @Parameter(property = "gwt.threadedPermutationWorker", defaultValue = "true")
    private boolean threadedPermutationWorker;

    /**
     * Whether or not to enable assertions in generated scripts (-ea).
     */
    @Parameter(defaultValue = "false")
    private boolean enableAssertions;

    /**
     * Add -strict parameter to the compiler command line.
     */
    @Parameter(property = "gwt.compiler.strict", defaultValue = "false")
    private boolean strict;

    /**
     * GWT 2.0 Enable faster, but less-optimized, compilations
     * 
     * @since GWT 2.0
     */
    @Parameter(defaultValue = "false")
    private boolean draftCompile;

    /**
     * Disables run-time checking of cast operations
     * 
     * @since GWT 2.0
     */
    @Parameter(defaultValue = "false")
    private boolean disableCastChecking;

    /**
     * Disables getName() java.lang.Class method.
     * 
     * @since GWT 2.0
     */
    @Parameter(defaultValue = "false")
    private boolean disableClassMetadata;

    /**
     * GWT 2.0 Enable Story Of Your Compile
     * 
     * @since GWT 2.0
     */
    @Parameter(defaultValue = "false")
    private boolean soyc;

    /**
     * Create Compile Report
     * 
     * @since GWT 2.0
     */
    @Parameter(defaultValue = "false")
    private boolean compileReport;

    /**
     * Additional compiler arguments. e.g. -XdisableClassMetadata -XdisableCastChecking
     * -XdisableAggressiveOptimization -XdisableRunAsync -XsoycDetailed
     * 
     */
    @Parameter
    private List<String> args;

    /**
     * Additional static setter for compilerClass
     * 
     */
    @Parameter
    private Map<String, String> compilerSet;

    /**
     * The directory containing generated classes.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${project.build.outputDirectory}")
    private File classesDirectory;

    /**
     * Location of the source files.
     * 
     */
    @Parameter
    private List<String> sourceDirectories;

    /**
     * Allows to disable GWT compilation cache.
     * 
     */
    @Parameter(property = "gwt.persistentunitcache", defaultValue = "true")
    private final boolean persistentUnitCache = true;

    /**
     * GWT Compiler now cache compilation artifacts between runs.
     * 
     */
    @Parameter(defaultValue = "${project.build.directory}")
    private File persistentUnitCacheDir;

    /**
     * Use plugin dependencies for GWT compilation. A comma-separated list of artifacts.
     * The artifact syntax is defined by StrictPatternIncludesArtifactFilter.
     * 
     */
    @Parameter
    protected String usePluginDependencies;

    /**
     * Pass project properties as Java system, properties.
     * 
     */
    @Parameter
    private List<String> systemProperties;

    /**
     * The maven project descriptor
     * 
     */
    @Component
    private MavenProject project;

    /**
     * The plugin dependencies.
     * 
     */
    @Component
    protected List<Artifact> pluginArtifacts;

    private Throwable executionError;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        SecurityManager oldSm = null;
        oldSm = System.getSecurityManager();
        PrintStream origStdOut = System.out;
        System.setOut(new GwtLogFilterPrintStream(origStdOut));
        try {
            System.setSecurityManager(NoExitSecurityManager.INSTANCE);
        } catch (SecurityException ex) {
            throw new MojoExecutionException("Cannot set custom SecurityManager. " + "Compile's call to System.exit() will cause application shutdown "
                    + "if not handled by the current SecurityManager.");
        }
        try {
            executeCompiler();
        } finally {
            System.setOut(origStdOut);
            System.setSecurityManager(oldSm);
        }
    }

    public void executeCompiler() throws MojoExecutionException, MojoFailureException {

        long start = System.currentTimeMillis();

        if (getLog().isDebugEnabled()) {
            getLog().debug("GwtCompileMojo#execute()");
        }

        URLClassLoader loader = getClassLoader();

        if (getLog().isDebugEnabled()) {
            for (URL url : loader.getURLs()) {
                getLog().debug("classpath:" + url);
            }
            try {
                loader.loadClass("org.apache.commons.logging.LogFactory");
            } catch (ClassNotFoundException e) {
                getLog().error("Can't find LogFactory", e);
            }
        }

        final Class<?> compiler;
        try {
            compiler = loader.loadClass(compilerClass);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Could not find " + compilerClass, e);
        }
        if (getLog().isDebugEnabled()) {
            getLog().debug("  Found class:" + compiler);
        }

        if (compilerSet != null) {
            for (Map.Entry<String, String> entry : compilerSet.entrySet()) {
                try {
                    Method method = compiler.getMethod(entry.getKey(), String.class);
                    method.invoke(null, new Object[] { entry.getValue() });
                } catch (Throwable e) {
                    throw new MojoExecutionException("Could not invoke setter " + entry.getKey() + " (String).", e);
                }
            }
            if (getLog().isDebugEnabled()) {
                try {
                    Method method = compiler.getMethod("debugEnabled", boolean.class);
                    method.invoke(null, new Object[] { true });
                } catch (Throwable e) {
                    getLog().warn("Could not invoke setter debugEnabled(boolean).", e);
                }
            }
        }

        final String mainMethodName = "main";
        final Method mainMethod;
        try {
            mainMethod = compiler.getMethod(mainMethodName, String[].class);
        } catch (NoSuchMethodException e) {
            throw new MojoExecutionException("Could not find " + compilerClass + "." + mainMethodName + "(String[]).", e);
        }
        if (getLog().isDebugEnabled()) {
            getLog().debug("  Found method:" + mainMethod);
        }

        final List<String> argList = new LinkedList<String>();

        boolean gwt2 = compilerClass.equals("com.google.gwt.dev.Compiler");

        if (!gwt2) {
            argList.add("-out");
            argList.add(this.webappDirectory.getAbsolutePath());
            if (this.generated != null) {
                argList.add("-gen");
                argList.add(this.generated.getAbsolutePath());
            }
            argList.add("-logLevel");
            argList.add(this.logLevel);
            argList.add("-style");
            argList.add(this.style);
            argList.add("-localWorkers");
            argList.add(String.valueOf(this.localWorkers));

            System.setProperty("gwt.nowarn.legacy.tools", "true");
            System.setProperty("gwt.jjs.maxThreads", String.valueOf(this.localWorkers));
        } else {
            argList.add("-XdisableUpdateCheck");

            argList.add("-war");
            argList.add(this.webappDirectory.getAbsolutePath());

            if (deploy != null) {
                argList.add("-deploy");
                argList.add(this.deploy.getAbsolutePath());
            }

            if (workDir != null) {
                argList.add("-workDir");
                argList.add(this.workDir.getAbsolutePath());
            }
            if (this.extra != null) {
                argList.add("-extra");
                argList.add(this.extra.getAbsolutePath());
            }

            if (this.generated != null) {
                argList.add("-gen");
                argList.add(this.generated.getAbsolutePath());
            }

            argList.add("-logLevel");
            argList.add(this.logLevel);

            if (soyc) {
                argList.add("-soyc");
            }

            if (draftCompile) {
                argList.add("-draftCompile");
            }
            if (compileReport) {
                argList.add("-compileReport");
            }
            if (strict) {
                argList.add("-strict");
            }
            if (disableClassMetadata) {
                argList.add("-XdisableClassMetadata");
            }
            if (disableCastChecking) {
                argList.add("-XdisableCastChecking");
            }
            if (enableAssertions) {
                argList.add("-ea");
            }

            argList.add("-style");
            argList.add(this.style);

            argList.add("-localWorkers");
            argList.add(String.valueOf(this.localWorkers));

            if (args != null) {
                for (String arg : args) {
                    argList.add(arg);
                }
            }

            if (threadedPermutationWorker) {
                getLog().info("Use default ThreadedPermutationWorkerFactory");
                System.setProperty("gwt.jjs.permutationWorkerFactory", "com.google.gwt.dev.ThreadedPermutationWorkerFactory");
                System.setProperty("gwt.jjs.maxThreads", String.valueOf(this.localWorkers));
            } else {
                getLog().info("Use default ExternalPermutationWorkerFactory");
                System.setProperty("gwt.jjs.permutationWorkerFactory", "com.google.gwt.dev.ExternalPermutationWorkerFactory");
                // See ExternalPermutationWorkerFactory
                StringBuilder classPath = new StringBuilder();
                for (URL url : loader.getURLs()) {
                    if (url.getFile().contains("/google/gwt/gwt")) {
                        if (classPath.length() > 0) {
                            classPath.append(File.pathSeparatorChar);
                        }
                        classPath.append(url.getFile());
                    }
                }
                // -classpath does not work since 'java' can't process two -cp arguments at the same time
                System.setProperty("gwt.jjs.javaArgs", "-Xbootclasspath/a:" + classPath);
            }
        }

        System.setProperty("gwt.persistentunitcache", String.valueOf(persistentUnitCache));
        System.setProperty("gwt.persistentunitcachedir", persistentUnitCacheDir.toString());
        if (systemProperties != null) {
            for (String property : systemProperties) {
                System.setProperty(property, project.getProperties().getProperty(property));
            }
        }

        getLog().info(
                "Max Memory " + (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + " MB, Free Memory " + (Runtime.getRuntime().freeMemory() / (1024 * 1024))
                        + " MB");
        getLog().info("Args " + argList);

        List<String> gwtModules = new Vector<String>();
        if (this.module != null) {
            gwtModules.add(this.module);
        }
        if ((this.modules != null) && (this.modules.length > 0)) {
            gwtModules.addAll(Arrays.asList(this.modules));
        }

        final String[] args = argList.toArray(new String[argList.size() + 1]);

        for (String gwtModule : gwtModules) {

            args[args.length - 1] = gwtModule;

            Thread compileThread = new Thread() {
                @Override
                public void run() {
                    try {
                        Object compilerInstance = null;
                        mainMethod.invoke(compilerInstance, new Object[] { args });
                    } catch (Throwable e) {
                        executionError = e;
                    }
                }
            };

            compileThread.setContextClassLoader(loader);
            compileThread.start();
            try {
                compileThread.join();
            } catch (InterruptedException e) {
                throw new MojoExecutionException("GWT Compiler thread stopped.", e);
            }

            if (executionError != null) {
                if (executionError instanceof InvocationTargetException) {
                    executionError = ((InvocationTargetException) executionError).getCause();
                }
                if (executionError instanceof JVMExitSecurityException) {
                    JVMExitSecurityException x = (JVMExitSecurityException) executionError;
                    if (x.getExitStatus() != 0) {
                        throw new MojoFailureException("GWT Compiler execution error " + x.getExitStatus());
                    }
                } else {
                    throw new MojoExecutionException("GWT Compiler execution error.", executionError);
                }
            }
            long duration = (System.currentTimeMillis() - start);
            getLog().info("-------------------------------------------");
            getLog().info("GWT Compiled " + (draftCompile ? "DRAFT " : "") + "in " + (duration / 1000) + " sec");
            getLog().info("-------------------------------------------");
        }

        // Java 7
        if (loader instanceof Closeable) {
            try {
                ((Closeable) loader).close();
            } catch (Throwable e) {
                getLog().warn("error closing classLoader " + e.getMessage());
            }
        }

    }

    private URL convert(String path) throws MojoExecutionException {
        // Solve the space problem in path
        try {
            URL urlToConvert = new File(path).getCanonicalFile().toURI().toURL();
            String url = urlToConvert.toExternalForm();
            return new File(url.substring("file:".length())).toURI().toURL();
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to convert original classpath to URL.", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected URLClassLoader getClassLoader() throws MojoExecutionException {
        final List<URL> urls = new Vector<URL>();

        ClassLoader cl;
        cl = this.getClass().getClassLoader();
        String classResource = compilerClass.replace('.', '/') + ".class";
        URL url = cl.getResource(classResource);
        if (url != null) {
            String compilerJar = url.toExternalForm();
            if (compilerJar.startsWith("jar:file:")) {
                compilerJar = compilerJar.substring("jar:file:".length());
                compilerJar = compilerJar.substring(0, compilerJar.indexOf('!'));
            } else {
                throw new MojoExecutionException("Unrecognized location (" + compilerJar + ") in classpath");
            }
            getLog().info("compilerClass found in " + compilerJar);
            urls.add(convert(compilerJar));
        } else {
            getLog().warn("compilerClass " + compilerClass + " not found");
        }

        try {
            URL classesDirectoryUrl = classesDirectory.toURI().toURL();
            getLog().info("GWT cp:" + classesDirectoryUrl);
            urls.add(classesDirectoryUrl);
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Failed to convert classpath to URL.", e);
        }

        for (String path : (List<String>) project.getCompileSourceRoots()) {
            urls.add(convert(path));
        }

        if (sourceDirectories != null) {
            for (String path : sourceDirectories) {
                File dir = new File(path);
                if (!dir.exists()) {
                    // to fix empty class path ignore
                    dir.mkdirs();
                    if (getLog().isDebugEnabled()) {
                        getLog().debug("Create empty class path dir" + dir.getAbsolutePath());
                    }
                }
                urls.add(convert(path));
            }
        }

        List<Artifact> dependencies = project.getCompileArtifacts();
        for (Artifact artifact : dependencies) {
            URI uri = artifact.getFile().toURI();
            getLog().info("GWT cp:" + uri);
            try {
                urls.add(uri.toURL());
            } catch (MalformedURLException e) {
                throw new MojoExecutionException("Failed to convert classpath to URL.", e);
            }
        }
        if (usePluginDependencies != null) {
            List<String> patterns = Arrays.asList(usePluginDependencies.split(","));
            StrictPatternIncludesArtifactFilter filter = new StrictPatternIncludesArtifactFilter(patterns);
            for (Artifact artifact : pluginArtifacts) {
                if (filter.include(artifact)) {
                    URI uri = artifact.getFile().toURI();
                    getLog().info("GWT cp:" + uri);
                    try {
                        urls.add(uri.toURL());
                    } catch (MalformedURLException e) {
                        throw new MojoExecutionException("Failed to convert classpath to URL.", e);
                    }
                }
            }
        }

        getLog().info("java.io.tmpdir:" + System.getProperty("java.io.tmpdir"));

        return new URLClassLoader(urls.toArray(new URL[urls.size()]));
    }
}
