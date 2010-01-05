/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.domain;

public class Page extends PageContainer {

    public PageContainer parent;

    public PageData data = new PageData();

    public String name;

    public String caption;

    @Override
    public String toString() {
        return name;
    }
}
