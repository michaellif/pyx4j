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
 */
package com.pyx4j.i18n.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.StrictPatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.xml.sax.SAXException;

import com.pyx4j.i18n.extractor.ConstantEntry;
import com.pyx4j.i18n.extractor.Extractor;
import com.pyx4j.i18n.gettext.POCatalog;
import com.pyx4j.i18n.gettext.POEntry;
import com.pyx4j.i18n.gettext.POFile;
import com.pyx4j.i18n.gettext.POFileWriter;
import com.pyx4j.i18n.translate.GoogleTranslate;
import com.pyx4j.scanner.DirectoryScanner;
import com.pyx4j.scanner.JarFileScanner;
import com.pyx4j.scanner.Scanner;
import com.pyx4j.scanner.ScannerEntry;

/**
 * Extracts i18n text from classes dependencies.
 *
 */
@Mojo(name = "extract", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
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
     */
    @Parameter
    public List<String> excludes = null;

    /**
     * Dependencies to include.
     * The artifact syntax is defined by AbstractStrictPatternArtifactFilter.
     *
     */
    @Parameter
    public List<String> includes = null;

    /**
     * Dependency artifacts scope: "compile", test", "runtime" or "system";
     */
    @Parameter(defaultValue = "compile")
    public String scope;

//TODO  public List<String> classesInclude;

//TODO    public List<String> classesExclude;

    /**
     * Filename of the created .pot file
     */
    @Parameter(defaultValue = "keys.pot")
    public String keysFile;

    /**
     * Corrections filename translations are equal to text
     *
     */
    @Parameter(defaultValue = "corrections.pot")
    public String correctionKeysFile;

    /**
     * PO directory
     */
    @Parameter(defaultValue = "${basedir}/src/main/resources/translations")
    public File poDirectory;

    public enum Sort {

        none,

        byText,

        byFile
    }

    /**
     * Set output sorting: none, byText, byFile
     *
     */
    @Parameter(defaultValue = "byText")
    public Sort poSort = Sort.byText;

    /**
     * Set output page width
     */
    @Parameter(defaultValue = "78")
    public int poPageWidth = 78;

    /**
     * Break long message lines, longer than the output page width, into several lines
     */
    @Parameter(defaultValue = "true")
    public boolean poWrapLines = true;

    /**
     * Add UTF-8 BOM to the file
     */
    @Parameter(defaultValue = "false")
    public boolean writeBomTokeysFile = false;

    /**
     * Write source code location
     */
    @Parameter(defaultValue = "true")
    public boolean poSaveLocation = true;

    /**
     * Write 'java-format' as Extracted Comment (transifex.net bug)
     */
    @Parameter(defaultValue = "true")
    public boolean javaFormatFlagAsComment = true;

    /**
     * File to save extracted strings, you may use it to run spell checker.
     */
    @Parameter(defaultValue = "${project.build.directory}/i18n.txt")
    public File extractedStrings;

    /**
     * Java Source language
     */
    @Parameter(defaultValue = "en")
    public String sourceLanguage = "en";

    /**
     * Enable auto translate.
     */
    @Parameter(property = "i18n.autoTranslate", defaultValue = "false")
    public boolean autoTranslate = false;

    /**
     * merge translation catalog
     */
    @Parameter(property = "i18n.merge", defaultValue = "false")
    public boolean merge = false;

    /**
     * Use translation catalog for auto translate and merge.
     */
    @Parameter(property = "i18n.catalog")
    private File translationCatalog;

    /**
     * Used for auto translate.
     */
    @Parameter(defaultValue = "${google.translate.apiKey}")
    public String googleApiKey;

    /**
     * Use Google Translate to create translation for languages
     */
    @Parameter
    public List<String> translates = null;

    /**
     * Use Google Translate to create translation for languages
     * Comma separated list of languages to translate or merge.
     */
    @Parameter
    public String translate = null;

    /**
     * The directory containing generated classes.
     */
    @Parameter(readonly = true, required = true, defaultValue = "${project.build.outputDirectory}")
    private File classesDirectory;

    /**
     * The Maven project.
     */
    @Component
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
        if (merge && autoTranslate) {
            throw new MojoExecutionException("Unsupported parameters;  use <merge> or <autoTranslate>");
        }
        if (translates != null && translate != null) {
            throw new MojoExecutionException("Unsupported parameters;  use <translates> or <translate>");
        }
        if (translate != null) {
            if (translate.contains(",")) {
                translates = Arrays.asList(translate.split(","));
            } else {
                translates = new ArrayList<String>();
                translates.add(translate);
            }
        }

        if (getLog().isDebugEnabled()) {
            POCatalog.debug = true;
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

        Extractor extractor = new Extractor();
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

        if (classesDirectory.exists()) {
            processClassesDirectory(extractor);
        }

        extractor.complete();

        POFile po = writePOFile(extractor);
        writeTextFile(extractor);
        if (merge) {
            mergeTranslationCatalog(po);
        } else if (autoTranslate && (translates != null) && (this.googleApiKey != null) && (this.googleApiKey.length() > 0)) {
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

    private void processArtifact(Extractor extractor, Artifact artifact) throws MojoExecutionException {
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
                if (!source.isDirectory()) {
                    if (source.getName().endsWith(".class")) {
                        InputStream in = source.getInputStream();
                        try {
                            extractor.readClass(in);
                        } finally {
                            IOUtils.closeQuietly(in);
                        }
                    } else if (source.getName().endsWith(".html")) {
                        InputStream in = source.getInputStream();
                        try {
                            extractor.readXMLFile(in, source.getName());
                        } catch (SAXException e) {
                            getLog().info("file pars error " + e.getMessage());
                            getLog().info("ignore rest of the file " + source.getName());
                        } finally {
                            IOUtils.closeQuietly(in);
                        }
                    }
                }
            }
            getLog().debug("processed  " + artifact + "; classes " + count);
        } catch (IOException e) {
            throw new MojoExecutionException("Error scanning dependency artifact " + artifact, e);
        } catch (AnalyzerException e) {
            throw new MojoExecutionException("Error reading dependency artifact " + artifact, e);
        } finally {
            IOUtils.closeQuietly(scanner);
        }
    }

    private void processClassesDirectory(Extractor extractor) throws MojoExecutionException {
        getLog().debug("processing " + classesDirectory);
        Scanner scanner = null;
        try {
            scanner = new DirectoryScanner(classesDirectory);
            int count = 0;
            for (ScannerEntry source : scanner.getEntries()) {
                if (!source.isDirectory()) {
                    if (source.getName().endsWith(".class")) {
                        InputStream in = source.getInputStream();
                        try {
                            extractor.readClass(in);
                        } finally {
                            IOUtils.closeQuietly(in);
                        }
                    } else if (source.getName().endsWith(".html")) {
                        InputStream in = source.getInputStream();
                        try {
                            extractor.readXMLFile(in, source.getName());
                        } catch (SAXException e) {
                            getLog().info("file pars error " + e.getMessage());
                            getLog().info("ignore rest of the file " + source.getName());
                        } finally {
                            IOUtils.closeQuietly(in);
                        }
                    }
                }
            }
            getLog().debug("processed  " + classesDirectory + "; classes " + count);
        } catch (IOException e) {
            throw new MojoExecutionException("Error scanning classesDirectory " + classesDirectory, e);
        } catch (AnalyzerException e) {
            throw new MojoExecutionException("Error reading classesDirectory " + classesDirectory, e);
        } finally {
            IOUtils.closeQuietly(scanner);
        }

    }

    private POFile writePOFile(Extractor extractor) throws MojoExecutionException {
        POFile po = new POFile();
        po.createDefaultHeader();

        Pattern javaFormatPattern = Pattern.compile(".*\\{\\d(,.*)*\\}.*", Pattern.DOTALL);

        for (ConstantEntry entry : extractor.getConstants()) {
            POEntry pe = new POEntry();
            pe.untranslated = entry.text;
            pe.context = entry.context;

            if (entry.comments != null) {
                for (String line : entry.comments) {
                    pe.addExtractedComment(line);
                }
            }

            if (poSaveLocation) {
                pe.references = entry.reference;
                Collections.sort(pe.references);
            }

            boolean javaFormat = false;
            if (entry.javaFormatFlag) {
                pe.addFlag("java-format");
                javaFormat = true;
            } else {
                Matcher m = javaFormatPattern.matcher(pe.untranslated);
                if (m.matches()) {
                    pe.addFlag("java-format");
                    javaFormat = true;
                }
            }

            if (javaFormat && javaFormatFlagAsComment) {
                pe.addExtractedComment("java-format");
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
        writePO(po, new File(poDirectory, keysFile), writeBomTokeysFile, false);
        getLog().info("Extracted " + po.entries.size() + " strings for i18n");

        //Create file for corrections
        if ((correctionKeysFile != null) && (correctionKeysFile.length() > 0)) {
            POFile poc = po.cloneForTranslation();
            for (POEntry entry : poc.entries) {
                entry.translated = entry.untranslated;
            }
            writePO(poc, new File(poDirectory, correctionKeysFile), writeBomTokeysFile, true);
        }

        return po;
    }

    private void writePO(POFile po, File file, boolean writeBom, boolean correctionsFile) throws MojoExecutionException {
        try {
            POFileWriter poWriter = new POFileWriter();
            poWriter.pageWidth = poPageWidth;
            poWriter.wrapLines = poWrapLines;
            poWriter.writeBom = writeBom;

            if (correctionsFile) {
                poWriter.duplicateReferencesAsComments = true;
            }

            poWriter.write(file, po);
        } catch (IOException e) {
            throw new MojoExecutionException("POFile " + file.getAbsolutePath() + " write error", e);
        }
    }

    private void writeTextFile(Extractor extractor) throws MojoExecutionException {
        PrintWriter writer = null;
        try {
            if (!extractedStrings.getParentFile().isDirectory()) {
                if (!extractedStrings.getParentFile().mkdirs()) {
                    throw new MojoExecutionException("Unable to create directory " + extractedStrings.getParentFile());
                }
            }
            writer = new PrintWriter(extractedStrings, "UTF-8");
            List<String> text = new Vector<String>();
            for (ConstantEntry entry : extractor.getConstants()) {
                text.add(entry.text);
            }
            Collections.sort(text);
            for (String line : text) {
                writer.println(line);
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
        for (String lang : translates) {
            GoogleTranslate gt = new GoogleTranslate(this.googleApiKey);

            POCatalog catalog = new POCatalog(lang, true);
            if (translationCatalog != null) {
                if (!translationCatalog.canRead()) {
                    throw new MojoExecutionException("Can't read translationCatalog '" + translationCatalog.getAbsolutePath() + "'");
                }
                catalog.loadCatalog(translationCatalog);
            }
            POFile poTransl = po.cloneForTranslation();
            getLog().info(
                    "Translating " + sourceLanguage + " -> " + lang + " ; entries to translate " + poTransl.entries.size() + ", use Local catalog "
                            + catalog.size());
            int translated = 0;
            int apiCalls = 0;
            for (POEntry entry : poTransl.entries) {
                entry.translated = catalog.translate(entry.context, entry.untranslated);
                //TODO add proper java format handling
                if (((entry.translated == null) || (entry.translated.length() == 0)) && (!entry.contanisFlag("java-format"))) {
                    try {
                        entry.translated = gt.translate(entry.untranslated, sourceLanguage, lang);
                        if (entry.translated == null) {
                            getLog().info("Can't translate \"" + entry.untranslated + "\"");
                        }
                    } catch (Throwable e) {
                        throw new MojoExecutionException("translate error", e);
                    }
                    catalog.update(entry.context, entry.untranslated, entry.translated);
                    apiCalls++;
                }
                translated++;
            }
            getLog().info("Translated:" + translated + "; api calls:" + apiCalls);
            if (apiCalls > 0) {
                catalog.write();
            }
            writePO(poTransl, new File(poDirectory, lang + ".po"), true, false);
        }
    }

    private void mergeTranslationCatalog(POFile po) throws MojoExecutionException {
        if (translationCatalog == null) {
            throw new MojoExecutionException("translationCatalog required");
        }
        if (!translationCatalog.canRead()) {
            throw new MojoExecutionException("Can't read translationCatalog '" + translationCatalog.getAbsolutePath() + "'");
        }
        for (String lang : translates) {
            POCatalog catalog = new POCatalog(lang, false);
            catalog.loadCatalog(translationCatalog);
            POFile poTransl = po.cloneForTranslation();
            getLog().info("Translating " + sourceLanguage + " -> " + lang);
            int translated = 0;
            int notTranslated = 0;
            for (POEntry entry : poTransl.entries) {
                entry.translated = catalog.translate(entry.context, entry.untranslated);
                if (entry.translated == null) {
                    notTranslated++;
                } else {
                    translated++;
                }
            }
            getLog().info("Translated:" + translated + "; Not Translated:" + notTranslated);
            writePO(poTransl, new File(poDirectory, lang + ".po"), true, false);
        }
    }
}
