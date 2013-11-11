/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.IsView;

import com.propertyvista.portal.shared.ui.IFormView.IFormPresenter;

public interface IWizardStepView<E extends IEntity> extends IsView {

    public interface IWizardStepPresenter<E extends IEntity> extends IFormPresenter<E> {

        void next();

        void previous();

        void cancel();

    }

    void setPresenter(IWizardStepPresenter<E> presenter);

    IWizardStepPresenter<E> getPresenter();

    void populate(E value);

    void reset();

    public E getValue();

    public boolean isDirty();

    boolean onSubmittionFail(Throwable caught);

}
