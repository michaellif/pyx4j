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
package com.propertyvista.portal.web.client.ui;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class AbstractWizardView<E extends IEntity> extends AbstractPortalView implements IWizardView<E> {

    private static final I18n i18n = I18n.get(AbstractWizardView.class);

    private WizardPresenter<E> presenter;

    private AbstractPortalWizard<E> wizardForm;

    public AbstractWizardView() {
        super();
    }

    public void setWizard(AbstractPortalWizard<E> wizardForm) {
        this.wizardForm = wizardForm;
        setWidget(wizardForm);
        wizardForm.initContent();
    }

    @Override
    public void populate(E value) {
        wizardForm.populate(value);
        wizardForm.calculateButtonsState();
    }

    @Override
    public void reset() {
        wizardForm.reset();
        wizardForm.calculateButtonsState();
    }

    @Override
    public void setPresenter(WizardPresenter<E> presenter) {
        this.presenter = presenter;
    }

    @Override
    public WizardPresenter<E> getPresenter() {
        return presenter;
    }

    @Override
    public E getValue() {
        return wizardForm.getValue();
    }

    @Override
    public CEntityContainer<?> getCContainer() {
        return wizardForm;
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

    protected void showErrorDialog(String message) {
        MessageDialog.error(i18n.tr("Error"), message);
    }

    @Override
    public void onStepChange() {
        wizardForm.calculateButtonsState();
    }

    @Override
    public boolean isDirty() {
        return wizardForm.isDirty();
    }
}