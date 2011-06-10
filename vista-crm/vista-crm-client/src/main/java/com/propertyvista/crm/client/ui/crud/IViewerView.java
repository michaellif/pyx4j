/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.site.rpc.AppPlace;

public interface IViewerView<E extends IEntity> extends IView<E> {

    public interface Presenter {

        public void populate();

        public void edit(Class<? extends AppPlace> editPlaceClass);
    }

    void setPresenter(Presenter presenter);

    public void populate(E value);
}
