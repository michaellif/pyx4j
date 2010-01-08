/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.validator;

import com.pyx4j.entity.shared.IObject;

public interface Validator {

    public boolean isValid(IObject<?, ?> object);

    //TODO
    //public String getValidationMessage(IObject<?, ?> object);
}
