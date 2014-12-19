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
 */
package com.propertyvista.crm.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.annotations.AbstractEntity;

import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.crm.client.ui.gadgets.common.IGadgetFactory;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public class GadgetFactoryGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        String implName = null;
        try {
            JClassType factoryInterfaceType = context.getTypeOracle().getType(typeName);
            List<Class<? extends GadgetInstanceBase<?>>> gadgetImplClasses = getGadgetImplClasses(logger, context.getTypeOracle());

            if (gadgetImplClasses.isEmpty()) {
                logger.log(Type.WARN, "the gadget factory generator hasn't found any gadgets");
            } else {
                logger.log(Type.DEBUG, "generating gadget factory for the following gadgets: " + toString(gadgetImplClasses));
            }

            String implSimpleName = (factoryInterfaceType.getSimpleSourceName() + "Impl").replaceFirst("^I", "");
            PrintWriter pw = context.tryCreate(logger, factoryInterfaceType.getPackage().getName(), implSimpleName);
            if (pw != null) {
                ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(factoryInterfaceType.getPackage().getName(), implSimpleName);
                composer.addImplementedInterface(IGadgetFactory.class.getName());
                composer.addImport(RuntimeException.class.getName());
                composer.addImport(GadgetInstanceBase.class.getName());
                composer.addImport(GadgetMetadata.class.getName());

                for (Class<? extends GadgetInstanceBase<?>> klass : gadgetImplClasses) {
                    composer.addImport(klass.getName());
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
                for (Class<? extends GadgetInstanceBase<?>> gadgetClass : gadgetImplClasses) {
                    Class<? extends GadgetMetadata> gadgetMetadataClass = getGadgetMetadataClass(logger, gadgetClass);
                    w.indent();
                    w.println("} else if (metadata.getInstanceValueClass().equals(%s.class)) {", gadgetMetadataClass.getName());
                    w.indentln("gadget = new %s((%s) metadata);", gadgetClass.getSimpleName(), gadgetMetadataClass.getName());
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
                w.println("}");

                w.outdent();
                w.commit(logger);
            }
            implName = factoryInterfaceType.getPackage().getName() + "." + implSimpleName;
        } catch (NotFoundException e) {
            logger.log(Type.ERROR, e.toString());
            throw new UnableToCompleteException();
        }

        return implName;

    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    private List<Class<? extends GadgetInstanceBase<?>>> getGadgetImplClasses(TreeLogger logger, TypeOracle typeOracle) throws UnableToCompleteException {
        JClassType baseGadgetType = typeOracle.findType(GadgetInstanceBase.class.getName());
        List<Class<? extends GadgetInstanceBase<?>>> gadgetImplClasses = new ArrayList<Class<? extends GadgetInstanceBase<?>>>();
        ClassLoader classLoader = getClass().getClassLoader();
        for (JClassType type : typeOracle.getTypes()) {
            if (baseGadgetType.isAssignableFrom(type) & !type.isAbstract()) {
                try {
                    gadgetImplClasses.add((Class<? extends GadgetInstanceBase<?>>) classLoader.loadClass(type.getQualifiedSourceName()));
                } catch (ClassNotFoundException e) {
                    logger.log(Type.ERROR, "failed to load gadget implementation class '" + type.getQualifiedSourceName() + "'", e);
                }
            }
        }

        return gadgetImplClasses;
    }

    private static String toString(List<Class<? extends GadgetInstanceBase<?>>> classList) {
        StringBuilder builder = new StringBuilder();
        for (Class<?> klass : classList) {
            builder.append(klass.getSimpleName()).append(' ');
        }
        return builder.toString();
    }
}
