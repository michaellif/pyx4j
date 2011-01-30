package com.pyx4j.dashboard.client;

import java.util.Vector;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dashboard panel.
 */
public class DashboardPanel extends /* BasePanel */SimplePanel {
    /**
     * Dashboard Widget interface. User-defined widgets should extend GWT Widget and
     * implement this interface.
     */
    public interface IWidget {
        // info:
        public Widget getWidget();

        public String getName();

        // flags:	
        public boolean isMaximizable();

        public boolean isSetupable();

        // verbs:
        public void showSetup();
    }

    /**
     * Dashboard layout data type.
     */
    public static class Layout {
        private int columns = 1; // at least one column exists by default...

        // geometry:
        private int spacing = 0; // horizontal and vertical cell spacing value...

        // column relative widths (in per-cents):
        private byte columnWidths[]; // should be filled with names if useColumnWidths set to true!..

        public boolean useColumnWidths = false;

        // decoration:
        private String columnNames[]; // should be filled with names if useColumnNames set to true!..

        public boolean useColumnNames = false;

        /*
         * Represents desirable layout for dashboard
         */
        public Layout() {
        }

        public Layout(int columns) throws ArrayIndexOutOfBoundsException {
            if (columns > 0)
                this.columns = columns;
            else
                throw new ArrayIndexOutOfBoundsException();
        }

        public Layout(int columns, int spacing) throws ArrayIndexOutOfBoundsException {
            if (columns > 0)
                this.columns = columns;
            else
                throw new ArrayIndexOutOfBoundsException();

            this.spacing = spacing;
        }

        public int getColumns() {
            return columns;
        }

        public int getSpacing() {
            return spacing;
        }

        public boolean setColumWidths(byte[] columnWidths) throws ArrayIndexOutOfBoundsException {
            if (columnWidths.length < getColumns())
                throw new ArrayIndexOutOfBoundsException();

            short pcSum = 0;
            for (int i = 0; i < columnWidths.length; ++i)
                pcSum += columnWidths[i];

            if (pcSum != 100)
                return false; // ok, the widths percentage isn't correct!?.

            this.columnWidths = columnWidths;
            return true;
        }

        public byte getCoumnWidth(int column) throws NullPointerException, ArrayIndexOutOfBoundsException {
            return columnWidths[column];
        }

        public void setColumnNames(String[] columnNames) throws ArrayIndexOutOfBoundsException {
            if (columnNames.length < getColumns())
                throw new ArrayIndexOutOfBoundsException();

            this.columnNames = columnNames;
        }

        public String getCoumnName(int column) throws NullPointerException, ArrayIndexOutOfBoundsException {
            return columnNames[column];
        }
    }

    // CSS style names used: 
    private static final String CSS_INSERT_PANEL = "InsertPanel";

    private static final String CSS_INSERT_PANEL_COLUMN_COMPOSITE = "InsertPanel-column-composite";

    private static final String CSS_INSERT_PANEL_COLUMN_HEADING = "InsertPanel-column-heading";

    private static final String CSS_INSERT_PANEL_COLUMN_CONTAINER = "InsertPanel-column-container";

    private static final String CSS_INSERT_PANEL_HOLDER = "InsertPanel-holder";

    private static final String CSS_INSERT_PANEL_HOLDER_CAPTION = "InsertPanel-holder-caption";

    private static final String CSS_INSERT_PANEL_HOLDER_MENU = "InsertPanel-holder-menu";

    private static final String CSS_INSERT_PANEL_HOLDER_MENU_BUTTON = "InsertPanel-holder-menu-button";

    // internal data:	
    protected Layout layout;

    //	protected PickupDragController columnDragController;
    protected PickupDragController widgetDragController;

    protected FlowPanel columnsContainerPanel; // holds columns (as vertical panels).

    // construction:
    public DashboardPanel() {
        this.layout = new Layout();
        init();
    }

    public DashboardPanel(Layout layout) {
        this.layout = layout;
        init();
    }

