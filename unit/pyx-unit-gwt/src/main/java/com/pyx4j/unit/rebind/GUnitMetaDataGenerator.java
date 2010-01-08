/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 21, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.rebind;

import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.unit.client.impl.AbstractGCaseMeta;
import com.pyx4j.unit.client.impl.AbstractTestSuiteMetaData;

public class GUnitMetaDataGenerator extends Generator {

    private final String BASE_TEST_CLASS_NAME = "junit.framework.TestCase";

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle oracle = context.getTypeOracle();
        try {
            JClassType interfaceType = oracle.getType(typeName);
            String packageName = interfaceType.getPackage().getName();
            String simpleName = interfaceType.getSimpleSourceName() + "_Impl";
            ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
            composer.setSuperclass(AbstractTestSuiteMetaData.class.getName());
            composer.addImport(BASE_TEST_CLASS_NAME);
            composer.addImport(AbstractGCaseMeta.class.getName());
            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return interfaceType.getParameterizedQualifiedSourceName() + "_Impl";
            }

            JClassType caseType = oracle.getType(BASE_TEST_CLASS_NAME);

            List<JClassType> cases = new Vector<JClassType>();

            for (JClassType type : oracle.getTypes()) {
                if ((type.isClass() != null) && type.isAssignableTo(caseType)) {
                    cases.add(type);
                    logger.log(TreeLogger.Type.DEBUG, "GUnit case:" + type.getName());
                }
            }

            if (cases.size() == 0) {
                logger.log(TreeLogger.Type.WARN, "No GUnit cases found");
            }
            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            writeImpl(writer, simpleName, cases);
            writer.commit(logger);
            return composer.getCreatedClassName();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    // Example of created code

    //    public class MyGUnitMetaData_Impl extends com.pyx4j.gunit.impl.AbstractTestSuiteMetaData {
    //        
    //        public MyGUnitMetaData_Impl() { 
    //          super();
    //          
    //          addCase(com.pyx4j.tester.client.tests.MyGUnitTest.class, new  AbstractGCaseMeta(MyGUnitTest.class, "testServiceEcho") {
    //    
    //              protected TestCase setUp() throws Exception {
    //                 return new com.pyx4j.tester.client.tests.MyGUnitTest();
    //              }
    //    
    //              protected void run(TestCase instance) throws Exception {
    //                  ((com.pyx4j.tester.client.tests.MyGUnitTest)instance).testServiceEcho();
    //              }
    //          });    
    //    
    //        }
    //    }    

    private void writeImpl(SourceWriter writer, String simpleName, List<JClassType> cases) {
        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();
        writer.println("super();");

        for (JClassType c : cases) {
            writer.println();
            StringBuilder caseClass = new StringBuilder();
            caseClass.append(c.getPackage().getName()).append('.');
            caseClass.append(c.getName());

            for (JMethod method : c.getMethods()) {
                if (method.getReturnType() != JPrimitiveType.VOID) {
                    continue;
                } else if (method.getParameters().length != 0) {
                    continue;
                } else if (!method.getName().startsWith("test")) {
                    continue;
                }

                writer.print("addCase(");
                writer.print(caseClass.toString());
                writer.print(".class, new ");
                writer.print(AbstractGCaseMeta.class.getSimpleName());
                writer.print("(" + caseClass.toString() + ".class , ");
                writer.println("\"" + method.getName() + "\") {");

                //protected GCase setUp() throws Exception;
                writer.println();
                writer.indent();
                writer.println("protected TestCase createTestCase() throws Exception {");

                writer.indent();
                writer.print("return new ");
                writer.print(caseClass.toString());
                writer.println("();");
                writer.outdent();

                writer.println("}");
                writer.outdent();

                //protected void run(GCase instance) throws Exception;

                writer.println();
                writer.indent();
                writer.println("protected void run(TestCase instance) throws Exception {");

                writer.indent();
                writer.print("((");
                writer.print(caseClass.toString());
                writer.print(")instance).");
                writer.print(method.getName());
                writer.println("();");
                writer.outdent();

                writer.println("}");
                writer.outdent();

                writer.println("});");
                writer.println();
            }
        }

        writer.outdent();
        writer.println("}");
        writer.outdent();

    }

}
