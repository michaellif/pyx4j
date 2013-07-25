/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-05
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.wizard;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.IPrimePane;
import com.pyx4j.site.client.ui.prime.misc.IMemento;
import com.pyx4j.site.client.ui.prime.misc.MementoImpl;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class VistaAbstractWizard<E extends IEntity> extends VistaWizardDecorator implements IPrimePane, IWizard<E> {

    private static final I18n i18n = I18n.get(VistaAbstractWizard.class);

    private final IMemento memento = new MementoImpl();

    private VistaWizardForm<E> form;

    private IWizard.Presenter presenter;

    private final Button btnPrevious;

    private final Button btnNext;

    private String endButtonCaption = i18n.tr("Finish");

    public VistaAbstractWizard(String caption) {
        super();
        setCaption(caption);

        Anchor btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                getPresenter().cancel();
            }
        });
        addFooterItem(btnCancel);

        btnPrevious = new Button(i18n.tr("Previous"), new Command() {
            @Override
            public void execute() {
                form.previous();
                calculateButtonsState();
            }
        });
        addFooterItem(btnPrevious);

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
        addFooterItem(btnNext);
    }

    protected void setForm(VistaWizardForm<E> form) {
        if (getForm() == form) {
            return; // already!?.
        }

        this.form = form;
        this.form.initContent();

        setContent(this.form.asWidget());
    }

    protected VistaWizardForm<E> getForm() {
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

    public void setEndButtonCaption(String endButtonCaption) {
        this.endButtonCaption = endButtonCaption;
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
            btnNext.setCaption(endButtonCaption);
        } else {
            btnNext.setCaption(i18n.tr("Next"));
        }

        btnPrevious.setEnabled(!form.isFirst());
    }

    @Override
    public void onStepChange() {
        setCaption(form.getSelectedStep().getStepTitle());
        calculateButtonsState();
    }

    @Override
    public IMemento getMemento() {
        return memento;
    }

    @Override
    public void storeState(Place place) {
        memento.setCurrentPlace(place);
    }

    @Override
    public void restoreState() {
    }
}