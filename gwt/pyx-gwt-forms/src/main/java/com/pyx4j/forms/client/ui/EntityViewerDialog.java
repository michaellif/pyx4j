/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jul 19, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.widgets.client.dialog.OkDialog;

public class EntityViewerDialog extends OkDialog {

    public EntityViewerDialog(IObject<?> entity) {
        super("Entity Viewer");

        FlowPanel panel = new FlowPanel();

        EntityViewerCellTree tree = new EntityViewerCellTree(entity);
        tree.setSize("100%", "100%");
        panel.add(tree);

        ScrollPanel scroll = new ScrollPanel(panel);
        LayoutPanel content = new LayoutPanel();
        content.setHeight("100%");
        content.add(scroll);
        setBody(content);

        setDialogPixelWidth(400);

    }

    @Override
    public boolean onClickOk() {
        return true;
    }
}