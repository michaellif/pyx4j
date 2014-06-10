/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 10, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.layout.backoffice;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

public class InlineExtraHolder extends DockLayoutPanel {

    private final BackOfficeLayoutPanel parent;

    private final String extra1Caption;

    private final String extra2Caption;

    private boolean empty = true;

    public InlineExtraHolder(BackOfficeLayoutPanel parent, String extra1Caption, String extra2Caption) {
        super(Unit.PCT);
        this.parent = parent;
        this.extra1Caption = extra1Caption;
        this.extra2Caption = extra2Caption;
        setStylePrimaryName(BackOfficeLayoutTheme.StyleName.BackOfficeLayoutInlineExtraPanel.name());
    }

    public void layout() {
        clear();
        IsWidget extra1Widget = parent.getDisplay(DisplayType.extra1).getWidget();
        IsWidget extra2Widget = parent.getDisplay(DisplayType.extra2).getWidget();
        IsWidget extra3Widget = parent.getDisplay(DisplayType.extra3).getWidget();
        empty = false;
        if (extra1Widget != null) {
            if (extra2Widget != null) {
                addSouth(new ExtraPanel(extra2Caption, parent.getDisplay(DisplayType.extra2)), 40);
            }
            if (extra3Widget != null) {
                addSouth(new ExtraPanel(null, parent.getDisplay(DisplayType.extra3)), 30);
            }
            add(new ExtraPanel(extra1Caption, parent.getDisplay(DisplayType.extra1)));

        } else if (extra2Widget != null) {
            if (extra3Widget != null) {
                addSouth(new ExtraPanel(null, parent.getDisplay(DisplayType.extra3)), 40);
            }
            add(new ExtraPanel(extra2Caption, parent.getDisplay(DisplayType.extra2)));
        } else if (extra3Widget != null) {
            add(new ExtraPanel(null, parent.getDisplay(DisplayType.extra3)));
        } else {
            empty = true;
        }
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public void clear() {
        super.clear();
        empty = true;
    }

    class ExtraPanel extends FlowPanel {

        ExtraPanel(String caption, IsWidget widget) {
            if (caption != null && !caption.trim().equals("")) {
                HTML captionLabel = new HTML(caption);
                captionLabel.setStylePrimaryName(BackOfficeLayoutTheme.StyleName.BackOfficeLayoutInlineExtraPanelCaption.name());
                add(captionLabel);
            }
            add(widget);
        }
    }
}
