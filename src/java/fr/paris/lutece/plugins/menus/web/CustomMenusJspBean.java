/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.menus.web;

import fr.paris.lutece.plugins.menus.business.CustomMenu;
import fr.paris.lutece.plugins.menus.business.CustomMenuHome;
import fr.paris.lutece.plugins.menus.business.CustomMenuItem;
import fr.paris.lutece.plugins.menus.business.CustomMenuItemHome;
import fr.paris.lutece.plugins.menus.service.CustomMenuService;
import fr.paris.lutece.plugins.menus.web.validator.ValidatorCustomItemForm;
import fr.paris.lutece.portal.service.cache.CacheService;
import fr.paris.lutece.portal.service.cache.CacheableService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * Custom Menus JSP Bean using MVC annotations
 */
@Controller( controllerJsp = "ManageCustomMenus.jsp", controllerPath = "jsp/admin/plugins/menus/", right = "CUSTOM_MENUS_MANAGEMENT" )
public class CustomMenusJspBean extends PaginatedJspBean < Integer, Object >
{

	private static final long serialVersionUID = 2L;

	public static final String RIGHT_MANAGE_CUSTOM_MENUS = "CUSTOM_MENUS_MANAGEMENT";

	// Views
	private static final String VIEW_MANAGE_CUSTOM_MENUS = "manageCustomMenus";
	private static final String VIEW_CREATE_CUSTOM_MENU = "createCustomMenu";
	private static final String VIEW_MODIFY_CUSTOM_MENU = "modifyCustomMenu";
	private static final String VIEW_CONFIRM_REMOVE_CUSTOM_MENU = "removeCustomMenu";
	private static final String VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS = "createCustomMenuWithItems";
	private static final String VIEW_MODIFY_CUSTOM_MENU_ITEM = "modifyCustomMenuItem";
	private static final String VIEW_MODIFY_CUSTOM_MENU_WITH_ITEMS = "modifyCustomMenuWithItems";
	private static final String VIEW_CONFIRM_REMOVE_CUSTOM_MENU_ITEM = "removeCustomMenuItem";

	// Actions
	private static final String ACTION_CREATE_CUSTOM_MENU = "createCustomMenu";
	private static final String ACTION_MODIFY_CUSTOM_MENU = "modifyCustomMenu";
	private static final String ACTION_REMOVE_CUSTOM_MENU = "removeCustomMenu";
	private static final String ACTION_CREATE_CUSTOM_MENU_ITEMS = "createCustomMenuItems";
	private static final String ACTION_MODIFY_CUSTOM_MENU_ITEM = "modifyCustomMenuItem";
	private static final String ACTION_REMOVE_CUSTOM_MENU_ITEM = "removeCustomMenuItem";
	private static final String ACTION_SEARCH_ITEMS = "searchItems";
	private static final String ACTION_CHANGE_ITEMS_ORDER = "changeMenuItemsOrder";

	// Marks
	private static final String MARK_ID_CUSTOM_MENU = "id_current_custom_menu";
	private static final String MARK_CUSTOM_MENUS_LIST = "custom_menus_list";
	private static final String MARK_CUSTOM_MENU = "custom_menu";
	private static final String MARK_CUSTOM_MENU_ITEM = "custom_menu_item";
	private static final String MARK_CUSTOM_MENU_ITEMS_LIST = "custom_menu_items_list";
	private static final String MARK_MENU_TYPES_LIST = "menu_types_list";
	private static final String MARK_ITEM_TYPES_LIST = "item_types_list";
	private static final String MARK_AVAILABLE_MENUS_LIST = "available_menus_list";
	private static final String MARK_AVAILABLE_XPAGES_LIST = "available_xpages_list";
	private static final String MARK_AVAILABLE_PAGES_LIST = "available_pages_list";
	private static final String MARK_CREATE_CUSTOM_MENU_ITEM_ERROR = "create_items_errors_list";
	private static final String MARK_MODIFY_CUSTOM_MENU_ITEM_ERROR = "modify_items_errors_list";
	private static final String MARK_SEARCH_CRITERIA = "search_criteria";

