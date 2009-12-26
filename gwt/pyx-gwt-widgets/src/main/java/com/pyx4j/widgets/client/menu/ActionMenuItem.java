/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jun 4, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.menu;

import com.google.gwt.user.client.Command;

public class ActionMenuItem extends LabelMenuItem {

    private Command command;

    public ActionMenuItem(String text, Command cmd) {
        this(text, false, cmd);
        setCommand(cmd);
    }

    public ActionMenuItem(String text, boolean asHTML, Command cmd) {
        super(text, asHTML);
        setCommand(cmd);
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command cmd) {
        command = cmd;
    }

}