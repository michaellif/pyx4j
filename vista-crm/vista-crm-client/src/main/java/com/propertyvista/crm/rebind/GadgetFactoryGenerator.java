/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rebind;

import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.annotations.AbstractEntity;

import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.crm.client.ui.gadgets.impl.ArrearsGadget;
import com.propertyvista.crm.client.ui.gadgets.impl._GadgetPackageMarker;
import com.propertyvista.crm.client.ui.gadgets.impl.demo._DemoGadgetPackageMarker;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class GadgetFactoryGenerator extends Generator {

    // TODO just for testing, use classpath scanning for the implementations of gadgets
    private static final Class<? extends GadgetInstanceBase<?>>[] GADGET_IMPLS = (Class<? extends GadgetInstanceBase<?>>[]) new Class<?>[] {//@formatter:off
            ArrearsGadget.class,
    };//@formatter:on

    private List<Class<? extends GadgetInstanceBase<?>>> getGadgetImplClasses(TreeLogger logger, List<JPackage> gadgetPackages)
            throws UnableToCompleteException {
        if (false) {
            return Arrays.<Class<? extends GadgetInstanceBase<?>>> asList(GADGET_IMPLS);
        } else {
            List<Class<? extends GadgetInstanceBase<?>>> xs = new ArrayList<Class<? extends GadgetInstanceBase<?>>>();
            for (JPackage jpackage : gadgetPackages) {
                for (JType type : jpackage.getTypes()) {

                    Class<? extends GadgetInstanceBase<?>> klass = null;
                    try {
                        klass = ((Class<? extends GadgetInstanceBase<?>>) GadgetFactoryGenerator.this.getClass().getClassLoader()
                                .loadClass(type.getQualifiedSourceName()));
                    } catch (ClassNotFoundException e) {
                        logger.log(Type.ERROR, e.toString());
                        throw new UnableToCompleteException();
                    }
                    if (GadgetInstanceBase.class.isAssignableFrom(klass) & !Modifier.isAbstract(klass.getModifiers())) {
                        xs.add(klass);
                    }

                }
            }
            if (xs.isEmpty()) {
                logger.log(Type.WARN, "the gadget factory generator hasn't found any gadgets");
            }
            return xs;
        }
    }

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        String implName = null;
        try {
            JClassType type = context.getTypeOracle().getType(typeName);
            List<JPackage> gadgetsPackages = new ArrayList<JPackage>();
            gadgetsPackages.add(context.getTypeOracle().getPackage(_GadgetPackageMarker.class.getPackage().getName()));
            if (_DemoGadgetPackageMarker.ENABLE_DEMO_GADGETS) {
                gadgetsPackages.add(context.getTypeOracle().getPackage(_DemoGadgetPackageMarker.class.getPackage().getName()));
            }

            logger.log(Type.INFO, "generating gadget factory for the following gadgets: " + getGadgetImplClasses(logger, gadgetsPackages).toString());

            String implSimpleName = type.getSimpleSourceName() + "Impl";
            PrintWriter pw = context.tryCreate(logger, type.getPackage().getName(), implSimpleName);
            if (pw != null) {
                ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(type.getPackage().getName(), implSimpleName);
                composer.addImplementedInterface(IGadgetFactory.class.getName());
                composer.addImport(RuntimeException.class.getName());
                composer.addImport(GadgetInstanceBase.class.getName());
                composer.addImport(GadgetMetadata.class.getName());

                for (Class<? extends GadgetInstanceBase<?>> klass : getGadgetImplClasses(logger, gadgetsPackages)) {
                    composer.addImport(klass.getName());
                    composer.addImport(getGadgetMetadataClass(logger, klass).getName());
                }
                SourceWriter w = composer.createSourceWriter(context, pw);

                w.indent();
                w.println();
                w.println("public " + implSimpleName + "() {}");
                w.println();

                w.println("@Override public GadgetInstanceBase<?> createGadget(GadgetMetadata metadata) throws Error {");
                w.indent();
                w.println("GadgetInstanceBase<?> gadget = null;");
                w.println("if (metadata == null) {");
                w.indentln("return null;");
                for (Class<? extends GadgetInstanceBase<?>> gadgetClass : getGadgetImplClasses(logger, gadgetsPackages)) {
                    Class<? extends GadgetMetadata> gadgetMetadataClass = getGadgetMetadataClass(logger, gadgetClass);
                    w.indent();
                    w.println("} else if (metadata.getInstanceValueClass().equals(%s.class)) {", gadgetMetadataClass.getSimpleName());
                    w.indentln("gadget = new %s((%s)metadata);", gadgetClass.getSimpleName(), gadgetMetadataClass.getSimpleName());
                    composer.addImport(gadgetClass.getName());
                    w.outdent();
                }
                w.println("}");
                w.println();
                w.println("if (gadget == null) {");
                w.indentln("throw new RuntimeException(\"Gadget Implemenation for '\" + metadata.getInstanceValueClass().getName() + \"' was not found!\");");
                w.println("} else {"); // if statement
                w.indentln("gadget.initView();");
                w.indentln("return gadget;");
                w.println("}");
                w.outdent();
                w.println("}"); // gadget implementation

                // TODO remove this method 
                w.println();
                w.println("@Override public Class<? extends GadgetMetadata> getGadgetMetadataClass() {");
                w.indentln("return null;");
                w.println("}");
                w.outdent();
                w.commit(logger);
            }
            implName = type.getPackage().getName() + "." + implSimpleName;
        } catch (NotFoundException e) {
            logger.log(Type.ERROR, e.toString());
            throw new UnableToCompleteException();
        }

        return implName;

    }

    private static Class<? extends GadgetMetadata> getGadgetMetadataClass(TreeLogger logger, Class<? extends GadgetInstanceBase<?>> gadgetClass)
            throws UnableToCompleteException {
        Class<? extends GadgetMetadata> gadgetMetadataClass = null;
        try {
            gadgetMetadataClass = (Class<? extends GadgetMetadata>) gadgetClass.getConstructors()[0].getParameterTypes()[0];

        } catch (Throwable e) {
            logger.log(Type.ERROR, "unable to find constructor or first parameter: constructor of gadget implementation '" + gadgetClass.getName()
                    + "' MUST accept one and only parameter that type extends GadgetMetadata");
            throw new UnableToCompleteException();
        }

        if (gadgetMetadataClass.getAnnotation(AbstractEntity.class) != null) {
            logger.log(Type.ERROR, SimpleMessageFormat.format(
                    "the constructor for class \"{0}\" must accept a CONCRETE IMPLEMENTATION of a GadgetMetadata, howerver it uses \"{1}\"",
                    gadgetClass.getName(), gadgetMetadataClass.getName()));
            throw new UnableToCompleteException();
        }

        return gadgetMetadataClass;
    }
}
