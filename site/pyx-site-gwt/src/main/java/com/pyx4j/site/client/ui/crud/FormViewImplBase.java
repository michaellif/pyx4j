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
 * Created on Jun 13, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.crud;

import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.ui.ViewImplBase;

public class FormViewImplBase<E extends IEntity> extends ViewImplBase implements IFormView<E> {

    private CrudEntityForm<E> form;

    public FormViewImplBase() {
        super();
    }

    /*
     * Should be called by descendant upon initialisation.
     */
    @SuppressWarnings("unchecked")
    protected void setForm(CrudEntityForm<? extends E> form) {

        if (getContentPane() == null) { // finalise UI here:
            setContentPane(new LayoutPanel());
            setSize("100%", "100%");
        }

        if (this.form == form) {
            return; // already!?.
        }

        this.form = (CrudEntityForm<E>) form;
        this.form.setParentView(this);
        this.form.initContent();

        LayoutPanel center = (LayoutPanel) getContentPane();
        center.clear(); // remove current form...

        center.add(this.form);

    }

    protected CrudEntityForm<E> getForm() {
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

    @Override
    public void setActiveTab(int index) {
        if (index >= 0) {
            assert (form != null);
            form.setActiveTab(index);
        }
    }

    @Override
    public int getActiveTab() {
        assert (form != null);
        return form.getActiveTab();
    }

}
