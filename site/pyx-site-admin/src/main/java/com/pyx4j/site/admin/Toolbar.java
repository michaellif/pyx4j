/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 27, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin;

public class Toolbar extends com.pyx4j.ria.client.Toolbar {

    public Toolbar(final AdminApplication app) {

        addItem(ImageFactory.getImages().save(), app.getSaveCommand(), "Save");

    }
}
