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
package com.pyx4j.site.client.ui.crud;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;

public class ViewerViewImplBase<E extends IEntity> extends ViewImplBase<E> implements IViewerView<E> {

    private static I18n i18n = I18nFactory.getI18n(ViewerViewImplBase.class);

    protected Presenter presenter;

    public ViewerViewImplBase() {
        super();
    }

    public ViewerViewImplBase(Widget header, double size) {
        super(header, size);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
