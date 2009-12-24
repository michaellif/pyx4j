/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 24, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.gae.domain;

public interface Job {

    Employer employer();

    String title();

    Float salary();

}
