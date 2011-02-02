package com.pyx4j.dashboard.client;

import java.util.Vector;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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
public class DashboardPanel extends SimplePanel {
    /**
     * Dashboard Widget interface. User-defined widgets should extend GWT Widget and
     * implement this interface.
     */
    public interface IWidget {
        // info:
        public Widget getWidget(); // should be implemented meaningful!

        public String getName();

        // flags:	
        public boolean isMaximizable();

        public boolean isMinimizable();

        public boolean isSetupable();

        // verbs:
        public void showSetup();

        // notifications:
        public void onMaximize(boolean maximized_restored); // true for max-ed, false - restored

        public void onMinimize(boolean minimized_restored); // true for min-ed, false - restored

        public void onDelete();
    } // Interface IWidget

    /**
     * Dashboard layout data type. Represents desirable layout for dashboard
     */
    public static class Layout {
        private int columns = 1; // at least one column exists by default...

        // geometry:
        private double horizontalSpacing = 0; // horizontal (%-values!) and

        private int verticalSpacing = 0; // vertical (pixels) cell spacing value...

        // column relative widths (in per-cents):
        private byte[] columnWidths = new byte[0]; // could be filled with widths...

        // decoration:
        private String[] columnNames = new String[0]; // could be filled with names...

        public Layout() {
        }

        public Layout(int columns) throws IllegalArgumentException {
            if (columns > 0)
                this.columns = columns;
            else
                throw new IllegalArgumentException();
        }

        public Layout(int columns, double spacingH_PCT, int spacingV_PX) throws IllegalArgumentException {
            if (columns > 0)
                this.columns = columns;
            else
                throw new IllegalArgumentException();

            if (!setHorizontalSpacing(spacingH_PCT))
                throw new IllegalArgumentException();

            this.verticalSpacing = spacingV_PX;
        }

        public int getColumns() {
            return columns;
        }

        public double getHorizontalSpacing() {
            return horizontalSpacing;
        }

        /*
         * Horizontal spacing set by %, so their sum (doubled value multiplied by column
         * number) may not exceed 100%, at least (in reality we want to leave space for
         * the columns itself!). Then, the spacing is formed by means of column padding,
         * so its doubled value may not exceed the size of the smallest column also...
         */
        public boolean setHorizontalSpacing(double spacingH_PCT) {
            if (getColumns() * spacingH_PCT * 2 >= 100.0)
                return false; // percentage looks strange!?.

            double pcMin = 100.0 / getColumns();
            for (int i = 0; i < columnWidths.length; ++i)
                pcMin = Math.min(pcMin, columnWidths[i]);

            if (pcMin <= spacingH_PCT * 2)
                return false; // ok, smallest column should be wider than spacing...

            horizontalSpacing = spacingH_PCT;
            return true;
        }

        public int getVerticalSpacing() {
            return verticalSpacing;
        }

        public void setverticalSpacing(int spacingV_PX) {
            verticalSpacing = spacingV_PX;
        }

        public boolean isColumnWidths() {
            return (columnWidths.length != 0);
        }

        /*
         * The column widths set by %, so their sum shouldn't exceed 100%. But there is
         * horizontal spacing also, and spacing formed by column padding, so the smallest
         * column width should be greater that doubled spacing value...
         */
        public boolean setColumnWidths(byte[] columnWidths) throws IllegalArgumentException {
            if (columnWidths.length < getColumns())
                throw new IllegalArgumentException();

            byte pcSum = 0;
            byte pcMin = 0;
            for (int i = 0; i < columnWidths.length; ++i) {
                pcSum += columnWidths[i];
                pcMin = (byte) Math.min(pcMin, columnWidths[i]);
            }

            if (pcSum > 100)
                return false; // mmm, the widths percentage looks strange!?.

            if (pcMin <= getHorizontalSpacing() * 2)
                return false; // ok, smallest column should be wider than spacing...

            this.columnWidths = columnWidths;
            return true;
        }

        public float getCoumnWidth(int column) throws ArrayIndexOutOfBoundsException {
            return columnWidths[column];
        }

        public boolean isColumnNames() {
            return (columnNames.length != 0);
        }

