/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jul 8, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

/*
 * Non Reflection Equal, e.g. in GWT
 */
public interface IEqual<E> {

    public boolean iequals(E other);
    
}
