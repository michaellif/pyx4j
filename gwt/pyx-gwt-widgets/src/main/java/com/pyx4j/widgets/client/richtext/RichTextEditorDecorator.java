/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author Michael
 * @version $Id: code-templates.xml 3267 2009-04-13 18:13:18Z vlads $
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
