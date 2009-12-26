/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on May 15, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.event.shared;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link BeforeCloseEvent} events.
 * 
 * @param <I>
 *            the type about to be closed
 */
public interface BeforeCloseHandler<I> extends EventHandler {

    /**
     * Called when {@link BeforeCloseEvent} is fired.
     * 
     * @param event
     *            the {@link BeforeCloseEvent} that was fired
     */
    void onBeforeClose(BeforeCloseEvent<I> event);
}
