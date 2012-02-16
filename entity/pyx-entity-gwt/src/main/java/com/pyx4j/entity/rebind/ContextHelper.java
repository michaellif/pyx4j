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
 * Created on 2010-11-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rebind;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;

class ContextHelper {

    final GeneratorContext context;

    final JClassType iPrimitiveInterfaceType;

    final JClassType iSetInterfaceType;

    final JClassType iListInterfaceType;

    final JClassType iEnentityInterfaceType;

    final JClassType iObjectInterfaceType;

    final JClassType iPrimitiveSetInterfaceType;

    final JClassType numberType;

    final boolean validateReservedKeywordsMembers;

    ContextHelper(TreeLogger logger, GeneratorContext context) throws NotFoundException, UnableToCompleteException {
        this.context = context;

        TypeOracle oracle = context.getTypeOracle();

        iObjectInterfaceType = oracle.getType(IObject.class.getName());
        iEnentityInterfaceType = oracle.getType(IEntity.class.getName());
        iPrimitiveInterfaceType = oracle.getType(IPrimitive.class.getName());
        iSetInterfaceType = oracle.getType(ISet.class.getName());
        iListInterfaceType = oracle.getType(IList.class.getName());
        iPrimitiveSetInterfaceType = oracle.getType(IPrimitiveSet.class.getName());
        numberType = oracle.getType(Number.class.getName());

        try {
            ConfigurationProperty prop = context.getPropertyOracle().getConfigurationProperty(EntityFactoryGenerator.CONFIG_VALIDATERESERVEDKEYWORDSMEMBERS);
            validateReservedKeywordsMembers = Boolean.valueOf(prop.getValues().get(0));
        } catch (BadPropertyValueException e) {
            logger.log(TreeLogger.ERROR, "The configuration property " + EntityFactoryGenerator.CONFIG_VALIDATERESERVEDKEYWORDSMEMBERS
                    + " was not defined. Is com.pyx4j.entity.Entity.gwt.xml inherited?");
            throw new UnableToCompleteException();
        }
    }

    boolean isInstantiabeEntity(JClassType type) {
        if (!(type.isAssignableTo(iEnentityInterfaceType) && (type.isInterface() != null) && iEnentityInterfaceType != type)) {
            return false;
        } else if ((type.getAnnotation(AbstractEntity.class) != null) && (!type.getAnnotation(AbstractEntity.class).generateMetadata())) {
            return false;
        } else {
            return true;
        }

    }

    boolean isEntityMember(JMethod method) {
        return (method.getReturnType() != JPrimitiveType.VOID) && (method.getParameters().length == 0);
    }

    boolean isNumber(JClassType valueClass) {
        return numberType == valueClass.getSuperclass();
    }

    List<JMethod> getAllEntityMethods(JClassType interfaceType, boolean forMetadata) {
        Set<String> uniqueNames = new HashSet<String>();
        List<JMethod> allMethods = new Vector<JMethod>();
        getAllEntityMethods(interfaceType, uniqueNames, allMethods, forMetadata);
        return allMethods;
    }

    void getAllEntityMethods(JClassType interfaceType, Set<String> uniqueNames, List<JMethod> allMethods, boolean forMetadata) {
        for (JMethod method : interfaceType.getMethods()) {
            if (!forMetadata && IEntity.PRIMARY_KEY.equals(method.getName())) {
                continue;
            }
            if (isEntityMember(method) && !uniqueNames.contains(method.getName())) {
                allMethods.add(method);
                uniqueNames.add(method.getName());
            }
        }
        for (JClassType impls : interfaceType.getImplementedInterfaces()) {
            if ((impls == iEnentityInterfaceType) || (impls == iObjectInterfaceType)) {
                continue;
            } else {
                getAllEntityMethods(impls, uniqueNames, allMethods, forMetadata);
            }
        }
    }
}
