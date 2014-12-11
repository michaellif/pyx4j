package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.gwt.commons.layout.LayoutChangeEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeHandler;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.Context;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.Button.SecureMenuItem;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

import com.propertyvista.common.client.ClientLocaleUtils;
import com.propertyvista.common.client.theme.SiteViewTheme;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.CrmClientCommunicationManager;
import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.crm.client.themes.CommunicationCrmTheme;
import com.propertyvista.crm.rpc.CrmUserVisit;
import com.propertyvista.crm.rpc.dto.communication.CrmCommunicationSystemNotification;
import com.propertyvista.crm.rpc.services.admin.ac.CrmAdministrationAccess;
import com.propertyvista.crm.rpc.services.organization.ac.EmployeeSelfAccountAndSettings;
import com.propertyvista.dto.MessageDTO;
import com.propertyvista.shared.i18n.CompiledLocale;

public class HeaderViewImpl extends FlowPanel implements HeaderView {

    public static String BACK_TO_CRM = "vista_Back2CRM";

    private static final I18n i18n = I18n.get(HeaderViewImpl.class);

    private HeaderPresenter presenter;

    private Button userButton;

    private Button adminButton;

    private Button exitAdminButton;

    private Button communicationButton;

    private MenuItem support;

    private Button languageButton;

    private ButtonMenuBar languageButtonMenu;

    private Button sideMenuButton;

    private static String brandedHeader;

    private static boolean useLogoImage;

    private LayoutType layoutType;

    private boolean loggedIn = false;

    private SimplePanel logoContainer;

    private Image logoImage;

    private Toolbar rightToolbar;

    //TODO Misha How can I do this properly ?
    @Deprecated
    public static void temporaryWayToSetTitle(String title, boolean logoImageAvalable) {
        brandedHeader = title;
        useLogoImage = logoImageAvalable;
    }

