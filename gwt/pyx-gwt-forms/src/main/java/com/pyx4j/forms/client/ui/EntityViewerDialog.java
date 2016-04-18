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
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.ScrollPanel;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.widgets.client.dialog.OkDialog;

public class EntityViewerDialog extends OkDialog {

    public EntityViewerDialog(IObject<?> entity) {
        super("Entity Viewer");
        setDialogPixelWidth(400);

        FlowPanel panel = new FlowPanel();
        panel.add(new EntityViewerCellTree(entity));

        ScrollPanel scroll = new ScrollPanel(panel);
        scroll.setHeight("500px");

        setBody(scroll);

    }

    @Override
    public boolean onClickOk() {
        return true;
    }
}