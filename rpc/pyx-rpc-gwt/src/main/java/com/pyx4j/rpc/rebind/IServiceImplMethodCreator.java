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
 * Created on Mar 11, 2011
 * @author michaellif
 */
package com.pyx4j.rpc.rebind;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.i18n.rebind.AbstractResource.ResourceList;
import com.google.gwt.i18n.shared.GwtLocale;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.AbstractGeneratorClassCreator;
import com.google.gwt.user.rebind.AbstractMethodCreator;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.rpc.client.ServiceExecutionInfo;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.ServiceExecution.OperationType;
import com.pyx4j.rpc.shared.ServiceQueueId;

public class IServiceImplMethodCreator extends AbstractMethodCreator {

    private final JClassType asyncCallbackType;

    private final JClassType iEntityType;

    private final Set<String> signatures = new HashSet<String>();

    public IServiceImplMethodCreator(AbstractGeneratorClassCreator classCreator, TypeOracle oracle) throws UnableToCompleteException {
        super(classCreator);
        try {
            asyncCallbackType = oracle.getType(AsyncCallback.class.getName());
            iEntityType = oracle.getType(IEntity.class.getName());
        } catch (NotFoundException e) {
            throw new UnableToCompleteException();
        }
    }

    @Override
    public void createMethodFor(TreeLogger logger, JMethod targetMethod, String key, ResourceList resourceList, GwtLocale locale)
            throws UnableToCompleteException {

        int signature = getMethodSignature(targetMethod);
        if (signatures.contains(targetMethod.getName() + signature)) {
            logger.log(Type.ERROR, "Duplicate method signature: " + currentCreator.getTarget().getName() + "." + targetMethod.getName());
            throw new UnableToCompleteException();
        }
        signatures.add(targetMethod.getName() + signature);

        ServiceExecution serviceExecution = targetMethod.getAnnotation(ServiceExecution.class);

        if (serviceExecution != null) {
            if (serviceExecution.cacheable()) {
                print("executeCacheable(");
            } else {
                print("executeWithExecutionInfo(");
            }

            print("new ");
            print(ServiceExecutionInfo.class.getSimpleName());
            print("(");
            OperationType operationType = serviceExecution.operationType();
            if (operationType == null) {
                operationType = OperationType.Transparent;
            }
            print(OperationType.class.getSimpleName());
            print(".");
            print(operationType.name());
            print(",");
            print(i18nEscapeSourceString(serviceExecution.waitCaption()));

            print(",");
            if (serviceExecution.queue() != null && serviceExecution.queue() != ServiceQueueId.class) {
                print(serviceExecution.queue().getName() + ".class");
            } else {
                print("null");
            }

            print("), ");
        } else {
            print("execute(");
        }
        print("\"");
        print(targetMethod.getName());
        print("\", ");
        print(String.valueOf(signature));

        if (targetMethod.getParameters().length == 0) {
            logger.log(Type.ERROR, "Should have at least one argument");
            throw new UnableToCompleteException();
        }

        JClassType classType = targetMethod.getParameters()[0].getType().isInterface();
        if (classType == null || !classType.isAssignableTo(asyncCallbackType)) {
            logger.log(Type.ERROR, "First parameter should be AsyncCallback");
            throw new UnableToCompleteException();
        }

        for (int i = 0; i < targetMethod.getParameters().length; i++) {
            print(", arg" + i);
        }

        println(");");
    }

    private int getMethodSignature(JMethod targetMethod) {
        int s = 0;
        for (int i = 0; i < targetMethod.getParameters().length; i++) {
            JType type = targetMethod.getParameters()[i].getType();
            if ((type instanceof JClassType) && (iEntityType.isAssignableFrom((JClassType) type))) {
                s = s * 31 + 123;
            } else {
                s = s * 31 + type.getSimpleSourceName().hashCode();
            }
        }
        return s;
    }

    static String escapeSourceString(String value) {
        if (value == null) {
            return "null";
        } else {
            return "\"" + Generator.escape(value) + "\"";
        }
    }

    static String i18nEscapeSourceString(String value) {
        String s = escapeSourceString(value);
        if (s.equals("\"\"") || s.equals("null")) {
            return "null";
        } else {
            return "i18n.tr(" + s + ")";
        }
    }
}
