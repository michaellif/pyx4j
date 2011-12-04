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
 * Created on 2011-03-11
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import com.pyx4j.rpc.client.IServiceBase;
import com.pyx4j.rpc.client.ServiceExecutionInfo;
import com.pyx4j.rpc.shared.ServiceExecution;

public class IServiceGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext genCtx, String typeName) throws UnableToCompleteException {
        TypeOracle oracle = genCtx.getTypeOracle();
        JClassType interfaceType;
        try {
            interfaceType = oracle.getType(typeName);
        } catch (NotFoundException e) {
            logger.log(TreeLogger.ERROR, "Unexpected error: " + e.getMessage(), e);
            throw new UnableToCompleteException();
        }

        String implName = interfaceType.getName();
        implName = implName.replace('.', '_') + "Impl";

        String packageName = interfaceType.getPackage().getName();
        PrintWriter printWriter = genCtx.tryCreate(logger, packageName, implName);

        if (printWriter != null) {
            ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(packageName, implName);
            factory.addImport(IServiceBase.class.getName());
            factory.addImport(ServiceExecutionInfo.class.getName());
            factory.addImport(ServiceExecution.OperationType.class.getName().replace('$', '.'));
            factory.setSuperclass(IServiceBase.class.getName());
            factory.addImplementedInterface(interfaceType.getQualifiedSourceName());
            SourceWriter sourceWriter = factory.createSourceWriter(genCtx, printWriter);

            IServiceImplCreator implCreator = new IServiceImplCreator(sourceWriter, interfaceType, oracle);
            implCreator.emitClass(logger, null);

            genCtx.commit(logger, printWriter);
        }

        return packageName + "." + implName;
    }

}
