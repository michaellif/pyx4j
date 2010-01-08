/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 18, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.serialization.client;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Used as marker for Generator
 */
public @interface RemoteServiceTarget {

    Class<? extends RemoteService> value();

}