    // Layout manipulation:
    public Layout getLayout() {
        return this.layout;
    }

    public boolean setLayout(Layout layout) {
        this.layout = layout; // accept new layout
        return refresh();
    }

    // initializing:
    protected boolean init() {
        addStyleName(CSS_INSERT_PANEL);

        // use the boundary panel as this composite's widget:
        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        // initialize horizontal panel to hold our columns:
        columnsContainerPanel = new FlowPanel();
        columnsContainerPanel.addStyleName(CSS_INSERT_PANEL_COLUMN_CONTAINER);
        //		columnsContainerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        //		columnsContainerPanel.setSpacing(layout.getSpacing());
        columnsContainerPanel.setWidth("100%");

        /*
         * // initialize our column drag controller: columnDragController = new
         * PickupDragController(boundaryPanel, false);
         * columnDragController.setBehaviorMultipleSelection(false);
         * 
         * // initialize our column drop controller: FlowPanelDropController
         * columnDropController = new FlowPanelDropController(columnsContainerPanel);
         * columnDragController.registerDropController(columnDropController);
         */
        boundaryPanel.add(columnsContainerPanel);

        // initialize our widget drag controller:
        widgetDragController = new PickupDragController(boundaryPanel, false);
        widgetDragController.setBehaviorMultipleSelection(false);

        return initColumns();
    }

    protected boolean initColumns() {
        columnsContainerPanel.clear();
        for (int col = 0; col < layout.getColumns(); ++col) {
            // vertical panel to hold the heading and a second vertical panel:
            FlowPanel columnCompositePanel = new FlowPanel();
            columnCompositePanel.addStyleName(CSS_INSERT_PANEL_COLUMN_COMPOSITE);
            columnCompositePanel.setWidth((layout.useColumnWidths ? layout.getCoumnWidth(col) : 100 / layout.getColumns()) - 0.6 + "%");

            /*
             * byte pcWidth = 0; for(int j = 0; j < col; ++j) { pcWidth +=
             * (layout.useColumnWidths ? layout.getCoumnWidth(j) :
             * 100/layout.getColumns()); }
             */
            DOM.setStyleAttribute(columnCompositePanel.getElement(), "margin", "0.2%");
            //			DOM.setStyleAttribute(columnCompositePanel.getElement(), "padding", "0.2%");
            //			DOM.setStyleAttribute(columnCompositePanel.getElement(), "float", "left");
            //			DOM.setStyleAttribute(columnCompositePanel.getElement(), "position", "relative");
            //			DOM.setStyleAttribute(columnCompositePanel.getElement(), "position", "float");
            //			DOM.setStyleAttribute(columnCompositePanel.getElement(), "top", "0");
            //			DOM.setStyleAttribute(columnCompositePanel.getElement(), "left", pcWidth +"%");
            //			DOM.setStyleAttribute(columnCompositePanel.getElement(), "height", "50px");

            // put column name if necessary:
            if (layout.useColumnNames) {
                Label heading = new Label(layout.getCoumnName(col));
                heading.addStyleName(CSS_INSERT_PANEL_COLUMN_HEADING);
                heading.setWidth("100%");

                columnCompositePanel.add(heading);

                /*
                 * // make the column draggable by its heading:
                 * columnDragController.makeDraggable(columnCompositePanel,heading);
                 */}

            // inner vertical panel to hold individual widgets:
            VerticalPanelWithSpacer columnPanel = new VerticalPanelWithSpacer();
            columnPanel.addStyleName(CSS_INSERT_PANEL_COLUMN_CONTAINER);
            //			columnPanel.setSpacing(layout.getSpacing());
            columnPanel.setWidth("100%");

            columnCompositePanel.add(columnPanel);
            //			columnCompositePanel.setCellWidth(columnPanel, "100%");

            // widget drop controller for the current column:
            FlowPanelDropController widgetDropController = new FlowPanelDropController(columnPanel);
            widgetDragController.registerDropController(widgetDropController);

            columnsContainerPanel.add(columnCompositePanel);
        }

        return true;
    }

