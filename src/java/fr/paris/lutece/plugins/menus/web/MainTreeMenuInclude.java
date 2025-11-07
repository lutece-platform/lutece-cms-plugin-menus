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
import fr.paris.lutece.plugins.menus.service.MainTreeMenuService;
import fr.paris.lutece.plugins.menus.service.MenusService;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

/**
 * MainTreeMenuInclude
 */
public class MainTreeMenuInclude implements PageInclude
{
	// ///////////////////////////////////////////////////////////////////////////////////////////
	// Constants

	// Templates
	private static final String TEMPLATE_MENU_PAGES = "skin/plugins/menus/main_tree_pages_list.html";
	private static final String TEMPLATE_MENU_PAGES_TREE = "skin/plugins/menus/main_tree_pages_list_tree.html";

	// Parameters
	private static final String PARAMETER_CURRENT_PAGE_ID = "current_page_id";

	// Markers
	private static final String MARK_MENU = "menu";
	private static final String MARK_MENU_NAME = "menu_name";
	private static final String MARK_CURRENT_PAGE_ID = "current_page_id";
	private static final String MARK_ROOT_PAGE_ID = "root_page_id";
	private static final String MARK_PAGE_MENU_MAIN = "page_tree_menu_main";
	private static final String MARK_PAGE_MENU_TREE = "page_tree_menu_tree";

	private MainTreeMenuService _mainTreeMenuService = CDI.current( ).select( MainTreeMenuService.class ).get( );

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

			/*
			 * test parameter name: page_id parameter for a PageContentService,
			 * current_page_id for a DocumentContentService
			 */
			String strParameterPageId = ( request.getParameter( PARAMETER_CURRENT_PAGE_ID ) == null )
					? Parameters.PAGE_ID
					: PARAMETER_CURRENT_PAGE_ID;

			try
			{
				nCurrentPageId = ( request.getParameter( strParameterPageId ) == null ) ? 0
						: Integer.parseInt( request.getParameter( strParameterPageId ) );
			}
			catch( NumberFormatException nfe )
			{
				AppLogService.info( "MainMenuInclude.fillTemplate() : " + nfe.getLocalizedMessage( ) );
				nCurrentPageId = 0;
			}

			rootModel.put( MARK_PAGE_MENU_MAIN, getMainPageList( nCurrentPageId, nMode, request ) );
			rootModel.put( MARK_PAGE_MENU_TREE, getTreePageList( nCurrentPageId, nMode, request ) );
		}
	}

	/**
	 * Display the list of childpages pages for first level of childpages
	 * 
	 * @param nCurrentPageId
	 *                       The current page id
	 * @param nMode
	 *                       The current mode
	 * @param request
	 *                       The HTTP request
	 * @return the list of childpages
	 */
	private String getMainPageList( int nCurrentPageId, int nMode, HttpServletRequest request )
	{

		HashMap < String, Object > modelList = new HashMap < String, Object >( );
		Locale locale = null;
		if( request != null )
		{
			locale = request.getLocale( );
		}

		// Define the root tree for each childpages of root page
		int nRootParentTree = _mainTreeMenuService.getRootParentTree( nCurrentPageId );

		MenuItem root = _mainTreeMenuService.getMainMenuItems( );

		modelList.put( MARK_MENU, root );
		modelList.put( MARK_ROOT_PAGE_ID, nRootParentTree );
		modelList.put( MARK_CURRENT_PAGE_ID, Integer.toString( nCurrentPageId ) );

		// Define the site path from url, by mode
		modelList.put( MenusService.MARKER_SITE_PATH, _menusService.getSitePath( nMode ) );

		HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MENU_PAGES, locale, modelList );

		return templateList.getHtml( );
	}

	/**
	 * Display the list of childpages pages for other levels
	 * 
	 * @param nCurrentPageId
	 *                       The current page id
	 * @param nMode
	 *                       The current mode
	 * @param request
	 *                       The HTTP request
	 * @return the list of chilpages
	 */
	private String getTreePageList( int nCurrentPageId, int nMode, HttpServletRequest request )
	{
		HashMap < String, Object > modelList = new HashMap < String, Object >( );
		Locale locale = null;
		if( request != null )
		{
			locale = request.getLocale( );
		}

		// Define the root tree for each childpages of root page
		int nRootParentTree = _mainTreeMenuService.getRootParentTree( nCurrentPageId );

		MenuItem root = _mainTreeMenuService.getTreeMenuItems( nCurrentPageId, nRootParentTree );

		modelList.put( MARK_MENU, root );
		modelList.put( MARK_ROOT_PAGE_ID, nRootParentTree );
		modelList.put( MARK_CURRENT_PAGE_ID, Integer.toString( nCurrentPageId ) );

		// Define the site path from url, by mode
		modelList.put( MenusService.MARKER_SITE_PATH, _menusService.getSitePath( nMode ) );

		HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MENU_PAGES_TREE, locale, modelList );

		return templateList.getHtml( );
	}
}
