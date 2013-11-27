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

public interface IViewerView<E extends IEntity> extends IsView {

    interface IViewerPresenter<E extends IEntity> {

    }

    void setPresenter(IViewerPresenter<E> presenter);

    IViewerPresenter<E> getPresenter();

    void populate(E value);

    void reset();
}
