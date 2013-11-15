/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PdfFormFieldMapping {

    public static class DefaultPdfFormatter implements Formatter {

        @Override
        public String format(Object object) {
            return object.toString();
        }

    }

    /**
     * Either name of the field, or multiple fields by following format:
     * 
     * <pre>
     * field1Name{field1Size},field2Name{field2Size},...,fieldNName{fieldNSize}
     * </pre>
     * 
     * The string provided by the property will be padded from left so it's size is <code>field1Size + field2Size + ... + fieldNSize</code>, but if the string
     * length overflows the sum of sizes of an exception will be thrown.
     * 
     * <br>
     * Formatter is applied before partitioning.
     */
    String value() default "";

}
