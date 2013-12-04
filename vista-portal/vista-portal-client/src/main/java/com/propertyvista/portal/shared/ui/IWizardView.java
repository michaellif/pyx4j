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
package com.propertyvista.portal.shared.ui;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.client.IsView;

public interface IWizardView<E extends IEntity> extends IsView {

    public interface IWizardFormPresenter<E extends IEntity> {

        void submit();

        void cancel();

    }

    void setPresenter(IWizardFormPresenter<E> presenter);

    IWizardFormPresenter<E> getPresenter();

    void populate(E value);

    void reset();

    public E getValue();

    public boolean isDirty();

    /**
     * return true if error is handled by wizard itself
     */
    boolean manageSubmissionFailure(Throwable caught);

    void onStepChange();

}