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
import fr.paris.lutece.plugins.menus.service.MenusService;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * MainTreeMenuInclude
 */
public class CustomMenuInclude implements PageInclude
{
    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Constants

    // Templates
    private static final String TEMPLATE_MENU_PAGES = "skin/plugins/menus/custom_menu_list.html";

    // Parameters
    private static final String PARAMETER_CURRENT_PAGE_ID = "current_page_id";
    private static final String PARAMETER_CURRENT_MENU = "customMenu";
    private static final String PARAMETER_CUSTOM_MAIN_MENU = "customMenuMainPage";
    private static final String PARAMETER_CUSTOM_INTERNAL_MENU = "customMenuInternalPage";
    private static final String PARAMETER_NAV_BAR_LUTECE = "navBarLutece";
    private static final String PARAMETER_COUNTER_MENU = "counterCustomMenu";
    
    private static final String TYPE_MENU = "menu";
    private static final String TYPE_MAIN_MENU = "main";
    private static final String TYPE_INTERNAL_MENU = "internal";
    
    private static final Integer CURRENT_DEPTH=1;
    private static final Integer MAX_DEPTH=2;
    

    /**
     * Substitue specific Freemarker markers in the page template.
     * 
     * @param rootModel
     *            the HashMap containing markers to substitute
     * @param data
     *            A PageData object containing applications data
     * @param nMode
     *            The current mode
     * @param request
     *            The HTTP request
     */
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        if ( request != null )
        {
            int nCurrentPageId;
            
            /* test parameter name: page_id parameter for a PageContentService, current_page_id for a DocumentContentService */
            String strParameterPageId = ( request.getParameter( PARAMETER_CURRENT_PAGE_ID ) == null ) ? Parameters.PAGE_ID : PARAMETER_CURRENT_PAGE_ID;

            try
            {
                nCurrentPageId = ( request.getParameter( strParameterPageId ) == null ) ? 0 : Integer.parseInt( request.getParameter( strParameterPageId ) );
            }
            catch( NumberFormatException nfe )
            {
                AppLogService.info( "MainMenuInclude.fillTemplate() : " + nfe.getLocalizedMessage( ) );
                nCurrentPageId = 0;
            }
            
            List<CustomMenu> listCostumMenu = CustomMenuHome.getCustomMenusList( );
            Boolean isInternalMenu = listCostumMenu.stream( ).anyMatch(menu -> StringUtils.equals( menu.getType( ), TYPE_INTERNAL_MENU ) );
            
            for( CustomMenu cm : listCostumMenu )
            {
            	if( !StringUtils.isBlank( cm.getBookmark( ) ) )
            	{	
            		loadMenuItems( cm, CURRENT_DEPTH, MAX_DEPTH );
            		rootModel.put( cm.getBookmark( ), getCustomMenuList( cm, nCurrentPageId, nMode, request, false ) );
            		
            		if( StringUtils.equals( cm.getType( ), TYPE_MAIN_MENU ) )
            		{
            			rootModel.put( PARAMETER_CUSTOM_MAIN_MENU, getCustomMenuList( cm, nCurrentPageId, nMode, request, true ) );
            			if( !isInternalMenu )
            			{
            				rootModel.put( PARAMETER_CUSTOM_INTERNAL_MENU, getCustomMenuList( cm, nCurrentPageId, nMode, request, true ) );
            			}
            		}
            		
            		if( StringUtils.equals( cm.getType( ), TYPE_INTERNAL_MENU ) )
            		{
            			rootModel.put( PARAMETER_CUSTOM_INTERNAL_MENU, getCustomMenuList( cm, nCurrentPageId, nMode, request, true ) );
            		}
            	}
            }
        }
    }

	/**
     * Display the list of childpages pages for first level of childpages
     * 
     * @param nCurrentPageId
     *            The current page id
     * @param nMode
     *            The current mode
     * @param request
     *            The HTTP request
     * @return the list of childpages
     */
    private String getCustomMenuList( CustomMenu cm, int nCurrentPageId, int nMode, HttpServletRequest request, Boolean isNavBarLutece )
    {
        HashMap<String, Object> modelList = new HashMap<String, Object>( );
        Locale locale = null;
        if ( request != null )
        {
            locale = request.getLocale( );
        }

        // Define the site path from url, by mode
        modelList.put( MenusService.MARKER_SITE_PATH, MenusService.getInstance( ).getSitePath( nMode ) );
        modelList.put( PARAMETER_CURRENT_MENU, cm );
        modelList.put( PARAMETER_NAV_BAR_LUTECE, isNavBarLutece );
        
        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MENU_PAGES, locale, modelList );

        return templateList.getHtml( );
    }

    /**
     * Loads menu items recursively, setting sub-menus for menu type items
     * 
     * @param menu The menu to load items for
     */
    private void loadMenuItems( CustomMenu menu, Integer currentDepth, Integer maxDepth )
    {
        // Stop recursion if maximum depth is exceeded
        if( currentDepth > maxDepth ) 
        {
            return;
        }
    	
        List<CustomMenuItem> listItems = CustomMenuItemHome.getCustomMenuItemsListByMenuId( menu.getId( ) );
        
        for( CustomMenuItem item : listItems )
        {
            if( StringUtils.equals( item.getType( ), TYPE_MENU ) && !StringUtils.isBlank( item.getSourceItemId( ) ) ) 
            {
                try 
                {
                    Integer nId = Integer.parseInt( item.getSourceItemId( ) );
                    CustomMenu subMenu = CustomMenuHome.findByPrimaryKey( nId );
            		
                    // Recursive call to load sub-menu items
                    if( subMenu != null )
                    {
            			// Load items for submenu
            			List < CustomMenuItem > items = CustomMenuItemHome.getCustomMenuItemsListByMenuId( nId );
            			subMenu.setListItems( items );
                        
            			loadMenuItems( subMenu, currentDepth+1, maxDepth );
                        item.setSubMenu( subMenu );
                    }
                }
                catch( Exception e )
                {
                    AppLogService.error( "Error loading sub-menu for item: " + item.getSourceItemId( ), e );
                }
            }
        }
        menu.setListItems( listItems );
    }
}
