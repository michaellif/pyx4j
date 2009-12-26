/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 14, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tabpanel;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public interface ITab {

    String getTitle();

    AbstractImagePrototype getImagePrototype();

    Widget getContentPane();

}
