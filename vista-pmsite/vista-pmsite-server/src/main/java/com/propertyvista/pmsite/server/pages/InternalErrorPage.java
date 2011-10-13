/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 12, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

public class InternalErrorPage extends ErrorPage {
    private static final long serialVersionUID = 1L;

    public InternalErrorPage(PageParameters params) {
        super();

        add(new Label("errorContent", params.get("error").toString()));
        setStatelessHint(true);
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        if (!isPageStateless()) {
            String message = "===> Page not stateless " + new Date().toString();
            // find out why
            final List<Component> statefulComponents = new ArrayList<Component>();
            visitChildren(Component.class, new IVisitor<Component, Object>() {

                @Override
                public void component(Component paramT, IVisit<Object> paramIVisit) {
                    if (!paramT.isStateless()) {
                        statefulComponents.add(paramT);
                    }
                }

            });
            if (statefulComponents.size() > 0) {
                message += "===>  Stateful components found: ";
                for (Component c : statefulComponents) {
                    message += "\n" + c.getMarkupId();
                }
            }
            System.out.println(message);
        }
    }
}
