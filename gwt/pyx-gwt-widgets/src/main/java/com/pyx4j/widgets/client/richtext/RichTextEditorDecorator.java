/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.richtext;

import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RichTextEditorDecorator extends VerticalPanel {

    public RichTextEditorDecorator(RichTextArea textArea) {

        textArea.setWidth("100%");

        RichTextToolbar toolbar = new RichTextToolbar(textArea);
        toolbar.setWidth("100%");

        add(toolbar);
        add(textArea);
    }

}
