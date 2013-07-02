/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.dashboard;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEntityViewer;

import com.propertyvista.portal.web.client.themes.DashboardTheme;

public abstract class AbstractGadget<E extends IObject<?>> extends CEntityViewer<E> {

    public AbstractGadget() {
        asWidget().setStyleName(DashboardTheme.StyleName.Gadget.name());
    }
//
//    @Override
//    protected IDecorator<?> createDecorator() {
//        return new GadgetDecorator();
//    }
//
//    class GadgetDecorator extends FlowPanel implements IDecorator<CEntityViewer<?>> {
//
//        @Override
//        public void setComponent(CEntityViewer<?> viewer) {
//            add(viewer.createContent());
//        }
//
//        @Override
//        public void onSetDebugId(IDebugId parentDebugId) {
//            // TODO Auto-generated method stub
//
//        }
//
//    }
}
