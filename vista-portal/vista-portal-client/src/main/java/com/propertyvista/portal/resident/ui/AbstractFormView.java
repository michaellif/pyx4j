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
package com.propertyvista.portal.resident.ui;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;

public class AbstractFormView<E extends IEntity> extends AbstractPortalPanel implements IFormView<E> {

    protected static final I18n i18n = I18n.get(AbstractFormView.class);

    private IFormViewPresenter<E> presenter;

    private CPortalEntityForm<E> form;

    public AbstractFormView() {
        super();
    }

    protected void setForm(final CPortalEntityForm<E> form) {
        this.form = form;
        if (form == null) {
            setWidget(null);
        } else {
            form.initContent();
            setWidget(form.asWidget());
        }
    }

    @Override
    public void setPresenter(IFormViewPresenter<E> presenter) {
        this.presenter = presenter;
        form.reset();
    }

    @Override
    public IFormViewPresenter<E> getPresenter() {
        return presenter;
    }

    @Override
    public void populate(E value) {
        form.populate(value);
    }

    public CPortalEntityForm<E> getForm() {
        return form;
    }

    @Override
    public void reset() {
        getForm().reset();
    }

}
