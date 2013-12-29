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

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class AbstractVisorEditor<E extends IEntity> extends AbstractVisorForm<E> implements IVisorEditor<E> {

    private static final I18n i18n = I18n.get(AbstractVisorEditor.class);

    protected final Button btnApply;

    protected final Button btnSave;

    public AbstractVisorEditor(final IVisorEditor.Controller controller) {
        super(controller);

        btnSave = new Button(i18n.tr("Save"), new Command() {
            @Override
            public void execute() {
                save();
            }
        });
        addFooterToolbarItem(btnSave);

        btnApply = new Button(i18n.tr("Apply"), new Command() {
            @Override
            public void execute() {
                apply();
            }
        });
        addFooterToolbarItem(btnApply);

        Anchor btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                getController().hide();
            }
        });
        addFooterToolbarItem(btnCancel);

    }

    @Override
    public IVisorEditor.Controller getController() {
        return (IVisorEditor.Controller) super.getController();
    }

    private void apply() {
        if (!getForm().isValid()) {
            getForm().setUnconditionalValidationErrorRendering(true);
            showValidationDialog();
        } else {
            getController().apply();
        }
    }

    private void save() {
        if (!getForm().isValid()) {
            getForm().setUnconditionalValidationErrorRendering(true);
            showValidationDialog();
        } else {
            getController().save();
        }
    }

    @Override
    public E getValue() {
        return getForm().getValue();
    }

    @Override
    public boolean isDirty() {
        return getForm().isDirty();
    }

    protected void showValidationDialog() {
        MessageDialog.error(i18n.tr("Error"), getForm().getValidationResults().getValidationMessage(true, true, true));
    }
}
