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
 * @version $Id$
 */
package com.pyx4j.rpc.rebind;

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

public class IServiceImplMethodCreator extends AbstractMethodCreator {

    private final JClassType asyncCallbackType;

    private final JClassType iEntityType;

    public IServiceImplMethodCreator(AbstractGeneratorClassCreator classCreator, TypeOracle oracle) throws UnableToCompleteException {
        super(classCreator);
        try {
            asyncCallbackType = oracle.getType(AsyncCallback.class.getName());
            iEntityType = oracle.getType("com.pyx4j.entity.shared.IEntity");
        } catch (NotFoundException e) {
            throw new UnableToCompleteException();
        }
    }

    @Override
    public void createMethodFor(TreeLogger logger, JMethod targetMethod, String key, ResourceList resourceList, GwtLocale locale)
            throws UnableToCompleteException {

        int signature = getMethodSignature(targetMethod);

        print("execute(");
        print("\"");
        print(currentCreator.getTarget().getQualifiedSourceName());
        print("\", \"");
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

}
