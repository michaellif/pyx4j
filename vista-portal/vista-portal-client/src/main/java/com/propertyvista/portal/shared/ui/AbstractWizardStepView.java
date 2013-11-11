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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class AbstractWizardStepView<E extends IEntity> extends AbstractPortalPanel implements IWizardStepView<E> {

    private static final I18n i18n = I18n.get(AbstractWizardStepView.class);

    private IWizardStepPresenter<E> presenter;

    private CPortalEntityWizardStep<E> wizardStep;

    public AbstractWizardStepView() {
        super();
    }

    public void setWizardStep(CPortalEntityWizardStep<E> wizardStep) {
        this.wizardStep = wizardStep;
        setWidget(wizardStep);
        wizardStep.initContent();
    }

    @Override
    public void populate(E value) {
        wizardStep.populate(value);
    }

    @Override
    public void reset() {
        wizardStep.reset();
    }

    public void setPresenter(IWizardStepPresenter<E> presenter) {
        this.presenter = presenter;
    }

    @Override
    public IWizardStepPresenter<E> getPresenter() {
        return presenter;
    }

    @Override
    public E getValue() {
        return wizardStep.getValue();
    }

    @Override
    public boolean onSubmittionFail(Throwable caught) {
        if (caught instanceof UniqueConstraintUserRuntimeException) {
            MessageDialog.error(i18n.tr("Error"), caught.getMessage());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isDirty() {
        return wizardStep.isDirty();
    }
}