	// Parameters
	private static final String PARAMETER_ID = "id";
	private static final String PARAMETER_SEARCH_CRITERIA = "search";
	private static final String PARAMETER_ACTION_CREATE_CUSTOM_MENU_BUTTON = "actionCreateCustomMenuButton";
	private static final String PARAMETER_ACTION_MODIFY_CUSTOM_MENU_BUTTON = "actionModifyCustomMenuButton";
	private static final String PARAMETER_ACTION_SEARCH_ITEMS_BUTTON = "searchItemsButton";
	private static final String PARAMETER_ORDER_ID = "order_id";

	// Button action value
	private static final String VALUE_ACTION_CREATE_CUSTOM_MENU_BUTTON = "create_custom_menu_with_items";
	private static final String VALUE_ACTION_MODIFY_CUSTOM_MENU_BUTTON = "modify_custom_menu_with_items";
	private static final String VALUE_CLEAN_BUTTON = "clean";

	// Templates
	private static final String TEMPLATE_MANAGE_CUSTOM_MENUS = "/admin/plugins/menus/manage_custom_menus.html";
	private static final String TEMPLATE_CREATE_CUSTOM_MENU = "/admin/plugins/menus/create_custom_menu.html";
	private static final String TEMPLATE_MODIFY_CUSTOM_MENU = "/admin/plugins/menus/modify_custom_menu.html";
	private static final String TEMPLATE_CREATE_CUSTOM_MENU_WITH_ITEMS = "/admin/plugins/menus/create_custom_menu_with_items.html";
	private static final String TEMPLATE_MODIFY_CUSTOM_MENU_ITEM = "/admin/plugins/menus/modify_custom_menu_item.html";
	private static final String TEMPLATE_MODIFY_CUSTOM_MENU_WITH_ITEMS = "/admin/plugins/menus/modify_custom_menu_with_items.html";

	
	// Properties for page titles
	private static final String PROPERTY_PAGE_TITLE_MANAGE_CUSTOM_MENUS = "menus.manage_custom_menus.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_CREATE_CUSTOM_MENU = "menus.create_custom_menu.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_CREATE_CUSTOM_MENU_WITH_ITEMS = "menus.create_custom_menu_with_items.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_MODIFY_CUSTOM_MENU = "menus.modify_custom_menu.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_MODIFY_CUSTOM_MENU_WITH_ITEMS = "menus.modify_custom_menu_with_items.pageTitle";

	// Jsp paths
	private static final String JSP_MANAGE_MENUS = "jsp/admin/plugins/menus/ManageCustomMenus.jsp";
	private static final String JSP_CREATE_ITEM = "jsp/admin/plugins/menus/ManageCustomMenus.jsp?view=createCustomMenuWithItems";

	// Info messages
	private static final String INFO_CUSTOM_MENU_CREATED = "menus.info.custom_menu.created";
	private static final String INFO_CUSTOM_MENU_UPDATED = "menus.info.custom_menu.updated";
	private static final String INFO_CUSTOM_MENU_REMOVED = "menus.info.custom_menu.removed";
	private static final String INFO_CUSTOM_MENU_ITEM_CREATED = "menus.info.custom_menu_item.created";
	private static final String INFO_CUSTOM_MENU_ITEM_UPDATED = "menus.info.custom_menu_item.updated";
	private static final String INFO_CUSTOM_MENU_ITEM_REMOVED = "menus.info.custom_menu_item.removed";
	private static final String EMPTY_MENU_TYPE = "menus.constant_custom_menu_item.type.emtpy";
	private static final String MESSAGE_CONFIRM_REMOVE_MENU = "menus.manage_custom_menus.confirmRemove";
	private static final String MESSAGE_CONFIRM_REMOVE_MENU_ITEM = "menus.manage_custom_menu_items.confirmRemove";
	private static final String MESSAGE_BOOKMARK_NOT_UNIQUE = "menus.manage_custom_menu.bookmark.notUnique";

	// Validation
	private static final String VALIDATION_ATTRIBUTES_PREFIX = "menus.model.entity.custom_menu";

	// Constants
	private static final String MENU_TYPE_MAIN = "main";
	private static final String MENU_TYPE_SUBMENU = "submenu";
	private static final String MENU_TYPE_INTERNAL = "internal";
	private static final String MENU_ITEM_TYPE_EMPTY = "";
	private static final String MENU_ITEM_TYPE_XPAGE = "xpage";
	private static final String MENU_ITEM_TYPE_PAGE = "page";
	private static final String MENU_ITEM_TYPE_EXTERNAL_URL = "external_url";
	private static final String MENU_ITEM_TYPE_MENU = "menu";
	private static final Integer ID_CACHE_PAGE_SERVICE_CACHE = 2;
	
