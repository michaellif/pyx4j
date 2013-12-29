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
package com.pyx4j.site.client.ui.prime.wizard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class AbstractWizard<E extends IEntity> extends AbstractPrimePane implements IWizard<E> {

    private static final I18n i18n = I18n.get(AbstractWizard.class);

    private WizardForm<E> form;

    private IWizard.Presenter presenter;

    private final Button btnPrevious;

    private final Button btnNext;

    private final Button btnCancel;

    public AbstractWizard(String caption) {
        super();
        setCaption(caption);

        btnPrevious = new Button(i18n.tr("Previous"), new Command() {
            @Override
            public void execute() {
                form.previous();
                calculateButtonsState();
            }
        });
        addFooterToolbarItem(btnPrevious);

        btnNext = new Button(i18n.tr("Next"), new Command() {
            @Override
            public void execute() {
                if (form.isLast()) {
                    presenter.finish();
                } else {
                    form.next();
                    calculateButtonsState();
                }
            }
        });
        addFooterToolbarItem(btnNext);

        btnCancel = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                getPresenter().cancel();
            }
        });
        addFooterToolbarItem(btnCancel);

    }

    protected void setForm(WizardForm<E> form) {
        if (getContentPane() == null) { // finalise UI here:
            setContentPane(new LayoutPanel());
            setSize("100%", "100%");
        }

        if (getForm() == form) {
            return; // already!?.
        }

        this.form = form;
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
        calculateButtonsState();
    }

    @Override
    public void reset() {
        assert (form != null);
        form.reset();
        calculateButtonsState();
    }

    @Override
    public void setPresenter(IWizard.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IWizard.Presenter getPresenter() {
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

    protected void calculateButtonsState() {
        if (form.isLast()) {
            btnNext.setCaption(i18n.tr("Finish"));
        } else {
            btnNext.setCaption(i18n.tr("Next"));
        }

        btnPrevious.setEnabled(!form.isFirst());
    }

    @Override
    public void onStepChange() {
        calculateButtonsState();
    }

    public Button getBtnPrevious() {
        return btnPrevious;
    }

    public Button getBtnNext() {
        return btnNext;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public int getSelectedIndex() {
        return form.getSelectedIndex();
    }
}