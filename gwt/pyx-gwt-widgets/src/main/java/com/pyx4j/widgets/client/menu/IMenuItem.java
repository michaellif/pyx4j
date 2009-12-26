/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jun 4, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.menu;

import com.google.gwt.user.client.Element;

public interface IMenuItem {

    public Menu getParentMenu();

    public void setParentMenu(Menu parentMenu);

    public Element getElement();

    public void ensureDebugId(String string);

}
