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
package com.propertyvista.portal.web.client.ui;

import com.pyx4j.entity.shared.IEntity;

public interface IEditorView<E extends IEntity> extends IFormView<E> {

    public interface IEditorPresenter<E extends IEntity> extends IFormViewPresenter<E> {

        void edit();

        void save();

        void populate();
    }

    @Override
    IEditorPresenter<E> getPresenter();

    public E getValue();

    public boolean isDirty();

    boolean onSaveFail(Throwable caught);

    void setEditable(boolean flag);

    boolean isEditable();

}
