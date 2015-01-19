/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 22, 2011
 * @author igors
 */
package com.propertyvista.crm.client.ui.crud.communication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.PrintUtils;
import com.propertyvista.crm.client.ui.components.boxes.EmployeeSelectionDialog;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmUserVisit;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.EmployeeEnabledCriteria;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.MessageDTO;

public class CommunicationViewerViewImpl extends CrmViewerViewImplBase<CommunicationThreadDTO> implements CommunicationViewerView {

    private static final I18n i18n = I18n.get(CommunicationViewerViewImpl.class);

    private final MenuItem assignOwnershipAction;

    private final MenuItem assignToMeAction;

    private final MenuItem unassignAction;

    private final MenuItem hideUnhideAction;

    private final Map<ThreadStatus, MenuItem> threadStatusActions;

    public CommunicationViewerViewImpl() {
        super(true);
        setForm(new CommunicationForm(this));

        final CommunicationForm form = (CommunicationForm) getForm();

        assignToMeAction = new MenuItem(i18n.tr("Assign to Me"), new Command() {
            @Override
            public void execute() {
                assignEmployee(ClientContext.visit(CrmUserVisit.class).getCurrentUser(), null);
            }
        });
        unassignAction = new MenuItem(i18n.tr("Unassign"), new Command() {
            @Override
            public void execute() {
                assignEmployee(null, null);
            }
        });
        assignOwnershipAction = new MenuItem(i18n.tr("Assign Owner"), new Command() {
            @Override
            public void execute() {
                new UpdateThreadStatusAndOwnerBox(form, null) {
                    @Override
                    public boolean onClickOk() {
                        if (validate()) {
                            //getValue().thread().set(form.getValue().thread());
                            assignEmployee(getEmployee(), getValue().text().getValue());
                            return true;
                        } else {
                            return false;
                        }
                    }
                }.show();

            }
        });

        threadStatusActions = new HashMap<ThreadStatus, MenuItem>();

        for (final ThreadStatus ts : ThreadStatus.values()) {

            threadStatusActions.put(ts, new MenuItem(ThreadStatus.Open.equals(ts) ? ThreadStatus.Open.toString() : i18n.tr("Resolve"), new Command() {
                @Override
                public void execute() {
                    new UpdateThreadStatusAndOwnerBox(form, ts.toString()) {
                        @Override
                        public boolean onClickOk() {
                            if (validate()) {
                                getValue().thread().set(form.getValue());
                                ((CommunicationViewerView.Presenter) form.getParentView().getPresenter()).saveMessage(getValue(), ts, true);
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }.show();
                }
            }));
        }
        hideUnhideAction = new MenuItem(i18n.tr("Hide"), new Command() {
            @Override
            public void execute() {
                hideUnhide();
            }
        });

        addAction(assignToMeAction);
        addAction(unassignAction);
        addAction(assignOwnershipAction);
        for (MenuItem action : threadStatusActions.values()) {
            addAction(action);
        }

        addAction(hideUnhideAction);

        MenuItem btnPrint = new MenuItem(i18n.tr("Print"), new Command() {
            @Override
            public void execute() {
                PrintUtils.print(CommunicationViewerViewImpl.this.getForm().getPrintableElement());
            }
        });

        addAction(btnPrint);
    }

    @Override
    public void reset() {
        for (MenuItem action : threadStatusActions.values()) {
            setActionVisible(action, false);
        }
        super.reset();
    }

    @Override
    public void populate(CommunicationThreadDTO value) {
        super.populate(value);

        boolean invisible = !CategoryType.Ticket.equals(value.category().categoryType().getValue());
        setActionVisible(assignOwnershipAction, !invisible || ClientContext.getUserVisit().getName().equals(value.owner().name().getValue()));
        for (ThreadStatus status : threadStatusActions.keySet()) {
            MenuItem action = threadStatusActions.get(status);
            if (status.equals(value.status().getValue())) {
                setActionVisible(action, false);
            }
            // only Open, Resolved;
            else {
                setActionVisible(action, !invisible && !TicketType.Maintenance.equals(value.category().ticketType().getValue()));
            }
        }
        setActionVisible(assignToMeAction, !invisible && !ClientContext.getUserVisit().getName().equals(value.owner().name().getValue()));
        setActionVisible(unassignAction, (!invisible && (!ContactType.System.equals(value.owner().getValue())) || ClientContext.getUserVisit().getName()
                .equals(value.owner().name().getValue()))
                && !ThreadStatus.Open.equals(value.status().getValue()));

        setCaption(value.deliveryMethod() == null || value.deliveryMethod().isNull() ? value.category().categoryType().getValue().toString() : value
                .deliveryMethod().getValue().toString());

        hideUnhideAction.setText(value.hidden().getValue(false) ? i18n.tr("Unhide") : i18n.tr("Hide"));
        //setActionVisible(hideUnhideAction, !CategoryType.Ticket.equals(value.category().categoryType().getValue()));
    }

    public void assignEmployee(IEntity e, String additionalComment) {
        ((CommunicationForm) getForm()).assignOwnership(e, additionalComment);
    }

    public void hideUnhide() {
        ((CommunicationForm) getForm()).hideUnhide();
    }

    abstract class UpdateThreadStatusAndOwnerBox extends OkCancelDialog {

        private CForm<MessageDTO> content;

        private final String newStatus;

        private Employee emp;

        public UpdateThreadStatusAndOwnerBox(final CommunicationForm form, String newStatus) {
            super(newStatus == null ? i18n.tr("Assign Owner ") : i18n.tr("Update Status to: ") + newStatus);
            this.newStatus = newStatus;
            setBody(createBody(form));
        }

        protected Widget createBody(final CommunicationForm form) {
            getOkButton().setEnabled(true);

            content = new CForm<MessageDTO>(MessageDTO.class) {
                @Override
                protected IsWidget createContent() {
                    FormPanel main = new FormPanel(this);
                    if (newStatus == null) {
                        main.append(Location.Dual, inject(proto().thread().owner(), new EmployeeSelector())).decorate();
                    }
                    main.append(Location.Dual, inject(proto().text())).decorate().customLabel(i18n.tr("Comment"));

                    return main;
                }

                @Override
                public void addValidations() {
                    super.addValidations();
                }
            };

            content.init();
            MessageDTO ms = EntityFactory.create(MessageDTO.class);
            content.populate(ms);
            return content.asWidget();
        }

        public MessageDTO getValue() {
            return content.getValue();
        }

        public boolean validate() {
            content.setVisitedRecursive();
            return getValue().text() != null && !getValue().text().isNull();
        }

        public Employee getEmployee() {
            return emp;
        }

        class EmployeeSelector extends CEntitySelectorHyperlink<Employee> {
            @Override
            protected AppPlace getTargetPlace() {
                return AppPlaceEntityMapper.resolvePlace(Employee.class, getValue().getPrimaryKey());
            }

            @Override
            protected IShowable getSelectorDialog() {
                return new EmployeeSelectionDialog() {
                    @Override
                    public boolean onClickOk() {
                        emp = getSelectedItem();
                        setValue(emp);
                        if (emp != null) {
                            MessageDTO ms = EntityFactory.create(MessageDTO.class);
                            ms.thread().owner().set(emp);
                            UpdateThreadStatusAndOwnerBox.this.content.populate(ms);
                        }
                        return true;
                    }

                    @Override
                    protected void setFilters(List<Criterion> filters) {
                        super.setFilters(filters);
                        addFilter(new EmployeeEnabledCriteria(true));
                    }
                };
            }

        }
    }

}
