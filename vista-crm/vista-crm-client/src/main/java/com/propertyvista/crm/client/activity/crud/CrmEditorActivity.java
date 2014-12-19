/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-09-26
 * @author VladL
 */
package com.propertyvista.crm.client.activity.crud;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeEditorActivity;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView;
import com.pyx4j.site.rpc.CrudAppPlace;

public class CrmEditorActivity<E extends IEntity> extends AbstractPrimeEditorActivity<E> {

    public CrmEditorActivity(Class<E> entityClass, CrudAppPlace place, IPrimeEditorView<E> view, AbstractCrudService<E> service) {
        super(entityClass, place, view, service);
    }
}
