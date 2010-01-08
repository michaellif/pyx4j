/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on May 14, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.ria.client;

public class ViewMemento {

    private int horizontalScrollPosition;

    private int verticalScrollPosition;

    public int getVerticalScrollPosition() {
        return verticalScrollPosition;
    }

    public void setVerticalScrollPosition(int position) {
        verticalScrollPosition = position;
    }

    public int getHorizontalScrollPosition() {
        return horizontalScrollPosition;
    }

    public void setHorizontalScrollPosition(int position) {
        horizontalScrollPosition = position;
    }

}
