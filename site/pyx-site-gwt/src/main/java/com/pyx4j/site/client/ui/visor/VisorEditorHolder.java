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
 * Created on Mar 14, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.visor;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.ui.IPane;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

public class VisorEditorHolder extends AbstractVisorHolder {

    private static final I18n i18n = I18n.get(VisorEditorHolder.class);

    protected final Button btnApply;

    protected final Button btnSave;

    public VisorEditorHolder(final IVisorEditor visor, String caption, final IPane parent) {
        super(visor, caption, parent);

        btnSave = new Button(i18n.tr("Save"), new Command() {
            @Override
            public void execute() {
                visor.save(new AsyncCallback<VoidSerializable>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        //stay opened
                    }

                    @Override
                    public void onSuccess(VoidSerializable result) {
                        if (visor.onBeforeClose()) {
                            parent.hideVisor();
                        }
                    }
                });

            }
        });
        addFooterToolbarItem(btnSave);

        btnApply = new Button(i18n.tr("Apply"), new Command() {
            @Override
            public void execute() {
                visor.apply();
            }
        });
        addFooterToolbarItem(btnApply);

        Anchor btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                if (visor.onBeforeClose()) {
                    parent.hideVisor();
                }
            }
        });
        addFooterToolbarItem(btnCancel);

    }

}
