/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.pyx4j.site.client.ui.wizard;

import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.ViewImplBase;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class WizardViewImplBase<E extends IEntity> extends ViewImplBase implements IWizardView<E> {

    private static final I18n i18n = I18n.get(WizardViewImplBase.class);

    private WizardForm<E> form;

    private IWizardView.Presenter presenter;

    public WizardViewImplBase() {
        super();
    }

    protected void setForm(WizardForm<? extends E> form) {
        if (getContentPane() == null) { // finalise UI here:
            setContentPane(new LayoutPanel());
            setSize("100%", "100%");
        }

        if (getForm() == form) {
            return; // already!?.
        }

        this.form = (WizardForm<E>) form;
        this.form.setParentView(this);
        this.form.initContent();

        LayoutPanel center = (LayoutPanel) getContentPane();
        center.clear(); // remove current form...

        center.add(this.form);

    }

    protected WizardForm<E> getForm() {
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
    public void setPresenter(IWizardView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IWizardView.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public E getValue() {
        return form.getValue();
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        if (caught instanceof UniqueConstraintUserRuntimeException) {
            showErrorDialog(caught.getMessage());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isDirty() {
        return form.isDirty();
    }

    protected void showErrorDialog(String message) {
        MessageDialog.error(i18n.tr("Error"), message);
    }

    protected void showValidationDialog() {
        MessageDialog.error(i18n.tr("Error"), form.getValidationResults().getMessagesText(true, true));
    }
}