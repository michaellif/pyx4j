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
 * Created on Jul 7, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.decorators;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

public class EditableEntityDecorator<E extends IEntity> extends FlowPanel implements IDecorator<CEntityForm<E>> {

    private static final I18n i18n = I18n.get(EditableEntityDecorator.class);

    private final Toolbar footerToolbar;

    private final SimplePanel componentHolder;

    protected final Button btnSave;

    protected final Button btnCancel;

    public EditableEntityDecorator() {
        super();

        componentHolder = new SimplePanel();
        add(componentHolder);

        SimplePanel footerToolbarHolder = new SimplePanel();
        footerToolbar = new Toolbar();
        footerToolbarHolder.setWidget(footerToolbar);
        add(footerToolbarHolder);

        btnSave = new Button(i18n.tr("Save"), new Command() {
            @Override
            public void execute() {

            }
        });
        footerToolbar.add(btnSave);

        btnCancel = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {

            }
        });
        footerToolbar.add(btnCancel);

    }

    @Override
    public void setComponent(CEntityForm<E> viewer) {
        componentHolder.setWidget(viewer.createContent().asWidget());
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub
    }

}