	// Instance variable for custom menu
	private CustomMenu _currentCustomMenu;
	private CustomMenuItem _currentCustomMenuItem;
	private ReferenceList _listMenuTypes;
	private ReferenceList _listMenuItemTypes;
	private ValidatorCustomItemForm _itemValidator;
	private String _strFilterCriteria;

	// /////////////////////////////////////////////////
	// ////////////////CUSTOM_MENUS/////////////////////
	// /////////////////////////////////////////////////

	// /////////////MANAGE_CUSTOM_MENUS/////////////////

	/**
	 * Returns the list of custom menus
	 *
	 * @param request The Http request
	 * @return the View
	 */
	@View( value = VIEW_MANAGE_CUSTOM_MENUS, defaultView = true )
	public String getManageCustomMenus( HttpServletRequest request )
	{
		_currentCustomMenu = null;
		_currentCustomMenuItem = null;

		initReferenceLists( ); // init constant lists : _listMenuTypes and _listMenuItemTypes

		List < Integer > listCustomMenusIds = CustomMenuHome.getIdMenusList( );

		Map < String, Object > model = getPaginatedListModelForCustomMenu( request, MARK_CUSTOM_MENUS_LIST,
				listCustomMenusIds,
				JSP_MANAGE_MENUS );
		model.put( MARK_MENU_TYPES_LIST, _listMenuTypes );

		return getPage( PROPERTY_PAGE_TITLE_MANAGE_CUSTOM_MENUS, TEMPLATE_MANAGE_CUSTOM_MENUS, model );
	}

	// /////////////CREATE_CUSTOM_MENU/////////////////

	/**
	 * Returns the form to create a custom menu
	 *
	 * @param request The Http request
	 * @return the View
	 */
	@View( VIEW_CREATE_CUSTOM_MENU )
	public String getCreateCustomMenu( HttpServletRequest request )
	{
		initReferenceLists( ); // init constant lists : _listMenuTypes and _listMenuItemTypes

		Map < String, Object > model = getModel( );
		model.put( MARK_MENU_TYPES_LIST, _listMenuTypes );

		return getPage( PROPERTY_PAGE_TITLE_CREATE_CUSTOM_MENU, TEMPLATE_CREATE_CUSTOM_MENU, model );
	}

	/**
	 * Process the data capture form of a new custom menu
	 *
	 * @param request The Http Request
	 * @return The JSP URL which displays the process result
	 */
	@Action( ACTION_CREATE_CUSTOM_MENU )
	public String doCreateCustomMenu( HttpServletRequest request )
	{
		_currentCustomMenu = new CustomMenu( );
		populate( _currentCustomMenu, request, getLocale( ) );
		Boolean bIsUniqueBookmark = CustomMenuHome.isUniqueBookmark( _currentCustomMenu, true );

		if( ! validateBean( _currentCustomMenu, VALIDATION_ATTRIBUTES_PREFIX ) || !bIsUniqueBookmark )
		{
			if( !bIsUniqueBookmark )
			{
				addError( I18nService.getLocalizedString( MESSAGE_BOOKMARK_NOT_UNIQUE, getLocale( ) ) );
			}
			return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
		}

		CustomMenuHome.create( _currentCustomMenu );
		addInfo( INFO_CUSTOM_MENU_CREATED, getLocale( ) );
		resetPagesCache( );
		
		String action = request.getParameter( PARAMETER_ACTION_CREATE_CUSTOM_MENU_BUTTON );

		if( StringUtils.equals( VALUE_ACTION_CREATE_CUSTOM_MENU_BUTTON, action ) )
		{
			return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
		}
		else
		{
			return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
		}
	}

	// /////////////MODIFY_CUSTOM_MENU/////////////////

