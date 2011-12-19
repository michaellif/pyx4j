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

import java.util.Arrays;
import java.util.Vector;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.CellTree.Resources;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.rpc.services.policy.OrganizationPolicyBrowserService;
import com.propertyvista.domain.policy.PolicyPresetAtNode.NodeType;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;

public abstract class OrganizationBrowser implements IsWidget {
    private static final I18n i18n = I18n.get(OrganizationBrowser.class);

    private final CellTree categoriesTree;

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
        categoriesTree.getRootTreeNode().setChildOpen(0, true);
        categoriesTree.setSize("100%", "100%");
    }

    @Override
    public Widget asWidget() {
        return categoriesTree;
    }

    private class OrganizationTreeModel implements TreeViewModel {
        private final OrganizationPolicyBrowserService service = GWT.create(OrganizationPolicyBrowserService.class);

        @Override
        public <T> NodeInfo<?> getNodeInfo(final T value) {
            // TODO add Region
            if (value == null) {
                // Root Node
                return new DefaultNodeInfo<String>(new ListDataProvider<String>(Arrays.asList(i18n.tr("Organization"))), new TextCell());
            } else if (value instanceof String) {
                return new DefaultNodeInfo<Country>(new AbstractDataProvider<Country>() {
                    @Override
                    protected void onRangeChanged(final HasData<Country> display) {
                        service.getCountries(new AsyncCallback<Vector<Country>>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                throw new Error(caught);
                            }

                            @Override
                            public void onSuccess(Vector<Country> result) {
                                updateRowData(display, 0, result);
                            }
                        });
                    }
                }, new AbstractCell<Country>() {
                    @Override
                    public void render(com.google.gwt.cell.client.Cell.Context context, Country value, SafeHtmlBuilder sb) {
                        if (value != null) {
                            sb.appendEscaped(value.getStringView());
                        }
                    };
                });
            } else if (value instanceof Country) {
                return new DefaultNodeInfo<Complex>(new AbstractDataProvider<Complex>() {
                    @Override
                    protected void onRangeChanged(final HasData<Complex> display) {
                        service.getComplexes(new AsyncCallback<Vector<Complex>>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                throw new Error(caught);
                            }

                            @Override
                            public void onSuccess(final Vector<Complex> result) {
                                updateRowData(display, 0, result);
                            }
                        }, ((Country) value).getPrimaryKey());
                    }
                }, new AbstractCell<Complex>() {
                    @Override
                    public void render(com.google.gwt.cell.client.Cell.Context context, Complex value, SafeHtmlBuilder sb) {
                        if (value != null) {
                            if (value.isNull()) {
                                sb.appendEscaped(i18n.tr("Buildings that do not belong to a complex"));
                            } else {
                                sb.appendEscaped(value.name().getValue());
                            }
                        }
                    }
                });
            } else if (value instanceof Complex) {
                return new DefaultNodeInfo<Building>(new AbstractDataProvider<Building>() {
                    @Override
                    protected void onRangeChanged(final HasData<Building> display) {
                        service.getBuildings(new AsyncCallback<Vector<Building>>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                throw new Error(caught);
                            }

                            @Override
                            public void onSuccess(final Vector<Building> result) {
                                updateRowData(display, 0, result);
                            }
                        }, ((Complex) value).getPrimaryKey());
                    }
                }, new AbstractCell<Building>() {
                    @Override
                    public void render(com.google.gwt.cell.client.Cell.Context context, Building value, SafeHtmlBuilder sb) {
                        if (value != null) {
                            sb.appendEscaped(value.getStringView());
                        }
                    }
                });
            } else if (value instanceof Building) {
                final SingleSelectionModel<AptUnit> selectionModel = new SingleSelectionModel<AptUnit>();
                selectionModel.addSelectionChangeHandler(new Handler() {
                    @Override
                    public void onSelectionChange(SelectionChangeEvent event) {
                        AptUnit unit = selectionModel.getSelectedObject();
                        if (unit != null) {
                            onNodeSelected(unit.getPrimaryKey(), NodeType.unit);
                        }
                    }
                });
                return new DefaultNodeInfo<AptUnit>(new AbstractDataProvider<AptUnit>() {
                    @Override
                    protected void onRangeChanged(final HasData<AptUnit> display) {
                        service.getUnits(new AsyncCallback<Vector<AptUnit>>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                throw new Error(caught);
                            }

                            @Override
                            public void onSuccess(final Vector<AptUnit> result) {
                                updateRowData(display, 0, result);
                            }
                        }, ((Building) value).getPrimaryKey());
                    }
                }, new AbstractCell<AptUnit>() {
                    @Override
                    public void render(com.google.gwt.cell.client.Cell.Context context, AptUnit value, SafeHtmlBuilder sb) {
                        if (value != null) {
                            sb.appendEscaped(value.getStringView());
                        }
                    }
                }, selectionModel, null);
            }
            return null;
        }

        @Override
        public boolean isLeaf(Object value) {
            return value instanceof AptUnit;
        }
    }

    public abstract void onNodeSelected(Key nodeKey, NodeType nodeType);
}
