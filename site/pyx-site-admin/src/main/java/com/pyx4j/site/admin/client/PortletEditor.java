/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Apr 28, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.admin.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.AbstractView;
import com.pyx4j.site.shared.domain.Portlet;
import com.pyx4j.widgets.client.richtext.RichTextEditorDecorator;

public class PortletEditor extends AbstractView {

    private final Portlet portlet;

    public PortletEditor(Portlet portlet) {
        super(new VerticalPanel(), portlet.capture().getValue(), ImageFactory.getImages().image());
        this.portlet = portlet;
        VerticalPanel contentPane = (VerticalPanel) getContentPane();

        final TextBox pageNameTextBox = new TextBox();

        final RichTextArea pageEditor = new RichTextArea();

        RichTextEditorDecorator editorDecorator = new RichTextEditorDecorator(pageEditor);

        final TextArea htmlViewer = new TextArea();

        contentPane.add(pageNameTextBox);
        contentPane.add(editorDecorator);
        contentPane.add(htmlViewer);

        // Focus the cursor on the name field when the app loads
        pageNameTextBox.setFocus(true);
        pageNameTextBox.selectAll();

    }

    @Override
    public Widget getFooterPane() {
        return new Label("FooterPane" + getTitle());
    }

    @Override
    public MenuBar getMenu() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Widget getToolbarPane() {
        // TODO Auto-generated method stub
        return null;
    }

    void simple() {

    }
}