    public HeaderViewImpl() {
        setStyleName(SiteViewTheme.StyleName.SiteViewHeader.name());

        {//Left Toolbar
            Toolbar toolbar = new Toolbar();
            sideMenuButton = new Button(CrmImages.INSTANCE.menu(), new Command() {
                @Override
                public void execute() {
                    AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideMenu));
                }
            });
            toolbar.addItem(sideMenuButton);
            add(toolbar);
        }

        {//Logo
            logoContainer = new SimplePanel();
            logoContainer.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            HasClickHandlers logoElement;

            if (useLogoImage) {
                logoImage = new Image(MediaUtils.createCrmLogoUrl());

                logoImage.getElement().getStyle().setProperty("maxHeight", "50px");
                logoImage.getElement().getStyle().setMarginLeft(-30, Unit.PX);
                logoImage.getElement().getStyle().setFloat(Style.Float.LEFT);
                logoImage.getElement().getStyle().setCursor(Cursor.POINTER);
                logoImage.setTitle(i18n.tr("Home"));

                RootPanel.get().add(logoImage);
                int logoImageWidth = logoImage.getOffsetWidth();

                logoContainer.setWidget(logoImage);

                logoImage.getElement().getStyle().setMarginLeft(-logoImageWidth / 2, Unit.PX);

                logoElement = logoImage;
            } else {
                HTML logoHtml = new HTML("<h1>" + (brandedHeader != null ? new SafeHtmlBuilder().appendEscaped(brandedHeader).toSafeHtml().asString() : "")
                        + "</h1>");
                logoHtml.getElement().getStyle().setCursor(Cursor.POINTER);

                logoContainer.setWidget(logoHtml);
                logoElement = logoHtml;
            }

            logoElement.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigToLanding();
                }
            });

            add(logoContainer);
        }

        {//Right Toolbar
            rightToolbar = new Toolbar();
            rightToolbar.getElement().getStyle().setProperty("right", "0");
            rightToolbar.getElement().getStyle().setProperty("top", "0");

            rightToolbar.addStyleName(SiteViewTheme.StyleName.SiteViewAction.name());

            userButton = new Button("");

            ButtonMenuBar userButtonMenu = new ButtonMenuBar();
            userButton.setMenu(userButtonMenu);

            userButtonMenu.addItem(new SecureMenuItem(i18n.tr("Profile"), new Command() {
                @Override
                public void execute() {
                    presenter.showUserProfile();
                }
            }, EmployeeSelfAccountAndSettings.class));
            userButtonMenu.addItem(new SecureMenuItem(i18n.tr("Account"), new Command() {
                @Override
                public void execute() {
                    presenter.showUserPreferences();
                }
            }));

            userButtonMenu.addItem(support = new MenuItem(i18n.tr("Support"), new Command() {
                @Override
                public void execute() {
                    presenter.getSatisfaction();
                }
            }));

            userButtonMenu.addItem(new MenuItem(i18n.tr("LogOut"), new Command() {
                @Override
                public void execute() {
                    presenter.logout();
                }
            }));

            communicationButton = new Button(CrmImages.INSTANCE.alertsOff(), new Command() {
                @Override
                public void execute() {
                    switch (layoutType) {
                    case phonePortrait:
                    case phoneLandscape:
                    case tabletPortrait:
                        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.toggleSideComm));
                        break;
                    default:
                        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(communicationButton));
                        break;
                    }
                    presenter.loadMessages();
                }
            });
            communicationButton.setPermission(DataModelPermission.permissionRead(MessageDTO.class));

            communicationButton.setStyleName(CommunicationCrmTheme.StyleName.AllertButton.name());
            communicationButton.addStyleName(WidgetsTheme.StyleName.Button.name());

            exitAdminButton = new Button(i18n.tr("Exit Administration"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(CrmSite.getDefaultPlace());
                }
            });
            exitAdminButton.ensureDebugId("home");

            adminButton = new Button(i18n.tr("Administration"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(CrmSite.getAvalableAdministrationPlace());
                }
            }, CrmAdministrationAccess.class);
            adminButton.ensureDebugId("administration");

            languageButton = new Button("");

            languageButtonMenu = new ButtonMenuBar();
            languageButton.setMenu(languageButtonMenu);

            // from toolbar:

            rightToolbar.addItem(exitAdminButton);
            rightToolbar.addItem(adminButton);
            rightToolbar.addItem(userButton);
            rightToolbar.addItem(languageButton);
            rightToolbar.addItem(communicationButton);
            add(rightToolbar);
        }

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

    }

    @Override
    public void setPresenter(final HeaderPresenter presenter) {
        this.presenter = presenter;
        calculateActionsState();
    }

    @Override
    public void onLogedOut() {
        this.loggedIn = false;
        communicationButton.setVisible(false);
        support.setVisible(false);

        userButton.setVisible(false);
        userButton.setTextLabel("");

        rightToolbar.setSecurityContext(null);

        calculateActionsState();
    }

    @Override
    public void onLogedIn(String userName) {
        this.loggedIn = true;
        communicationButton.setVisible(true);

        support.setVisible(true);

        userButton.setVisible(true);
        userButton.setTextLabel(userName);

        rightToolbar.setSecurityContext(Context.visit(CrmUserVisit.class).getCurrentUser());

        calculateActionsState();
    }

    @Override
    public void setAvailableLocales(List<CompiledLocale> localeList) {
        languageButtonMenu.clearItems();
        for (final CompiledLocale compiledLocale : localeList) {
            languageButtonMenu.addItem(new MenuItem(compiledLocale.getNativeDisplayName(), new Command() {
                @Override
                public void execute() {
                    presenter.setLocale(compiledLocale);
                }
            }));
        }
        languageButton.setTextLabel(ClientLocaleUtils.getCurrentLocale().getNativeDisplayName());
    }

    private void doLayout(LayoutType layoutType) {
        this.layoutType = layoutType;
        calculateActionsState();
    }

    private void calculateActionsState() {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
        case tabletPortrait:
            sideMenuButton.setVisible(loggedIn);
            userButton.setVisible(false);
            if (presenter != null) {
                if (presenter.isAdminPlace()) {
                    communicationButton.setVisible(false);
                    exitAdminButton.setVisible(true);
                } else {
                    exitAdminButton.setVisible(false);
                    communicationButton.setVisible(true);
                }
            }
            adminButton.setVisible(false);
            exitAdminButton.setVisible(false);
            languageButton.setVisible(false);
            logoContainer.getElement().getStyle().setProperty("margin", "0 50%");
            break;
        default:
            sideMenuButton.setVisible(false);
            userButton.setVisible(loggedIn);
            if (presenter != null) {
                if (presenter.isAdminPlace()) {
                    exitAdminButton.setVisible(loggedIn);
                    adminButton.setVisible(false);
                } else {
                    exitAdminButton.setVisible(false);
                    adminButton.setVisible(loggedIn);
                }
            }
            languageButton.setVisible(loggedIn);
            communicationButton.setVisible(loggedIn);
            if (logoImage != null) {
                logoContainer.getElement().getStyle().setProperty("margin", "0 " + (logoImage.getOffsetWidth() / 2 + 20) + "px");
            }
            break;
        }
        setCommunicationMessagesCount(CrmClientCommunicationManager.instance().getLatestCommunicationNotification());
        communicationButton.setVisible(loggedIn);
    }

    @Override
    public void setCommunicationMessagesCount(CrmCommunicationSystemNotification status) {
        StringBuffer statusLabel = null;
        if (status != null) {
            if (status.numberOfNewDirectMessages > 0) {
                statusLabel = new StringBuffer();
                statusLabel.append(status.numberOfNewDirectMessages);
            }

            if (status.numberOfNewDispatchedMessages > 0) {
                if (statusLabel == null) {
                    statusLabel = new StringBuffer();
                } else {
                    statusLabel.append(" / ");
                }
                statusLabel.append(status.numberOfNewDispatchedMessages);
            }
        }
        if (statusLabel != null) {
            communicationButton.setImage(CrmImages.INSTANCE.alertsOn());
            communicationButton.setTextLabel(statusLabel.toString());
            communicationButton.addStyleDependentName(CommunicationCrmTheme.StyleDependent.alertOn.name());
        } else {
            communicationButton.setImage(CrmImages.INSTANCE.alertsOff());
            communicationButton.setTextLabel("");
            communicationButton.removeStyleDependentName(CommunicationCrmTheme.StyleDependent.alertOn.name());
        }
    }

}