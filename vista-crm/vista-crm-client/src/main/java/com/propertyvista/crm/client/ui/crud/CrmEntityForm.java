/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;
import com.pyx4j.site.client.backoffice.ui.prime.form.PrimeEntityForm;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;

public abstract class CrmEntityForm<E extends IEntity> extends PrimeEntityForm<E> {

    private final Class<E> rootClass;

    public CrmEntityForm(Class<E> rootClass, IFormView<E> view) {
        super(rootClass, new VistaEditorsComponentFactory(), view);
        this.rootClass = rootClass;
    }

    public Class<E> getRootClass() {
        return rootClass;
    }

}
