/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
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
