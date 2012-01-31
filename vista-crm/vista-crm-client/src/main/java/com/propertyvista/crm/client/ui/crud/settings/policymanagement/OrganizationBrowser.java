/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.policymanagement;

import java.util.Vector;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.policies.OrganizationPolicyBrowserService;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public abstract class OrganizationBrowser implements IsWidget {
    private static final I18n i18n = I18n.get(OrganizationBrowser.class);

    private final CellTree categoriesTree;

    private final ScrollPanel panel;

    public OrganizationBrowser() {
        // TODO remove the workaround when it's not required
        // WORKAROUND START (see: http://code.google.com/p/google-web-toolkit/issues/detail?id=6359 for more details)
        Resources resources = GWT.create(CellTree.BasicResources.class);
        StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeTopItem() + " {margin-top: 0px;}");
        // WORKAROUND END
        // remove padding (i don't really know why it must be done here and not in the theme, but it that the way it works)
        StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeItem() + " {padding: 0px;}");

        categoriesTree = new CellTree(new OrganizationTreeModel(), null, resources);
        categoriesTree.setAnimationEnabled(true);
        if (categoriesTree.getRootTreeNode().getChildCount() > 1) {
            categoriesTree.getRootTreeNode().setChildOpen(0, true);
        }

        panel = new ScrollPanel(categoriesTree);
        panel.setSize("100%", "100%");
        panel.getElement().getStyle().setProperty("borderStyle", "inset");
        panel.getElement().getStyle().setProperty("borderColor", "black");
        panel.getElement().getStyle().setProperty("borderWidth", "1px");
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    private class OrganizationTreeModel implements TreeViewModel {
        private final OrganizationPolicyBrowserService service = GWT.create(OrganizationPolicyBrowserService.class);

        private final SingleSelectionModel<PolicyNode> selectionModel;

        public OrganizationTreeModel() {
            selectionModel = new SingleSelectionModel<PolicyNode>();
            selectionModel.addSelectionChangeHandler(new Handler() {
                @Override
                public void onSelectionChange(SelectionChangeEvent event) {
                    PolicyNode obj = selectionModel.getSelectedObject();
                    if (obj != null) {
                        onNodeSelected(obj);
                    }
                }
            });
        }

        @Override
        public <T> NodeInfo<?> getNodeInfo(final T value) {
            return new DefaultNodeInfo<PolicyNode>(new AbstractDataProvider<PolicyNode>() {
                @Override
                protected void onRangeChanged(final HasData<PolicyNode> display) {
                    service.getChildNodes(new AsyncCallback<Vector<PolicyNode>>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            throw new Error(caught);
                        }

                        @Override
                        public void onSuccess(Vector<PolicyNode> result) {
                            if (result.isEmpty()) {
                                updateRowCount(0, true);
                            } else {
                                updateRowData(display, 0, result);
                            }
                        }
                    }, value == null ? null : (PolicyNode) value);
                }
            }, new AbstractCell<PolicyNode>() {
                @Override
                public void render(com.google.gwt.cell.client.Cell.Context context, PolicyNode value, SafeHtmlBuilder sb) {
                    // TODO change this to PMC name
                    if (value != null) {
                        if (value instanceof OrganizationPoliciesNode) {
                            sb.appendEscaped(i18n.tr("Organization"));
                        } else {
                            sb.appendEscaped(value.getStringView());
                        }
                    }
                }
            }, selectionModel, null);
        }

        @Override
        public boolean isLeaf(Object value) {
            return value instanceof AptUnit;
        }
    }

    public abstract void onNodeSelected(PolicyNode node);
}
