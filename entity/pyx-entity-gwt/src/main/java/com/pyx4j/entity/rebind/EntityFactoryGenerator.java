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
 * Created on Dec 29, 2009
 * @author vlads
 */
package com.pyx4j.entity.rebind;

import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.RpcBlacklistCheck;

import com.pyx4j.entity.annotations.GwtBlacklist;
import com.pyx4j.entity.annotations.RpcBlacklist;
import com.pyx4j.entity.client.AbstractClientEntityFactoryImpl;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.meta.EntityMeta;

public class EntityFactoryGenerator extends Generator {

    /**
     * Configuration property.
     */
    public static final String CONFIG_VALIDATERESERVEDKEYWORDSMEMBERS = "pyx.validateReservedKeywordsMembers";

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        TypeOracle oracle = context.getTypeOracle();
        try {
            JClassType interfaceType = oracle.getType(typeName);
            String packageName = interfaceType.getPackage().getName();
            String simpleName = EntityHandlerWriter.implClassName(interfaceType.getName());
            ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);

            composer.setSuperclass(AbstractClientEntityFactoryImpl.class.getName());
            composer.addImport(IEntity.class.getName());
            composer.addImport(IObject.class.getName());

            PrintWriter printWriter = context.tryCreate(logger, composer.getCreatedPackage(), composer.getCreatedClassShortName());
            if (printWriter == null) {
                // the generated type already exists
                return interfaceType.getParameterizedQualifiedSourceName() + IEntity.SERIALIZABLE_IMPL_CLASS_SUFIX;
            }

            //TODO, this does not work!
            if (oracle.findType(RemoteService.class.getName() + "_TypeSerializer") != null) {
                logger.log(TreeLogger.Type.WARN, "RemoteService serializer already created! IEntity generated implementations would not be serializable. "
                        + "Call ClientEntityFactory.ensureIEntityImplementations(); in your code first");
            }

            RpcBlacklistCheck rpcFilter = new RpcBlacklistCheck(logger, context.getPropertyOracle());

            ContextHelper contextHelper = new ContextHelper(logger, context);

            List<JClassType> cases = new Vector<JClassType>();

            int validationErrors = 0;
            for (JClassType type : oracle.getTypes()) {
                if (contextHelper.isInstantiabeEntity(type)) {
                    if (type.isAnnotationPresent(GwtBlacklist.class)) {
                        continue;
                    }
                    cases.add(type);
                    logger.log(TreeLogger.Type.DEBUG, "Creating IEntity:" + type.getName());

                    if (type.isAnnotationPresent(RpcBlacklist.class) && rpcFilter.isAllowed(type)) {
                        throw new RuntimeException("IEntity class :" + type.getPackage().getName() + "." + type.getName() + " should be in rpc.blacklist");
                    }

                    EntityHandlerWriter.createEntityHandlerImpl(logger, contextHelper, type);
                    validationErrors += EntityMetaWriter.createEntityMetaImpl(logger, contextHelper, type);
                }
            }

            if (validationErrors > 0) {
                logger.log(TreeLogger.Type.WARN, "There are " + validationErrors + " validation errors in IEntity declaration");
                throw new UnableToCompleteException();
            }

            if (cases.size() == 0) {
                logger.log(TreeLogger.Type.WARN, "No IEntity implementations found");
            } else {
                logger.log(TreeLogger.Type.DEBUG, "Adding " + cases.size() + " IEntity generated implementations");
            }
            composer.addImport(EntityMeta.class.getName());
            SourceWriter writer = composer.createSourceWriter(context, printWriter);
            writeEntityFactoryImplImpl(writer, simpleName, cases);
            writer.commit(logger);
            return composer.getCreatedClassName();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeEntityFactoryImplImpl(SourceWriter writer, String simpleName, List<JClassType> interfaceClasses) {
        writer.println();
        writer.indent();
        writer.println("public " + simpleName + "() { ");
        writer.indent();
        writer.println("super();");

        for (JClassType interfaceType : interfaceClasses) {
            writer.println();

            writer.print("addClassFactory(");
            writer.print(interfaceType.getQualifiedSourceName());
            writer.print(".class, ");
            writer.println("new IEntityFactoryImpl() {");

            writer.indent();
            writer.println("@SuppressWarnings(\"unchecked\")");
            writer.println("@Override");
            writer.println("public <T extends IEntity> T create(Class<T> clazz, IObject<?> parent, String fieldName){");

            writer.indent();
            writer.print("return (T)new ");
            writer.print(interfaceType.getPackage().getName());
            writer.print(".");
            writer.print(EntityHandlerWriter.implClassName(interfaceType.getName()));
            writer.println("(parent, fieldName);");
            writer.outdent();

            writer.println("}");

            writer.println();
            writer.println("@Override");
            writer.println("public EntityMeta createEntityMeta(Class<? extends IEntity> clazz){");

            writer.indent();
            writer.print("return new ");
            writer.print(interfaceType.getPackage().getName());
            writer.print(".");
            writer.print(EntityMetaWriter.metaImplClassName(interfaceType.getName()));
            writer.println("();");
            writer.outdent();

            writer.println("}");

            writer.outdent();

            writer.println("});");
        }

        writer.outdent();
        writer.println("}");
        writer.outdent();
    }

}