        public void setColumnNames(String[] columnNames) throws IllegalArgumentException {
            if (columnNames.length < getColumns())
                throw new IllegalArgumentException();

            this.columnNames = columnNames;
        }

        public String getCoumnName(int column) throws ArrayIndexOutOfBoundsException {
            return columnNames[column];
        }
    } // class Layout

    // CSS style names used: 
    private static final String CSS_DASHBOARD_PANEL = "DashboardPanel";

    private static final String CSS_DASHBOARD_PANEL_COLUMN_COMPOSITE = "DashboardPanel-column-composite";

    private static final String CSS_DASHBOARD_PANEL_COLUMN_HEADING = "DashboardPanel-column-heading";

    private static final String CSS_DASHBOARD_PANEL_COLUMN_CONTAINER = "DashboardPanel-column-container";

    private static final String CSS_DASHBOARD_PANEL_HOLDER = "DashboardPanel-holder";

    private static final String CSS_DASHBOARD_PANEL_HOLDER_CAPTION = "DashboardPanel-holder-caption";

    private static final String CSS_DASHBOARD_PANEL_HOLDER_MENU = "DashboardPanel-holder-menu";

    private static final String CSS_DASHBOARD_PANEL_HOLDER_MENU_BUTTON = "DashboardPanel-holder-menu-button";

    // internal data:	
    protected Layout layout;

    //    // VladLL : column drag-g-drop functionality is commented till now!..
    //    protected PickupDragController columnDragController;

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

