/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.annotations;

public @interface Table {

    /**
     * The name of the table or Entity kind for GAE.
     * 
     * Defaults to the entity name.
     */
    String name() default "";

    /**
     * Name prefix.
     */
    String prefix() default "";
}
