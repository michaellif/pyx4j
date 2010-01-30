/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 10, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.client;

import com.google.gwt.user.client.Command;

public class CommandLink extends LinkBarItem {

    public Command command;

    public CommandLink(String html, Command command) {
        super(html);
        this.command = command;
    }

}