    public boolean refresh() {
        // hold the current widgets for a while:
        Vector<VerticalPanelWithSpacer> columnWidgetsPanels = new Vector<VerticalPanelWithSpacer>(columnsContainerPanel.getWidgetCount());
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

    // initializing:
    protected boolean init() {
        addStyleName(CSS_DASHBOARD_PANEL);

        // use the boundary panel as this composite's widget:
        AbsolutePanel boundaryPanel = new AbsolutePanel();
        boundaryPanel.setSize("100%", "100%");
        setWidget(boundaryPanel);

        // initialize horizontal panel to hold our columns:
        columnsContainerPanel = new FlowPanel();
        columnsContainerPanel.addStyleName(CSS_DASHBOARD_PANEL_COLUMN_CONTAINER);
        columnsContainerPanel.setWidth("100%");

        //        // VladLL : column drag-g-drop functionality is commented till now!..
        //        //initialize our column drag controller: 
        //        columnDragController = new PickupDragController(boundaryPanel, false);
        //        columnDragController.setBehaviorMultipleSelection(false);
        //
        //        // initialize our column drop controller:
        //        CustomFlowPanelDropController columnDropController = new CustomFlowPanelDropController(columnsContainerPanel);
        //        columnDragController.registerDropController(columnDropController);

        boundaryPanel.add(columnsContainerPanel);

        // initialize our widget drag controller:
        widgetDragController = new PickupDragController(boundaryPanel, false);
        widgetDragController.setBehaviorMultipleSelection(false);

        return initColumns();
    }

    protected boolean initColumns() {
        columnsContainerPanel.clear();
        for (int col = 0; col < layout.getColumns(); ++col) {
            // vertical panel to hold the heading and a second vertical panel for widgets:
            FlowPanel columnCompositePanel = new FlowPanel();
            columnCompositePanel.addStyleName(CSS_DASHBOARD_PANEL_COLUMN_COMPOSITE);
            columnCompositePanel.setWidth((layout.isColumnWidths() ? layout.getCoumnWidth(col) : 100.0 / layout.getColumns()) - layout.getHorizontalSpacing()
                    * 2 + "%");

            DOM.setStyleAttribute(columnCompositePanel.getElement(), "padding", "0px " + layout.getHorizontalSpacing() + "%");
            DOM.setStyleAttribute(columnCompositePanel.getElement(), "float", "left");

            // put column name if necessary:
            if (layout.isColumnNames()) {
                Label heading = new Label(layout.getCoumnName(col));
                heading.addStyleName(CSS_DASHBOARD_PANEL_COLUMN_HEADING);
                heading.setWidth("100%");

                columnCompositePanel.add(heading);

                //                // VladLL : column drag-g-drop functionality is commented till now!..
                //                // make the column draggable by its heading:
                //                columnDragController.makeDraggable(columnCompositePanel, heading);
            }

            // inner vertical panel to hold individual widgets:
            VerticalPanelWithSpacer columnPanel = new VerticalPanelWithSpacer();
            columnPanel.addStyleName(CSS_DASHBOARD_PANEL_COLUMN_CONTAINER);
            columnPanel.setWidth("100%");

            columnCompositePanel.add(columnPanel);

            // widget drop controller for the current column:
            CustomFlowPanelDropController widgetDropController = new CustomFlowPanelDropController(columnPanel);
            widgetDragController.registerDropController(widgetDropController);

            columnsContainerPanel.add(columnCompositePanel);
        }

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
    protected VerticalPanelWithSpacer getColumnWidgetsPanel(int column) {
        FlowPanel columnCompositePanel = (FlowPanel) columnsContainerPanel.getWidget(column);
        return (VerticalPanelWithSpacer) columnCompositePanel.getWidget(columnCompositePanel.getWidgetCount() - 1);
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

    protected final class WidgetHolder extends SimplePanel {
        private final IWidget holdedWidget;

        private final DashboardPanel mainPanel;

        private final VerticalPanel holder = new VerticalPanel();

        private final Label title = new Label();

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
            title.setText(holdedWidget.getName());

            widgetHolderCaption.add(title);
            widgetHolderCaption.setCellWidth(title, "100%");
            widgetHolderCaption.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            widgetHolderCaption.add(createWidgetMenu());
            widgetHolderCaption.addStyleName(CSS_DASHBOARD_PANEL_HOLDER_CAPTION);
            widgetHolderCaption.setWidth("100%");

            // put it together:
            holder.add(widgetHolderCaption);
            holder.add(holdedWidget.getWidget());
            holder.addStyleName(CSS_DASHBOARD_PANEL_HOLDER);
            holder.setWidth("100%");

            this.setWidget(holder);
            this.setWidth("100%");

            // don't forget about vertical spacing:
            DOM.setStyleAttribute(this.getElement(), "padding", mainPanel.layout.getVerticalSpacing() + "px" + " 0px");

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
                    if (holdedWidget.isMinimizable())
                        menu.addItem((isMinimized() ? "Expand" : "Minimize"), cmdMinimize);

                    if (holdedWidget.isMaximizable())
                        menu.addItem((isMaximized() ? "Restore" : "Maximize"), cmdMaximize);

                    menu.addItem("Delete", cmdDelete);

                    if (holdedWidget.isSetupable()) {
                        menu.addSeparator();
                        menu.addItem("Setup", cmdSetup);
                    }

                    //                    menu.addStyleName(CSS_DASHBOARD_PANEL_HOLDER_MENU);

                    pp.setWidget(menu);
                    pp.addStyleName(CSS_DASHBOARD_PANEL_HOLDER_MENU);
                    pp.setPopupPosition(event.getClientX() - 40, event.getClientY());
                    pp.show();
                } // onClick button event handler...
            }); // ClickHandler class...

            btn.addStyleName(CSS_DASHBOARD_PANEL_HOLDER_MENU_BUTTON);
            btn.setHeight("1.5em");
            return btn;
        }

        // --------------------------------------------------------------
        private void minimize() {
            if (isMinimized()) {
                holder.add(minimizedWidget);
                minimizedWidget = null;
                holdedWidget.onMinimize(false);
            } else { // minimize:
                minimizedWidget = holdedWidget.getWidget();
                holder.remove(minimizedWidget);
                holdedWidget.onMinimize(true);
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

                //                this.setWidth("100%");
                widgetDragController.makeDraggable(this, title);
                holdedWidget.onMaximize(false);
            } else { // maximize:
                maximizeData.saveWidgetPosition((FlowPanel) getParent(), this);
                maximizeData.boundaryPanel = mainPanel.getWidget();
                mainPanel.setWidget(this);

                //                this.setSize("100%", "100%");
                widgetDragController.makeNotDraggable(this);
                holdedWidget.onMaximize(true);
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
            holdedWidget.onDelete();
            ((FlowPanel) getParent()).remove(this);
        }
    } // WidgetHolder
} // DashboardPanel class...
