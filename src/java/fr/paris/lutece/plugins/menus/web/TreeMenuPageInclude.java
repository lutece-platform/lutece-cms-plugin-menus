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

import fr.paris.lutece.plugins.menus.business.MenuItem;
import fr.paris.lutece.plugins.menus.business.Menus;
import fr.paris.lutece.plugins.menus.business.MenusHome;
import fr.paris.lutece.plugins.menus.service.MenusPlugin;
import fr.paris.lutece.plugins.menus.service.MenusService;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.PortalMenuService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This class provides the list of the page associated by the main menu of the
 * site
 */
public class TreeMenuPageInclude implements PageInclude
{
	// ///////////////////////////////////////////////////////////////////////////////////////////
	// Constants
	private static final String MENU_MARKER = "page_tree_menu";

	// Templates
	private static final String TEMPLATE_TREE_MENU_PAGES = "skin/plugins/menus/tree_menu_page_list.html";

	// Markers
	private static final String MARK_MENU = "menu";
	private static final String MARK_CURRENT_PAGE_ID = "current_page_id";
	private static final String MARK_ROOT_PAGE_ID = "root_page_id";

	private MenusService _menusService = CDI.current( ).select( MenusService.class ).get( );

	/**
	 * Substitue specific Freemarker markers in the page template.
	 * 
	 * @param rootModel
	 *                  the HashMap containing markers to substitute
	 * @param data
	 *                  A PageData object containing applications data
	 * @param nMode
	 *                  The current mode
	 * @param request
	 *                  The HTTP request
	 */
	@Override
	public void fillTemplate( Map < String, Object > rootModel, PageData data, int nMode, HttpServletRequest request )
	{
		if( request != null )
		{
			int nCurrentPageId;

			try
			{
				nCurrentPageId = ( request.getParameter( Parameters.PAGE_ID ) == null ) ? 0
						: Integer.parseInt( request.getParameter( Parameters.PAGE_ID ) );
			}
			catch( NumberFormatException nfe )
			{
				AppLogService.info( "MainMenuInclude.fillTemplate() : " + nfe.getLocalizedMessage( ) );
				nCurrentPageId = 0;
			}

			Plugin plugin = PluginService.getPlugin( MenusPlugin.PLUGIN_NAME );

			for( Menus menus : MenusHome.findAll( plugin ) )
			{
				if( menus.getMenuType( ).startsWith( MENU_MARKER ) )
				{
					String strTreeMenuPage = getTreeMenuPage( nCurrentPageId, nMode, menus, request );
					rootModel.put( menus.getMenuMarker( ), ( strTreeMenuPage == null ) ? "" : strTreeMenuPage );
				}
			}
		}
	}

	/**
	 * Builds the tree menu bar
	 *
	 * @param nIdPage
	 *                The page id
	 * @param nMode
	 *                the mode id
	 * @param menus
	 *                the current menu
	 * @param request
	 *                The HttpServletRequest
	 * @return The list of the tree menus rendered with the template of the plugin
	 */
	public String getTreeMenuPage( int nIdPage, int nMode, Menus menus, HttpServletRequest request )
	{
		MenuItem root = new MenuItem( );

		for( Page menuPage : PageHome.getChildPagesMinimalData( menus.getIdPageRoot( ) ) )
		{
			if( ( menuPage.isVisible( request ) ) || ( nMode == PortalMenuService.MODE_ADMIN ) )
			{
				root.addChild( buildMenuItem( menuPage, nMode, request ) );
			}
		}

		Locale locale = ( request == null ) ? null : request.getLocale( );

		Map < String, Object > model = new HashMap < String, Object >( );
		model.put( MARK_MENU, root );
		model.put( MARK_ROOT_PAGE_ID, menus.getIdPageRoot( ) );
		model.put( MARK_CURRENT_PAGE_ID, Integer.toString( nIdPage ) );

		// Define the site path from url, by mode
		model.put( MenusService.MARKER_SITE_PATH, _menusService.getSitePath( nMode ) );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TREE_MENU_PAGES, locale, model );

		return template.getHtml( );
	}

	/**
	 * Builds a menu item and its sub level menu items
	 *
	 * @param menuPage
	 *                 The page of the menu item
	 * @param nMode
	 *                 the mode id
	 * @param request
	 *                 The HttpServletRequest
	 * @return The menu item
	 */
	private MenuItem buildMenuItem( Page menuPage, int nMode, HttpServletRequest request )
	{
		MenuItem menuItem = new MenuItem( );
		menuItem.setPage( menuPage );

		Collection < Page > listSubLevelMenuPages = PageHome.getChildPagesMinimalData( menuPage.getId( ) );

		for( Page subLevelMenuPage : listSubLevelMenuPages )
		{
			if( ( subLevelMenuPage.isVisible( request ) ) || ( nMode == PortalMenuService.MODE_ADMIN ) )
			{
				MenuItem subMenuItem = new MenuItem( );
				subMenuItem.setPage( subLevelMenuPage );
				menuItem.addChild( subMenuItem );
			}
		}

		return menuItem;
	}
}
