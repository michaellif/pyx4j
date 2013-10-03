/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.IsView;

public interface IWizardView<E extends IEntity> extends IsView {

    public interface IWizardPresenter<E extends IEntity> {

        //TODO rename to Submit
        void finish();

        void cancel();

    }

    void setPresenter(IWizardPresenter<E> presenter);

    IWizardPresenter<E> getPresenter();

    void populate(E value);

    void reset();

    public E getValue();

    public boolean isDirty();

    //TODO rename to onSubmittionFailed
    boolean onSaveFail(Throwable caught);

    void onStepChange();

}