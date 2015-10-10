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
 * Created on Dec 13, 2011
 * @author vlads
 */
package com.pyx4j.config.rebind;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.config.client.ClientApplicationVersion;

/**
 * Code generator for implementations of {@link ClientApplicationVersion}.
 */
public class ClientApplicationVersionGenerator extends Generator {

    /**
     * Configuration properties.
     */
    public static final String PROJECT_VERSION = "pyx.compileTimeSystemProperty.project.version";

    public static final String PATCH_NUMBER = "pyx.compileTimeSystemProperty.patch.number";

    public static final String BRANCH_NAME_SHORT = "pyx.compileTimeSystemProperty.branch.name.short";

    public static final String BUILD_NUMBER = "pyx.compileTimeSystemProperty.build.number";

    public static final String BUILD_TIME = "pyx.compileTimeSystemProperty.build.time";

    public static final String BUILD_TIME_FORMAT = "pyx.compileTimeSystemProperty.build.timeFormat";

    public static final String BUILD_FORCE_RPC_VERSION = "pyx.build.forceRPCVersion";

    public static final String SCM_REVISION = "pyx.compileTimeSystemProperty.scm.revision";

    private static final String PYX_BUILD_PROPERTIES_FILE = "com/pyx4j/config/generated/pyx-build.version.properties";

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle oracle = context.getTypeOracle();
        JClassType interfaceType;
        try {
            interfaceType = oracle.getType(typeName);
        } catch (NotFoundException e) {
            logger.log(TreeLogger.ERROR, "Unexpected error: " + e.getMessage(), e);
            throw new UnableToCompleteException();
        }

        String productVersion = getConfigurationProperty(logger, context, PROJECT_VERSION);
        if ((productVersion != null) && productVersion.endsWith("-SNAPSHOT")) {
            productVersion = productVersion.substring(0, productVersion.indexOf("-SNAPSHOT"));
        }
        String patchNumber = getConfigurationProperty(logger, context, PATCH_NUMBER);
        if (patchNumber != null && patchNumber.startsWith("${")) {
            patchNumber = "";
        }
        String branchNameShort = getConfigurationProperty(logger, context, BRANCH_NAME_SHORT);
        if (branchNameShort != null && branchNameShort.startsWith("${")) {
            branchNameShort = "";
        }
        String buildNumber = getConfigurationProperty(logger, context, BUILD_NUMBER);
        if (buildNumber != null && buildNumber.startsWith("${")) {
            buildNumber = "n/a";
        }

        String buildLabel = productVersion + patchNumber + branchNameShort + "." + buildNumber;
        String buildTime = getConfigurationProperty(logger, context, BUILD_TIME);
        String buildFromat = getConfigurationProperty(logger, context, BUILD_TIME_FORMAT);
        String scmRevision = getConfigurationProperty(logger, context, SCM_REVISION);

        boolean forceRPCVersion = false;
        if (CommonsStringUtils.isStringSet(buildLabel)) {
            ConfigurationProperty prop;
            try {
                prop = context.getPropertyOracle().getConfigurationProperty(BUILD_FORCE_RPC_VERSION);
            } catch (BadPropertyValueException e) {
                logger.log(TreeLogger.ERROR,
                        "The configuration property " + BUILD_FORCE_RPC_VERSION + " was not defined. Is com.pyx4j.config.Config.gwt.xml inherited?");
                throw new UnableToCompleteException();
            }
            String value = prop.getValues().get(0);
            if (CommonsStringUtils.isStringSet(value)) {
                forceRPCVersion = Boolean.valueOf(value);
            }
        }

        String pyxScmRevision = "";
        {
            Properties properties = new Properties();
            // find the resource in the classPath
            URL url = Thread.currentThread().getContextClassLoader().getResource(PYX_BUILD_PROPERTIES_FILE);
            if (url != null) {
                InputStream is = null;
                try {
                    is = url.openStream();
                    properties.load(is);
                } catch (IOException e) {
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ignore) {
                            is = null;
                        }
                    }
                }
            }
            pyxScmRevision = properties.getProperty("scm.revision", "");
            if ("${scm.revision}".equals(pyxScmRevision)) {
                pyxScmRevision = "";
            }
        }

        String implName = interfaceType.getName();
        implName = implName.replace('.', '_') + "Impl";

        String packageName = interfaceType.getPackage().getName();
        PrintWriter printWriter = context.tryCreate(logger, packageName, implName);

        if (printWriter != null) {
            ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(packageName, implName);
            factory.setSuperclass(interfaceType.getQualifiedSourceName());
            if (forceRPCVersion) {
                factory.addImplementedInterface(Serializable.class.getName());
            }
            factory.addImport(Date.class.getName());
            SourceWriter writer = factory.createSourceWriter(context, printWriter);

            writer.println();
            writer.println("public final String getBuildLabel() {");
            writer.indent();
            writer.print("return \"");
            writer.print(buildLabel);
            writer.println("\";");
            writer.outdent();
            writer.println("}");
            writer.println();

            writer.println();
            writer.println("/* " + new Date() + "*/");
            writer.println("public final Date getBuildDate() {");
            writer.indent();
            writer.print("return new Date(");
            long buildDate = System.currentTimeMillis();
            if (CommonsStringUtils.isStringSet(buildTime) && CommonsStringUtils.isStringSet(buildFromat)) {
                try {
                    buildDate = new SimpleDateFormat(buildFromat).parse(buildTime).getTime();
                } catch (Throwable ignore) {
                    logger.log(TreeLogger.ERROR, "buildTime " + buildTime + " format " + buildFromat + " error");
                    throw new UnableToCompleteException();
                }
            }
            writer.print(String.valueOf(buildDate));
            writer.println("L);");
            writer.outdent();
            writer.println("}");
            writer.println();

            writer.println();
            writer.println("public final String getScmRevision() {");
            writer.indent();
            writer.print("return \"");
            writer.print(scmRevision);
            writer.println("\";");
            writer.outdent();
            writer.println("}");
            writer.println();

            writer.println();
            writer.println("public final String getPyxScmRevision() {");
            writer.indent();
            writer.print("return \"");
            writer.print(pyxScmRevision);
            writer.println("\";");
            writer.outdent();
            writer.println("}");
            writer.println();

            if (forceRPCVersion) {
                logger.log(TreeLogger.INFO, "Force new RPC version");
                writer.println();
                writer.print("public String buildId");
                writer.print(buildLabel.replace('-', '_').replace('.', '_').replace(' ', '_').trim());
                writer.print(";");
                writer.println();
            }

            writer.commit(logger);
        }

        return packageName + "." + implName;
    }

    private String getConfigurationProperty(TreeLogger logger, GeneratorContext context, String propertyName) throws UnableToCompleteException {
        try {
            ConfigurationProperty prop = context.getPropertyOracle().getConfigurationProperty(propertyName);
            String value = prop.getValues().get(0);
            if (CommonsStringUtils.isStringSet(value)) {
                return System.getProperty(value, "");
            } else {
                return "";
            }
        } catch (BadPropertyValueException e) {
            logger.log(TreeLogger.ERROR, "The configuration property " + propertyName + " was not defined. Is com.pyx4j.config.Config.gwt.xml inherited?");
            throw new UnableToCompleteException();
        }

    }
}
