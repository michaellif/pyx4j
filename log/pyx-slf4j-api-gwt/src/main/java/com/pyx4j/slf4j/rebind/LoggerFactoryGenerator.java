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
 * Created on Dec 27, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.slf4j.rebind;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.ConfigurationProperty;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

public class LoggerFactoryGenerator extends Generator {

    private boolean logOnce = true;

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        try {
            PropertyOracle propertyOracle = context.getPropertyOracle();
            ConfigurationProperty classProp = propertyOracle.getConfigurationProperty("org.slf4j.ILoggerFactory");
            if ((classProp.getValues().size() > 0) && (classProp.getValues().get(0) != null)) {
                if (logOnce) {
                    logger.log(TreeLogger.INFO, "Use configured LoggerFactory class: " + classProp.getValues().get(0));
                    logOnce = false;
                }
                return classProp.getValues().get(0);
            } else {
                // Find the fist implementation
                TypeOracle oracle = context.getTypeOracle();
                JClassType factoryType = oracle.getType(org.slf4j.ILoggerFactory.class.getName());
                for (JClassType type : oracle.getTypes()) {
                    if ((type.isClass() != null) && type.isAssignableTo(factoryType) && (!type.isAbstract())) {
                        if (logOnce) {
                            logger.log(TreeLogger.INFO, "Use LoggerFactory class: " + type.getQualifiedSourceName());
                            logOnce = false;
                        }
                        return type.getQualifiedSourceName();
                    }
                }
                throw new RuntimeException("org.slf4j.ILoggerFactory implementations not found");
            }
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        } catch (BadPropertyValueException e) {
            throw new RuntimeException(e);
        }
    }

}
