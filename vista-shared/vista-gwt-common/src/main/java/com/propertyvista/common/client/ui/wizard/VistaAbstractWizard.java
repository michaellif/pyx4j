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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.IPrimePane;
import com.pyx4j.site.client.ui.prime.misc.IMemento;
import com.pyx4j.site.client.ui.prime.misc.MementoImpl;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.site.client.ui.visor.IVisor;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class VistaAbstractWizard<E extends IEntity> extends WizardDecorator<E> implements IPrimePane, IWizard<E> {

    private static final I18n i18n = I18n.get(VistaAbstractWizard.class);

    private final IMemento memento = new MementoImpl();

    private IWizard.Presenter presenter;

    public VistaAbstractWizard(String caption) {
        super();
        setCaption(caption);

    }

    @Override
    public void populate(E value) {
        assert (getForm() != null);
        getForm().populate(value);
        calculateButtonsState();
    }

    @Override
    public void reset() {
        assert (getForm() != null);
        getForm().reset();
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
        return getForm().getValue();
    }

    @Override
    protected void onFinish() {
        getPresenter().finish();
    };

    @Override
    protected void onCancel() {
        getPresenter().cancel();
    };

    @Override
    public boolean onSaveFail(Throwable caught) {
        if (caught instanceof UniqueConstraintUserRuntimeException) {
            showErrorDialog(caught.getMessage());
            return true;
        } else {
            return false;
        }
    }

    protected void showErrorDialog(String message) {
        MessageDialog.error(i18n.tr("Error"), message);
    }

    @Override
    public void onStepChange() {
        setCaption(getForm().getSelectedStep().getStepTitle());
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

    @Override
    public void showVisor(IVisor visor) {
        // TODO Auto-generated method stub
    }

    @Override
    public void hideVisor() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isVisorShown() {
        // TODO Auto-generated method stub
        return false;
    }
}