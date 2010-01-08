/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jul 8, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

/*
 * Non Reflection Cloneable, e.g. in GWT
 */
public interface ICloneable<E> {

    public E iclone();
}
