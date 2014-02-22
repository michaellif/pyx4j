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
package com.propertyvista.portal.shared.ui;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class AbstractWizardFormView<E extends IEntity> extends AbstractPortalPanel implements IWizardView<E> {

    private static final I18n i18n = I18n.get(AbstractWizardFormView.class);

    private IWizardFormPresenter<E> presenter;

    private CPortalEntityWizard<E> wizardForm;

    public AbstractWizardFormView() {
        super();
    }

    public void setWizard(CPortalEntityWizard<E> wizardForm) {
        this.wizardForm = wizardForm;
        setWidget(wizardForm);
        if (wizardForm != null) {
            wizardForm.initContent();
        }
    }

    @Override
    public CPortalEntityWizard<E> getWizard() {
        return wizardForm;
    }

    @Override
    public void populate(E value) {
        if (wizardForm != null) {
            wizardForm.populate(value);
            wizardForm.calculateButtonsState();
        }
    }

    @Override
    public void reset() {
        if (wizardForm != null) {
            wizardForm.reset();
            wizardForm.calculateButtonsState();
        }
    }

    @Override
    public void setPresenter(IWizardFormPresenter<E> presenter) {
        this.presenter = presenter;
    }

    @Override
    public IWizardFormPresenter<E> getPresenter() {
        return presenter;
    }

    @Override
    public E getValue() {
        return wizardForm == null ? null : wizardForm.getValue();
    }

    @Override
    public boolean manageSubmissionFailure(Throwable caught) {
        if (caught instanceof UniqueConstraintUserRuntimeException) {
            MessageDialog.error(i18n.tr("Error"), caught.getMessage());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isDirty() {
        return wizardForm != null && wizardForm.isDirty();
    }
}