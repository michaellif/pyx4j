/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;

public interface IEditorView<E extends IEntity> extends IsWidget {

    public interface Presenter {

        public void populate();

        public void save();

        public void cancel();
    }

    void setPresenter(Presenter presenter);

    public void populate(E value);

    public E getValue();
}
