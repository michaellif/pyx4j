/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Oct 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.domain;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;

public interface Country extends IObject<Country> {

    IPrimitive<String, Country> name();

}