    private boolean refresh() {
        // hold the current widgets for a while:
        Vector<FlowPanel> columnWidgetsPanels = new Vector<FlowPanel>(columnsContainerPanel.getWidgetCount());
        for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i)
            columnWidgetsPanels.add(getColumnWidgetsPanel(i));

        initColumns(); // initialize new columns according to the (new) layout

        // transfer current widgets to the new layout, 
        // first - move intersected part of the columns:
        int i;
        int minCommonSize = Math.min(columnWidgetsPanels.size(), columnsContainerPanel.getWidgetCount());
        for (i = 0; i < minCommonSize; ++i)
            for (int j = 0; j < columnWidgetsPanels.get(i).getWidgetCount(); ++j)
                getColumnWidgetsPanel(i).add(columnWidgetsPanels.get(i).getWidget(j));

        // and then - move the rest in the last column of new layout (if present):
        for (; i < columnWidgetsPanels.size(); ++i)
            for (int j = 0; j < columnWidgetsPanels.get(i).getWidgetCount(); ++j)
                getColumnWidgetsPanel(columnsContainerPanel.getWidgetCount() - 1).add(columnWidgetsPanels.get(i).getWidget(j));

        return true;
    }

    // Widget manipulation:	
    public boolean addWidget(IWidget widget) {
        return insertWidget(widget, 0, 0);
    }

    public boolean addWidget(IWidget widget, int column) {
        return insertWidget(widget, column, -1);
    }

    public boolean insertWidget(IWidget widget, int column, int row) {
        if (checkIndexes(column, row, true)) {
            // create holder for supplied widget and insert it into specified column,row:
            WidgetHolder wh = new WidgetHolder(widget, this);

            if (row > 0)
                getColumnWidgetsPanel(column).insert(wh, row);
            else
                // if row is negative - just add at the end:
                getColumnWidgetsPanel(column).add(wh);

            return true;
        }

        return false;
    }

    public boolean removeWidget(int column, int row) {
        return (checkIndexes(column, row, false) && getColumnWidgetsPanel(column).remove(row));
    }

    public void removeAllWidgets() {
        for (int i = 0; i < columnsContainerPanel.getWidgetCount(); ++i)
            getColumnWidgetsPanel(i).clear();
    }

    // internals:	
    protected FlowPanel getColumnWidgetsPanel(int column) {
        FlowPanel columnCompositePanel = (FlowPanel) columnsContainerPanel.getWidget(column);
        return (FlowPanel) columnCompositePanel.getWidget(columnCompositePanel.getWidgetCount() - 1);
        // note, that first element could be label with column name, so always get last one!..
    }

    protected boolean checkIndexes(int column, int row, boolean insert) {
        if (column >= columnsContainerPanel.getWidgetCount())
            return false;

        if (insert) {
            if (row > getColumnWidgetsPanel(column).getWidgetCount())
                return false;
        } else if (row >= getColumnWidgetsPanel(column).getWidgetCount())
            return false;

        return true;
    }

    protected final class WidgetHolder extends VerticalPanel {
        private final IWidget holdedWidget;

        private final DashboardPanel mainPanel;

        // public interface:
        public IWidget getIWidget() {
            return holdedWidget;
        }

        // internals:
        protected WidgetHolder(IWidget widget, DashboardPanel mainPanel) {
            this.holdedWidget = widget;
            this.mainPanel = mainPanel;

            // create caption with title and menu:
            HorizontalPanel widgetHolderCaption = new HorizontalPanel();

            Label title = new Label(holdedWidget.getName());

            widgetHolderCaption.add(title);
            widgetHolderCaption.setCellWidth(title, "100%");
            widgetHolderCaption.setHorizontalAlignment(ALIGN_RIGHT);
            widgetHolderCaption.add(createWidgetMenu());
            widgetHolderCaption.addStyleName(CSS_INSERT_PANEL_HOLDER_CAPTION);
            widgetHolderCaption.setWidth("100%");

            // put it together:
            add(widgetHolderCaption);
            add(holdedWidget.getWidget());
            addStyleName(CSS_INSERT_PANEL_HOLDER);
            setWidth("100%");

            DOM.setStyleAttribute(getElement(), "margin", mainPanel.layout.getSpacing() + "px");

            // make the widget place holder draggable by its title:
            widgetDragController.makeDraggable(this, title);
        }

        private Widget createWidgetMenu() {
            final Button btn = new Button("^");
            btn.addClickHandler(new ClickHandler() {
                private final PopupPanel pp = new PopupPanel(true);

                @Override
                public void onClick(ClickEvent event) {
                    // menu items command processors go here:
                    Command cmdMinimize = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            minimize();
                        }
                    };

                    Command cmdMaximize = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            maximize();
                        }
                    };

                    Command cmdSetup = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            getIWidget().showSetup();
                        }
                    };

                    Command cmdDelete = new Command() {
                        @Override
                        public void execute() {
                            pp.hide();
                            delete();
                        }
                    };

                    // create the menu:
                    MenuBar menu = new MenuBar(true);
                    menu.addItem((isMinimized() ? "Expand" : "Minimize"), cmdMinimize);

                    if (holdedWidget.isMaximizable())
                        menu.addItem((isMaximized() ? "Restore" : "Maximize"), cmdMaximize);

                    menu.addItem("Delete", cmdDelete);

                    if (holdedWidget.isSetupable()) {
                        menu.addSeparator();
                        menu.addItem("Setup", cmdSetup);
                    }

                    //					menu.addStyleName(CSS_INSERT_PANEL_HOLDER_MENU);

                    pp.setWidget(menu);
                    pp.addStyleName(CSS_INSERT_PANEL_HOLDER_MENU);
                    pp.setPopupPosition(event.getClientX() - 40, event.getClientY());
                    pp.show();
                } // onClick button event handler...
            }); // ClickHandler class...

            btn.addStyleName(CSS_INSERT_PANEL_HOLDER_MENU_BUTTON);
            btn.setHeight("1.2em");
            return btn;
        }

        // --------------------------------------------------------------
        private void minimize() {
            if (isMinimized()) {
                add(minimizedWidget);
                minimizedWidget = null;
            } else { // minimize:
                minimizedWidget = holdedWidget.getWidget();
                remove(minimizedWidget);
            }
        }

        private boolean isMinimized() {
            return minimizedWidget != null;
        }

        private Widget minimizedWidget;

        // --------------------------------------------------------------
        private void maximize() {
            if (isMaximized()) {
                maximizeData.restoreWidgetPosition(this);
                mainPanel.setWidget(maximizeData.boundaryPanel);
                maximizeData.clear();
                this.setWidth("100%");
            } else { // maximize:
                maximizeData.saveWidgetPosition((FlowPanel) getParent(), this);
                maximizeData.boundaryPanel = mainPanel.getWidget();
                mainPanel.setWidget(this);
                this.setSize("100%", "100%");
            }
        }

        private boolean isMaximized() {
            return maximizeData.boundaryPanel != null;
        }

        private class MaximizeData {
            private FlowPanel columnPanel;

            private int widgetIndex;

            public Widget boundaryPanel;

            public void saveWidgetPosition(FlowPanel currentColumn, WidgetHolder widget) {
                columnPanel = currentColumn;
                widgetIndex = maximizeData.columnPanel.getWidgetIndex(widget);
            }

            public void restoreWidgetPosition(WidgetHolder widget) {
                columnPanel.insert(widget, widgetIndex);
            }

            public void clear() {
                boundaryPanel = null;
                columnPanel = null;
            }
        }

        private final MaximizeData maximizeData = new MaximizeData();

        // --------------------------------------------------------------
        private void delete() {
            ((VerticalPanel) getParent()).remove(this);
        }
    } // WidgetHolder
} // DashboardPanel class...
