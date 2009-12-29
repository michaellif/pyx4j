/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.env;

import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.entity.shared.ObjectFactory;

public class ConfigureTestsEnv {

    public static void configure() {
        ObjectFactory.setImplementation(new ClientEntityFactory());
    }

}
