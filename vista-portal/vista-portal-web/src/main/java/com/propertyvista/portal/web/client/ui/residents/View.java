/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-24
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.portal.web.client.ui.EntityView;

public interface View<E extends IEntity> extends EntityView<E> {

    interface Presenter<E extends IEntity> extends EntityView.EntityPresenter<E> {

        void edit(Key id);

        void back();
    }
}
