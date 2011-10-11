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
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.StrictPatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import com.pyx4j.i18n.extractor.ConstantEntry;
import com.pyx4j.i18n.extractor.ConstantExtractor;
import com.pyx4j.i18n.gettext.POCatalog;
import com.pyx4j.i18n.gettext.POEntry;
import com.pyx4j.i18n.gettext.POFile;
import com.pyx4j.i18n.gettext.POFileWriter;
import com.pyx4j.i18n.translate.GoogleTranslate;
import com.pyx4j.scanner.DirectoryScanner;
import com.pyx4j.scanner.JarFileScanner;
import com.pyx4j.scanner.Scanner;
import com.pyx4j.scanner.ScannerEntry;

import edu.emory.mathcs.backport.java.util.Collections;

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
    public String scope;

//TODO  public List<String> classesInclude;

//TODO    public List<String> classesExclude;

    /**
     * Filename of the created .pot file
     * 
     * @parameter expression="keys.pot"
     */
    public String keysFile;

    /**
     * PO directory
     * 
     * @parameter expression="${basedir}/src/main/resources/translations"
     */
    public File poDirectory;

    public enum Sort {

        none,

        byText,

        byFile
    }

    /**
     * Set output sorting: none, byText, byFile
     * 
     * @parameter default-value="byText"
     */
    public Sort poSort = Sort.byText;

    /**
     * Set output page width
     * 
     * @parameter default-value="78"
     */
    public int poPageWidth = 78;

    /**
     * Break long message lines, longer than the output page width, into several lines
     * 
     * @parameter default-value="true"
     */
    public boolean poWrapLines = true;

    /**
     * Write source code location
     * 
     * @parameter default-value="true"
     */
    public boolean poSaveLocation = true;

    /**
     * File to save extracted strings, you may use it to run spell checker.
     * 
     * @parameter expression="${project.build.directory}/i18n.txt"
     */
    public File extractedStrings;

    /**
     * Java Source language
     * 
     * @parameter default-value="en"
     */
    public String sourceLanguage = "en";

    /**
     * Enable auto translate.
     * 
     * @parameter expression="${i18n.autoTranslate}" default-value="false"
     */
    public boolean autoTranslate = false;

    /**
     * Used for auto translate.
     * 
     * @parameter expression="${google.translate.apiKey}"
     */
    public String googleApiKey;

    /**
     * Use Google Translate to create translation for languages
     * 
     * @parameter
     */
    public List<String> autoTranslates = null;

    /**
     * The directory containing generated classes.
     * 
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private File classesDirectory;

    /**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    public MavenProject project;

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

        processClassesDirectory(extractor);

        extractor.analyzeTranslatableHierarchy();

        POFile po = writePOFile(extractor);
        writeTextFile(extractor);
        if (autoTranslate && (autoTranslates != null) && (this.googleApiKey != null) && (this.googleApiKey.length() > 0)) {
            autoTranslate(po);
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

        File file = getArtifactFile(artifact);
        Scanner scanner = null;
        try {
            if (file.isDirectory()) {
                scanner = new DirectoryScanner(file);
            } else {
                scanner = new JarFileScanner(file);
            }
            int count = 0;
            for (ScannerEntry source : scanner.getEntries()) {
                if (!source.isDirectory() && source.getName().endsWith(".class")) {
                    InputStream in = source.getInputStream();
                    try {
                        extractor.readClass(in);
                        count++;
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                }
            }
            getLog().debug("processed  " + artifact + "; classes " + count);
        } catch (IOException e) {
            throw new MojoExecutionException("Error sacanning dependency artifact " + artifact, e);
        } catch (AnalyzerException e) {
            throw new MojoExecutionException("Error reading dependency artifact " + artifact, e);
        } finally {
            IOUtils.closeQuietly(scanner);
        }
    }

    private void processClassesDirectory(ConstantExtractor extractor) throws MojoExecutionException {
        getLog().debug("processing " + classesDirectory);
        Scanner scanner = null;
        try {
            scanner = new DirectoryScanner(classesDirectory);
            int count = 0;
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
            getLog().debug("processed  " + classesDirectory + "; classes " + count);
        } catch (IOException e) {
            throw new MojoExecutionException("Error sacanning classesDirectory " + classesDirectory, e);
        } catch (AnalyzerException e) {
            throw new MojoExecutionException("Error reading classesDirectory " + classesDirectory, e);
        } finally {
            IOUtils.closeQuietly(scanner);
        }

    }

    private POFile writePOFile(ConstantExtractor extractor) throws MojoExecutionException {
        POFile po = new POFile();
        po.createDefaultHeader();

        Pattern javaFormatPattern = Pattern.compile(".*\\{\\d(,.*)*\\}.*", Pattern.DOTALL);

        for (ConstantEntry entry : extractor.getConstants()) {
            POEntry pe = new POEntry();
            pe.untranslated = entry.text;

            if (poSaveLocation) {
                pe.references = entry.reference;
                Collections.sort(pe.references);
            }

            if (entry.javaFormatFlag) {
                pe.addFlag("java-format");
            } else {
                Matcher m = javaFormatPattern.matcher(pe.untranslated);
                if (m.matches()) {
                    pe.addFlag("java-format");
                }
            }

            po.entries.add(pe);
        }

        if ((poSort != null) && (poSort != Sort.none)) {
            Collections.sort(po.entries, new POEntry.ByTextComparator());
        }

        if (!poDirectory.isDirectory()) {
            if (!poDirectory.mkdirs()) {
                throw new MojoExecutionException("Unable to create poDirectory " + poDirectory);
            }
        }
        writePO(po, new File(poDirectory, keysFile));
        getLog().info("Extracted " + po.entries.size() + " strings for i18n");
        return po;
    }

    private void writePO(POFile po, File file) throws MojoExecutionException {
        try {
            POFileWriter poWriter = new POFileWriter();
            poWriter.pageWidth = poPageWidth;
            poWriter.wrapLines = poWrapLines;

            poWriter.write(file, po);
        } catch (IOException e) {
            throw new MojoExecutionException("POFile " + file.getAbsolutePath() + " write error", e);
        }
    }

    private void writeTextFile(ConstantExtractor extractor) throws MojoExecutionException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(extractedStrings, "UTF-8");
            for (ConstantEntry entry : extractor.getConstants()) {
                writer.println(entry.text);
                writer.println();
            }
            writer.flush();
        } catch (IOException e) {
            throw new MojoExecutionException("text file " + extractedStrings.getAbsolutePath() + " write error", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private void autoTranslate(POFile po) throws MojoExecutionException {
        for (String lang : autoTranslates) {
            GoogleTranslate gt = new GoogleTranslate(this.googleApiKey);

            POCatalog catalog = new POCatalog(lang);
            POFile poTransl = po.cloneForTranslation();
            getLog().info("Translating " + sourceLanguage + " -> " + lang);
            int translated = 0;
            int apiCalls = 0;
            for (POEntry entry : poTransl.entries) {
                entry.translated = catalog.translate(entry.untranslated);
                //TODO add proper java format handling
                if ((entry.translated == null) && (!entry.contanisFlag("java-format"))) {
                    try {
                        entry.translated = gt.translate(entry.untranslated, sourceLanguage, lang);
                    } catch (Throwable e) {
                        throw new MojoExecutionException("translate error", e);
                    }
                    catalog.update(entry.untranslated, entry.translated);
                    apiCalls++;
                }
                translated++;
            }
            getLog().info("Translated " + translated + "; api calls:" + apiCalls);
            catalog.write();
            writePO(poTransl, new File(poDirectory, lang + ".po"));
        }
    }
}
