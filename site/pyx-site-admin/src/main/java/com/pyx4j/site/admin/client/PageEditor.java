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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.ria.client.AbstractView;
import com.pyx4j.site.shared.domain.Page;
import com.pyx4j.widgets.client.richtext.RichTextEditorDecorator;

public class PageEditor extends AbstractView {

    private final Page page;

    private final RichTextArea pageEditor;

    private final TextArea htmlViewer;

    public PageEditor(Page page) {
        super(new VerticalPanel(), page.uri().uri().getValue(), ImageFactory.getImages().image());
        this.page = page;
        VerticalPanel contentPane = (VerticalPanel) getContentPane();

        pageEditor = new RichTextArea();
        pageEditor.getElement().getStyle().setColor("black");

        RichTextEditorDecorator editorDecorator = new RichTextEditorDecorator(pageEditor);

        htmlViewer = new TextArea();
        htmlViewer.setSize("800px", "3000px");
        htmlViewer.setReadOnly(true);

        Button refreshButton = new Button("Refresh", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                htmlViewer.setText(pageEditor.getHTML());
            }
        });
        contentPane.add(editorDecorator);
        contentPane.add(htmlViewer);
        contentPane.add(refreshButton);

        populate();

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

    private void populate() {
        pageEditor.setHTML(page.data().html().getValue());
        htmlViewer.setText(page.data().html().getValue());
    }

    public Page getUpdatedPage() {
        page.data().html().setValue(HtmlCleanup.cleanup(pageEditor.getHTML()));
        return page;
    }
}