	/**
	 * Returns the form to modify a custom menu
	 *
	 * @param request The Http request
	 * @return the View
	 */
	@View( VIEW_MODIFY_CUSTOM_MENU )
	public String getModifyCustomMenu( HttpServletRequest request )
	{
		initReferenceLists( ); // init constant lists : _listMenuTypes and _listMenuItemTypes
		
		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID ) );
		_currentCustomMenu = CustomMenuHome.findByPrimaryKey( nId );
		 
		Map < String, Object > model = getModel( );
		model.put( MARK_CUSTOM_MENU, _currentCustomMenu );
		model.put( MARK_MENU_TYPES_LIST, _listMenuTypes );

		return getPage( PROPERTY_PAGE_TITLE_MODIFY_CUSTOM_MENU, TEMPLATE_MODIFY_CUSTOM_MENU, model );
	}

	/**
	 * Process the data capture form of a custom menu to modify
	 *
	 * @param request The Http Request
	 * @return The JSP URL which displays the process result
	 */
	@Action( ACTION_MODIFY_CUSTOM_MENU )
	public String doModifyCustomMenu( HttpServletRequest request )
	{
		
		_currentCustomMenu = new CustomMenu( );
		populate( _currentCustomMenu, request, getLocale( ) );
		Boolean bIsUniqueBookmark = CustomMenuHome.isUniqueBookmark( _currentCustomMenu, false );
		
		if( ! validateBean( _currentCustomMenu, VALIDATION_ATTRIBUTES_PREFIX ) || !bIsUniqueBookmark )
		{
			if( !bIsUniqueBookmark )
			{
				addError( I18nService.getLocalizedString( MESSAGE_BOOKMARK_NOT_UNIQUE, getLocale( ) ) );
			}
			return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
		}
		
		CustomMenuHome.update( _currentCustomMenu );
		addInfo( INFO_CUSTOM_MENU_UPDATED, getLocale( ) );
		resetPagesCache( );
		
		String action = request.getParameter( PARAMETER_ACTION_MODIFY_CUSTOM_MENU_BUTTON );

		if( StringUtils.equals( VALUE_ACTION_MODIFY_CUSTOM_MENU_BUTTON, action ) )
		{
			return redirectView( request, VIEW_MODIFY_CUSTOM_MENU_WITH_ITEMS );
		}
		else
		{
			return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
		}
	}

	/**
	 * Returns the form to modify a custom menu
	 *
	 * @param request The Http request
	 * @return the View
	 */
	@View( VIEW_MODIFY_CUSTOM_MENU_WITH_ITEMS )
	public String getModifyCustomMenuWithItems( HttpServletRequest request )
	{
		
		if( _currentCustomMenu == null )
		{
			return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
		}
		
		initReferenceLists( ); // init constant lists : _listMenuTypes and _listMenuItemTypes
	
		List < Integer > listCustomMenuItemsIds = CustomMenuItemHome.getCustomMenuItemsIdsListByMenuId( _currentCustomMenu.getId( ) );
		
		Map < String, Object > model = getPaginatedListModelForCustomMenuItem( request, MARK_CUSTOM_MENU_ITEMS_LIST,
		listCustomMenuItemsIds, JSP_CREATE_ITEM );
		model.put( MARK_ID_CUSTOM_MENU, _currentCustomMenu.getId( ) );
		model.put( MARK_ITEM_TYPES_LIST, _listMenuItemTypes );
		model.put( MARK_AVAILABLE_PAGES_LIST, CustomMenuService.getInstance( ).getAvailablePagesReferenceList( _strFilterCriteria ) );
		model.put( MARK_AVAILABLE_XPAGES_LIST, CustomMenuService.getInstance( ).getAvailableXpagesReferenceList( _strFilterCriteria ) );
		model.put( MARK_AVAILABLE_MENUS_LIST, CustomMenuService.getInstance( ).getAvailableMenusReferenceList( _currentCustomMenu, _strFilterCriteria ) );
		model.put( MARK_SEARCH_CRITERIA, _strFilterCriteria );
		
		if( _itemValidator != null )
		{
		model.put( MARK_CREATE_CUSTOM_MENU_ITEM_ERROR, _itemValidator.getListErrors( ) );
		_itemValidator = null;
		}
		
		return getPage( PROPERTY_PAGE_TITLE_MODIFY_CUSTOM_MENU_WITH_ITEMS, TEMPLATE_MODIFY_CUSTOM_MENU_WITH_ITEMS,model );
	}
	
	// /////////////REMOVE_CUSTOM_MENU/////////////////

	/**
	 * Manages the removal form of a custom menu whose identifier is in the http
	 * request
	 *
	 * @param request The Http request
	 * @return the html code to confirm
	 */
	@View( VIEW_CONFIRM_REMOVE_CUSTOM_MENU )
	public String getConfirmRemoveCustomMenu( HttpServletRequest request )
	{
		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID ) );
		UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CUSTOM_MENU ) );
		url.addParameter( PARAMETER_ID, nId );

		String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_MENU,
				url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

		return redirect( request, strMessageUrl );
	}

	/**
	 * Handles the removal form of a custom menu
	 *
	 * @param request The Http request
	 * @return the JSP URL to display the form to manage custom menus
	 */
	@Action( ACTION_REMOVE_CUSTOM_MENU )
	public String doRemoveCustomMenu( HttpServletRequest request )
	{
		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID ) );
		CustomMenuHome.remove( nId );
		addInfo( INFO_CUSTOM_MENU_REMOVED, getLocale( ) );
		resetPagesCache( );
		
		return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
	}

	// /////////////////////////////////////////////////////
	// ////////////////CUSTOM_MENU_ITEMS////////////////////
	// /////////////////////////////////////////////////////

	// /////////////CREATE_CUSTOM_MENU_ITEMS////////////////

	/**
	 * Returns the form to create a custom menu
	 *
	 * @param request The Http request
	 * @return the View
	 */
	@View( VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS )
	public String getCreateCustomMenuWithItems( HttpServletRequest request )
	{
		initReferenceLists( ); // init constant lists : _listMenuTypes and _listMenuItemTypes

		if( _currentCustomMenu == null )
		{
			return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
		}

		List < Integer > listCustomMenuItemsIds = CustomMenuItemHome
				.getCustomMenuItemsIdsListByMenuId( _currentCustomMenu.getId( ) );

		Map < String, Object > model = getPaginatedListModelForCustomMenuItem( request, MARK_CUSTOM_MENU_ITEMS_LIST,
				listCustomMenuItemsIds, JSP_CREATE_ITEM );
		model.put( MARK_ID_CUSTOM_MENU, _currentCustomMenu.getId( ) );
		model.put( MARK_ITEM_TYPES_LIST, _listMenuItemTypes );
		model.put( MARK_AVAILABLE_PAGES_LIST, CustomMenuService.getInstance( ).getAvailablePagesReferenceList( _strFilterCriteria ) );
		model.put( MARK_AVAILABLE_XPAGES_LIST, CustomMenuService.getInstance( ).getAvailableXpagesReferenceList( _strFilterCriteria ) );
		model.put( MARK_AVAILABLE_MENUS_LIST, CustomMenuService.getInstance( ).getAvailableMenusReferenceList(  _currentCustomMenu ,_strFilterCriteria ) );
		model.put( MARK_SEARCH_CRITERIA, _strFilterCriteria );

		if( _itemValidator != null )
		{
			model.put( MARK_CREATE_CUSTOM_MENU_ITEM_ERROR, _itemValidator.getListErrors( ) );
			_itemValidator = null;
		}

		return getPage( PROPERTY_PAGE_TITLE_CREATE_CUSTOM_MENU_WITH_ITEMS, TEMPLATE_CREATE_CUSTOM_MENU_WITH_ITEMS,
				model );
	}

	/**
	 * Process the data capture form of a custom menu to create
	 *
	 * @param request The Http Request
	 * @return The JSP URL which displays the process result
	 */
	@Action( ACTION_CREATE_CUSTOM_MENU_ITEMS )
	public String docreateCustomMenuItems( HttpServletRequest request )
	{
		_itemValidator = new ValidatorCustomItemForm( );
		_currentCustomMenuItem = new CustomMenuItem( );
		populate( _currentCustomMenuItem, request, getLocale( ) );

		// Validate the bean
		if( ! validateBean( _currentCustomMenuItem, VALIDATION_ATTRIBUTES_PREFIX )
				|| ! _itemValidator.isValid( _currentCustomMenuItem, getLocale( ) ) )
		{
			return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
		}

		CustomMenuItemHome.create( _currentCustomMenuItem );
		addInfo( INFO_CUSTOM_MENU_ITEM_CREATED, getLocale( ) );
		resetPagesCache( );
		
		_currentCustomMenuItem = null;
		return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
	}

	// /////////////MODIFY_CUSTOM_MENU_ITEM////////////////

	/**
	 * Returns the form to modify a custom menu item
	 *
	 * @param request The Http request
	 * @return the View
	 */
	@View( VIEW_MODIFY_CUSTOM_MENU_ITEM )
	public String getModifyCustomMenuItem( HttpServletRequest request )
	{
		if( _currentCustomMenu == null )
		{
			return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
		}

		initReferenceLists( ); // init constant lists : _listMenuTypes and _listMenuItemTypes

		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID ) );
		_currentCustomMenuItem = CustomMenuItemHome.findByPrimaryKey( nId );

		if( _currentCustomMenuItem == null || _currentCustomMenuItem.getParentMenuId( ) != _currentCustomMenu.getId( ) )
		{
			// The item does not exist or does not belong to the current menu
			return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
		}

		Map < String, Object > model = getModel( );
		model.put( MARK_ID_CUSTOM_MENU, _currentCustomMenu.getId( ) );
		model.put( MARK_CUSTOM_MENU_ITEM, _currentCustomMenuItem );
		model.put( MARK_ITEM_TYPES_LIST, _listMenuItemTypes );
		model.put( MARK_AVAILABLE_PAGES_LIST, CustomMenuService.getInstance( ).getAvailablePagesReferenceList( _strFilterCriteria ) );
		model.put( MARK_AVAILABLE_XPAGES_LIST, CustomMenuService.getInstance( ).getAvailableXpagesReferenceList( _strFilterCriteria ) );
		model.put( MARK_AVAILABLE_MENUS_LIST, CustomMenuService.getInstance( ).getAvailableMenusReferenceList(  _currentCustomMenu ,_strFilterCriteria ) );

		if( _itemValidator != null )
		{
			model.put( MARK_MODIFY_CUSTOM_MENU_ITEM_ERROR, _itemValidator.getListErrors( ) );
			_itemValidator = null;
		}

		return getPage( PROPERTY_PAGE_TITLE_MODIFY_CUSTOM_MENU, TEMPLATE_MODIFY_CUSTOM_MENU_ITEM, model );
	}

	/**
	 * Process the data capture form of a custom menu item to modify
	 *
	 * @param request The Http Request
	 * @return The JSP URL which displays the process result
	 */
	@Action( ACTION_MODIFY_CUSTOM_MENU_ITEM )
	public String doModifyCustomMenuItem( HttpServletRequest request )
	{
		_itemValidator = new ValidatorCustomItemForm( );
		_currentCustomMenuItem = new CustomMenuItem( );
		populate( _currentCustomMenuItem, request, getLocale( ) );

		// Validate the bean
		if( ! validateBean( _currentCustomMenuItem, VALIDATION_ATTRIBUTES_PREFIX )
				|| ! _itemValidator.isValid( _currentCustomMenuItem, getLocale( ) ) )
		{
			return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
		}

		CustomMenuItemHome.update( _currentCustomMenuItem );
		addInfo( INFO_CUSTOM_MENU_ITEM_UPDATED, getLocale( ) );
		resetPagesCache( );
		
		_currentCustomMenuItem = null;
		return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
	}

	// /////////////REMOVE_CUSTOM_MENU_ITEM////////////////

	/**
	 * Returns the page with confirmation to remove message for a custom menu item
	 *
	 * @param request The Http request
	 * @return the View
	 */
	@View( VIEW_CONFIRM_REMOVE_CUSTOM_MENU_ITEM )
	public String getConfirmRemoveCustomMenuItem( HttpServletRequest request )
	{
		int nId = Integer.parseInt( request.getParameter( PARAMETER_ID ) );
		UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CUSTOM_MENU_ITEM ) );
		url.addParameter( PARAMETER_ID, nId );

		String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_MENU_ITEM,
				url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

		return redirect( request, strMessageUrl );
	}

	/**
	 * Handles the removal form of a custom menu item
	 *
	 * @param request The Http request
	 * @return the JSP URL to display the form to manage custom menus
	 */
	@Action( ACTION_REMOVE_CUSTOM_MENU_ITEM )
	public String doRemoveCustomMenuItem( HttpServletRequest request )
	{
		// Retrieve the ID of the item to delete
		int nItemId = Integer.parseInt( request.getParameter( PARAMETER_ID ) );

		// Retrieve the item to delete to obtain its order

		CustomMenuItem itemToRemove = CustomMenuItemHome.findByPrimaryKey( nItemId );
		if( itemToRemove == null )
		{
			// Item doesn't exist
			return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
		}

		// Check that the current menu exists and that the item belongs to this menu
		if( _currentCustomMenu == null || itemToRemove.getParentMenuId( ) != _currentCustomMenu.getId( ) )
		{
			return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
		}

		int removedItemOrder = itemToRemove.getOrder( );

		// Delete the item from the database
		CustomMenuItemHome.remove( nItemId );

		// Retrieve all items in the current menu after deletion
		List < CustomMenuItem > menuItems = CustomMenuItemHome
				.getCustomMenuItemsListByMenuId( _currentCustomMenu.getId( ) );

		// Update the order: decrement by 1 for all items with an order higher than the
		// one deleted.
		for( CustomMenuItem item : menuItems )
		{
			if( item.getOrder( ) > removedItemOrder )
			{
				item.setOrder( item.getOrder( ) - 1 );
				CustomMenuItemHome.update( item );
			}
		}

		addInfo( INFO_CUSTOM_MENU_ITEM_REMOVED, getLocale( ) );
		resetPagesCache( );
		
		return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
	}

	// /////////////////////////////////////////
	// /////////////SEARCH_ITEMS////////////////
	// /////////////////////////////////////////

	/**
	 * Set strFilterCriteria variable for the search of pages, xpages, and menus
	 *
	 * @param request The Http request
	 * @return JSON response with search results
	 */
	@Action( ACTION_SEARCH_ITEMS )
	public String doSearchItems( HttpServletRequest request )
	{
		String strButtonValue = request.getParameter( PARAMETER_ACTION_SEARCH_ITEMS_BUTTON );

		if( StringUtils.equals( strButtonValue, VALUE_CLEAN_BUTTON ) )
		{
			_strFilterCriteria = null;
		}
		else
		{
			_strFilterCriteria = request.getParameter( PARAMETER_SEARCH_CRITERIA );
		}

		return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
	}

	// ///////////////////////////////////////////////
	// /////////////CHANGE_ITEMS_ORDER////////////////
	// ///////////////////////////////////////////////

	/**
	 * Change order items
	 *
	 * @param request The Http request
	 * @return a view
	 */
	@Action( ACTION_CHANGE_ITEMS_ORDER )
	public String doChangeItemOrder( HttpServletRequest request )
	{

		if( _currentCustomMenu == null )
		{
			return redirectView( request, VIEW_MANAGE_CUSTOM_MENUS );
		}

		int nItemId = Integer.parseInt( request.getParameter( PARAMETER_ID ) );
		int nNewOrder = Integer.parseInt( request.getParameter( PARAMETER_ORDER_ID ) );

		// Get item to change
		CustomMenuItem itemToMove = CustomMenuItemHome.findByPrimaryKey( nItemId );
		if( itemToMove == null || itemToMove.getParentMenuId( ) != _currentCustomMenu.getId( ) )
		{
			// Item doesn't find
			return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
		}

		// Get all item of the current menu
		List < CustomMenuItem > menuItems = CustomMenuItemHome
				.getCustomMenuItemsListByMenuId( _currentCustomMenu.getId( ) );

		// Get the item whose has the order choosen for the item to change
		CustomMenuItem itemWithTargetOrder = null;
		for( CustomMenuItem item : menuItems )
		{
			if( item.getOrder( ) == nNewOrder && item.getId( ) != nItemId )
			{
				itemWithTargetOrder = item;
				break;
			}
		}

		// Exchange of orders
		int oldOrder = itemToMove.getOrder( );
		itemToMove.setOrder( nNewOrder );
		CustomMenuItemHome.update( itemToMove );

		// If an item already occupied this position, it is given the old order
		if( itemWithTargetOrder != null )
		{
			itemWithTargetOrder.setOrder( oldOrder );
			CustomMenuItemHome.update( itemWithTargetOrder );
		}

		resetPagesCache( );
		return redirectView( request, VIEW_CREATE_CUSTOM_MENU_WITH_ITEMS );
	}

	// /////////////////////////////////////////
	// ///////////INIT REFERENCE LISTS//////////
	// /////////////////////////////////////////

	/**
	 * Init reference list fo select menu types and item types
	 * 
	 */
	private void initReferenceLists( )
	{
		if( _listMenuTypes == null )
		{
			initMenuTypeReferenceList( );
		}
		if( _listMenuItemTypes == null )
		{
			initMenuItemTypeReferenceList( );
		}

	}

	/**
	 * Get the type reference list
	 * 
	 * @return the reference list
	 */
	private void initMenuTypeReferenceList( )
	{
		_listMenuTypes = new ReferenceList( );
		_listMenuTypes.addItem( MENU_TYPE_MAIN, I18nService.getLocalizedString( CustomMenu.TYPE_MAIN, getLocale( ) ) );
		_listMenuTypes.addItem( MENU_TYPE_INTERNAL,
				I18nService.getLocalizedString( CustomMenu.TYPE_INTERNAL, getLocale( ) ) );
		_listMenuTypes.addItem( MENU_TYPE_SUBMENU,
				I18nService.getLocalizedString( CustomMenu.TYPE_SUBMENU, getLocale( ) ) );
	}

	/**
	 * Get the item type reference list
	 * 
	 * @return the reference list
	 */
	private void initMenuItemTypeReferenceList( )
	{
		_listMenuItemTypes = new ReferenceList( );
		_listMenuItemTypes.addItem( MENU_ITEM_TYPE_EMPTY,
				I18nService.getLocalizedString( EMPTY_MENU_TYPE, getLocale( ) ) );
		_listMenuItemTypes.addItem( MENU_ITEM_TYPE_XPAGE,
				I18nService.getLocalizedString( CustomMenuItem.TYPE_XPAGE, getLocale( ) ) );
		_listMenuItemTypes.addItem( MENU_ITEM_TYPE_PAGE,
				I18nService.getLocalizedString( CustomMenuItem.TYPE_PAGE, getLocale( ) ) );
		_listMenuItemTypes.addItem( MENU_ITEM_TYPE_EXTERNAL_URL,
				I18nService.getLocalizedString( CustomMenuItem.TYPE_EXTERNAL_URL, getLocale( ) ) );
		_listMenuItemTypes.addItem( MENU_ITEM_TYPE_MENU,
				I18nService.getLocalizedString( CustomMenuItem.TYPE_MENU, getLocale( ) ) );
	}

	// ////////////////////////////////////////////
	// ///////////////PAGINATORS AND TOOLS/////////
	// ////////////////////////////////////////////

	// /////////PAGINATOR FOR CUSTOM MENU//////////

	@Override
	List < Object > getItemsFromIds( List < Integer > listIds, String strBookmark )
	{
		if( StringUtils.equals( strBookmark, MARK_CUSTOM_MENU_ITEMS_LIST ) )
		{
			return getMenuItemsListFromIds( listIds );
		}
		else
		{
			return getMenusListFromIds( listIds );
		}
	}

	/**
	 * Get MenuList from a list ids of menu
	 * 
	 * @param listIds
	 *                list of Ids
	 * @return menu list
	 */
	private List < Object > getMenusListFromIds( List < Integer > listIds )
	{

		List < CustomMenu > listCustomMenus = CustomMenuHome.getMenusListByIds( listIds );
		Map < String, String > mapMenuType = _listMenuTypes.toMap( );

		// Set Type values according to message ressource file.
		for( CustomMenu cm : listCustomMenus )
		{
			cm.setType( mapMenuType.containsKey( cm.getType( ) ) ? mapMenuType.get( cm.getType( ) ) : "" );
		}

		// keep original order
		return listCustomMenus.stream( ).sorted( Comparator.comparingInt( menu -> listIds.indexOf( menu.getId( ) ) ) )
				.collect( Collectors.toList( ) );
	}

	/**
	 * Get ItemList from a list ids of items
	 * 
	 * @param listIds
	 *                list of Ids
	 * @return item list
	 */
	private List < Object > getMenuItemsListFromIds( List < Integer > listIds )
	{
		Collection < CustomMenuItem > listCustomMenuItems = CustomMenuItemHome
				.getCustomMenuItemsListByMenuId( _currentCustomMenu.getId( ) );

		// keep original order
		return listCustomMenuItems.stream( )
				.sorted( Comparator.comparingInt( item -> listIds.indexOf( item.getId( ) ) ) )
				.collect( Collectors.toList( ) );
	}
	
	
	/**
	 * Reset Cache of Page Service Cache
	 * 
	 */	
	private void resetPagesCache( )
	{
        CacheableService cs = CacheService.getCacheableServicesList( ).get( ID_CACHE_PAGE_SERVICE_CACHE );
        
        if( cs != null )
        {
        	cs.resetCache( );
        }
	}
}
