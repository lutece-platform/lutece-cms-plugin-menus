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

import fr.paris.lutece.plugins.menus.business.Menus;
import fr.paris.lutece.plugins.menus.business.MenusHome;
import fr.paris.lutece.plugins.menus.service.MenusPlugin;
import fr.paris.lutece.plugins.menus.service.MenusService;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.xpages.XPageApplicationEntry;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This class provides the list of the xpage on a Html Menu
 */
public class XPageMenuInclude implements PageInclude
{
	// ///////////////////////////////////////////////////////////////////////////////////////////
	// Constants
	private static final String TEMPLATE_MENU_XPAGES = "skin/plugins/menus/xpages_list.html";
	private static final String MARK_XPAGES_LIST = "xpages_list";
	private static final String MENU_MARKER = "xpage_menu";

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
	public void fillTemplate( Map < String, Object > rootModel, PageData data, int nMode, HttpServletRequest request )
	{
		if( request != null )
		{
			Plugin plugin = PluginService.getPlugin( MenusPlugin.PLUGIN_NAME );

			for( Menus menus : MenusHome.findAll( plugin ) )
			{
				if( menus.getMenuType( ).equals( MENU_MARKER ) )
				{
					String strMarkerMenuXPage = menus.getMenuMarker( );
					rootModel.put( strMarkerMenuXPage, getXPageList( nMode, request ) );
				}
			}
		}
	}

	/**
	 * Display the list of plugins app installed on the instance of lutece
	 *
	 * @param nMode
	 *                The current mode
	 * @param request
	 *                The HTTP request
	 * @return the list
	 */
	private String getXPageList( int nMode, HttpServletRequest request )
	{
		HashMap < String, Object > modelList = new HashMap < String, Object >( );
		Collection < Plugin > pluginList = new ArrayList < Plugin >( );
		Locale locale = null;
		if( request != null )
		{
			locale = request.getLocale( );
		}

		// Scan of the list
		for( XPageApplicationEntry entry : XPageAppService.getXPageApplicationsList( ) )
		{
			if( entry.isEnable( ) )
			{
				Plugin plugin = entry.getPlugin( );

				if( plugin != null )
				{
					pluginList.add( plugin );
				}
			}
		}

		// Define the site path from url, by mode
		modelList.put( MenusService.MARKER_SITE_PATH, _menusService.getSitePath( nMode ) );

		// Insert the rows in the list
		modelList.put( MARK_XPAGES_LIST, pluginList );

		HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MENU_XPAGES, locale, modelList );

		return templateList.getHtml( );
	}
}
