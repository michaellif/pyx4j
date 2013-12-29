/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.validation.framework;

import com.pyx4j.entity.core.IObject;

public class SimpleValidationFailure implements ValidationFailure {

    @SuppressWarnings("rawtypes")
    private final IObject property;

    private final String message;

    public SimpleValidationFailure(IObject property, String message) {
        this.property = property;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public IObject getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj instanceof SimpleValidationFailure) {
            return false;
        } else {
            SimpleValidationFailure other = (SimpleValidationFailure) obj;
            return (property.equals(other.property) & message.equals(other.message));
        }
    }

    @Override
    public int hashCode() {
        return property.hashCode() ^ message.hashCode();
    }

    @Override
    public String toString() {
        return getMessage();
    }

}
