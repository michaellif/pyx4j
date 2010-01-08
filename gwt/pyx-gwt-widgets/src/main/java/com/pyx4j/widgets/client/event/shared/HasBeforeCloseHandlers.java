/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on May 15, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.event.shared;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * A widget that implements this interface is a public source of {@link BeforeCloseEvent}
 * events.
 * 
 * @param <I>
 *            the type about to be closed
 */
public interface HasBeforeCloseHandlers<I> extends HasHandlers {
    /**
     * Adds a {@link BeforeCloseEvent} handler.
     * 
     * @param handler
     *            the handler
     * @return the registration for the event
     */
    HandlerRegistration addBeforeCloseHandler(BeforeCloseHandler<I> handler);
}
