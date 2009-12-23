/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.domain;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;

public interface Department extends IObject<Department> {

    IPrimitive<String, Department> name();

}
