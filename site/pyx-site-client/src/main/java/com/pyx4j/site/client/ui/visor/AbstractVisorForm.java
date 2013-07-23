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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;

public abstract class AbstractVisorForm<E extends IEntity> extends AbstractVisorPane implements IVisorViewer<E> {

    private CEntityForm<E> form;

    public AbstractVisorForm(Controller controller) {
        super(controller);
    }

    @Override
    protected final void setContentPane(IsWidget widget) {
        throw new Error("Call setForm instead of calling setContentPane");
    }

    protected void setForm(CEntityForm<E> form) {

        this.form = form;
        this.form.initContent();

        super.setContentPane(new ScrollPanel(form.asWidget()));

    }

    protected CEntityForm<E> getForm() {
        return form;
    }

    @Override
    public void populate(E value) {
        assert (form != null);
        form.populate(value);
    }

    @Override
    public void reset() {
        setCaption(null);
        assert (form != null);
        form.reset();
    }

}
