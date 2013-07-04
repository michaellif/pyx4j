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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;

import com.propertyvista.portal.web.client.ui.EntityViewImpl;

public class ViewImpl<E extends IEntity> extends EntityViewImpl<E> implements View<E> {

    public ViewImpl() {
        this(null);

    }

    public ViewImpl(CEntityForm<E> form) {
        this(form, false, false);
    }

    public ViewImpl(boolean noEdit, boolean noBack) {
        this(null, noEdit, noBack);
    }

    public ViewImpl(CEntityForm<E> form, boolean noEdit, boolean noBack) {
        super(form);

    }

}
