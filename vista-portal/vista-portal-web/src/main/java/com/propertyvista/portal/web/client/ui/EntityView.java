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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.shared.IEntity;

public interface EntityView<E extends IEntity> extends IsWidget {

    interface EntityPresenter<E extends IEntity> {

        void save(E value);

    }

    void setPresenter(EntityPresenter<E> presenter);

    void populate(E value);

}
