/*
 * Copyright (c) 2002-2009, Mairie de Paris
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
import fr.paris.lutece.plugins.menus.service.MenusService;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * MainTreeMenuInclude
 */
public class MainTreeMenuIncludeAllPages extends AbstractCacheableService implements PageInclude
{
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Constants

    //Templates
    private static final String TEMPLATE_MENU_PAGES = "skin/plugins/menus/main_tree_pages_list.html";
    private static final String TEMPLATE_MENU_PAGES_TREE = "skin/plugins/menus/main_tree_pages_list_tree.html";

    // Markers
    private static final String MARK_MENU = "menu";
    private static final String MARK_CURRENT_PAGE_ID = "current_page_id";
    private static final String MARK_ROOT_PAGE_ID = "root_page_id";
    private static final String MARK_PAGE_MENU_MAIN_ALL_PAGES = "page_tree_menu_main_all_pages";
    private static final String MARK_PAGE_MENU_TREE_ALL_PAGES = "page_tree_menu_tree_all_pages";
    private static final String CACHE_NAME = "Plugin Menus - Main Tree Menu All Pages Cache";
    
    // Properties
    private static final String PROPERTY_CACHE_ENABLED_ALLPAGES = "menus.mainTreeMenu.cache.enabled.allpages";
    private static final String PROPERTY_DEPTH_MAIN_LEVEL_ALLPAGES = "menus.mainTreeMenu.depth.main.allpages";
    private static final String PROPERTY_DEPTH_TREE_LEVEL_ALLPAGES = "menus.mainTreeMenu.depth.tree.allpages";

    private static final String DEFAULT_CACHE_ENABLED = "true";
    private static final String KEY_MAIN = "main";
    private static final String KEY_TREE = "menu";
    private static boolean _bInit;

    /**
     * Substitue specific Freemarker markers in the page template.
     * @param rootModel the HashMap containing markers to substitute
     * @param data A PageData object containing applications data
     * @param nMode The current mode
     * @param request The HTTP request
     */
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        if ( !_bInit )
        {
            init(  );
        }

        if ( request != null )
        {
            int nCurrentPageId;

            try
            {
                nCurrentPageId = ( request.getParameter( Parameters.PAGE_ID ) == null ) ? 0
                                                                                        : Integer.parseInt( request.getParameter( 
                            Parameters.PAGE_ID ) );
            }
            catch ( NumberFormatException nfe )
            {
                AppLogService.info( "MainMenuInclude.fillTemplate() : " + nfe.getLocalizedMessage(  ) );
                nCurrentPageId = 0;
            }

            rootModel.put( MARK_PAGE_MENU_MAIN_ALL_PAGES, getMainPageList( nCurrentPageId, nMode, request ) );
            rootModel.put( MARK_PAGE_MENU_TREE_ALL_PAGES, getTreePageList( nCurrentPageId, nMode, request ) );
        }
    }

    /**
     * Returns the cache name
     * @return The cache name
     */
    public String getName(  )
    {
        return CACHE_NAME;
    }

    /**
     * Initialization for cache management
     */
    private void init(  )
    {
        String strCacheEnabled = AppPropertiesService.getProperty( PROPERTY_CACHE_ENABLED_ALLPAGES, DEFAULT_CACHE_ENABLED );

        if ( strCacheEnabled.equalsIgnoreCase( DEFAULT_CACHE_ENABLED ) )
        {
            initCache( CACHE_NAME );
        }

        PortalService.registerCacheableService( CACHE_NAME, this );
        _bInit = true;
    }

    /**
     * Display the list of childpages pages for first level of childpages
     * @param nCurrentPageId The current page id
     * @param request The HTTP request
     * @return the list of childpages
     */
    private String getMainPageList( int nCurrentPageId, int nMode, HttpServletRequest request )
    {
        HashMap<String, Object> modelList = new HashMap<String, Object>(  );
        Locale locale = ( request == null ) ? null : request.getLocale(  );

        // Define the root tree for each childpages of root page
        int nRootParentTree = getRootParentTree( nCurrentPageId );

        MenuItem root = null;

        // Define the level of tree
        int nDepth = AppPropertiesService.getPropertyInt( PROPERTY_DEPTH_MAIN_LEVEL_ALLPAGES, 0);

        // Search the list of childpages from the root page, the number of levels defined by nDepth
        // and store it in root object
        if ( isCacheEnable(  ) )
        {
            root = (MenuItem) getFromCache( KEY_MAIN );
        }

        if ( root == null )
        {
            root = new MenuItem(  );
            buildMenuTree( root, PortalService.getRootPageId(  ), nDepth );

            if ( isCacheEnable(  ) )
            {
                putInCache( KEY_MAIN, root );
            }
        }

        modelList.put( MARK_MENU, root );
        modelList.put( MARK_ROOT_PAGE_ID, nRootParentTree );
        modelList.put( MARK_CURRENT_PAGE_ID, Integer.toString( nCurrentPageId ) );

        // Define the site path from url, by mode
        modelList.put( MenusService.MARKER_SITE_PATH, MenusService.getInstance(  ).getSitePath( nMode ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MENU_PAGES, locale, modelList );

        return templateList.getHtml(  );
    }

    /**
     * Display the list of childpages pages for other levels (nDepth define how many level to add on the list)
     * @param nCurrentPageId The current page id
     * @param request The HTTP request
     * @return the list of chilpages
     */
    private String getTreePageList( int nCurrentPageId, int nMode, HttpServletRequest request )
    {
        HashMap<String, Object> modelList = new HashMap<String, Object>(  );
        Locale locale = ( request == null ) ? null : request.getLocale(  );

        // Define the root tree for each childpages of root page
        int nRootParentTree = getRootParentTree( nCurrentPageId );

        MenuItem root = null;
        String strCacheKey = KEY_TREE + nCurrentPageId;

        if ( isCacheEnable(  ) )
        {
            root = (MenuItem) getFromCache( strCacheKey );
        }

        if ( root == null )
        {
            root = new MenuItem(  );

            // Define the level of tree
            int nDepth = AppPropertiesService.getPropertyInt( PROPERTY_DEPTH_TREE_LEVEL_ALLPAGES, 3);
            
            // If the root tree isn't site root tree (1), search the list of childpages from this nRootTree, the number of levels defined by nDepth
            //if ( nRootParentTree != PortalService.getRootPageId(  ) )
           // {
                buildMenuTree( root, PortalService.getRootPageId(  ), nDepth );
            //}

            // If the root tree is the site root tree (1), search the list of childpages from the current page, the number of levels defined by nDepth
           /* else
            {
                buildMenuTree( root, nCurrentPageId, nDepth );
            }*/

            if ( isCacheEnable(  ) )
            {
                putInCache( strCacheKey, root );
            }
        }

        modelList.put( MARK_MENU, root );
        modelList.put( MARK_ROOT_PAGE_ID, nRootParentTree );
        modelList.put( MARK_CURRENT_PAGE_ID, Integer.toString( nCurrentPageId ) );

        // Define the site path from url, by mode
        modelList.put( MenusService.MARKER_SITE_PATH, MenusService.getInstance(  ).getSitePath( nMode ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MENU_PAGES_TREE, locale, modelList );

        return templateList.getHtml(  );
    }

    /**
     * Define the root tree id of a page
     * @param nPageId The page identifier
     * @return The parent page identifier or root tree
     */
    private int getRootParentTree( int nPageId )
    {
        Page page = PageHome.getPage( nPageId );
        int nParentPageId = page.getParentPageId(  );

        if ( nParentPageId == 0 )
        {
            return nPageId;
        }

        int nParentTree = nParentPageId;

        while ( nParentPageId != 1 )
        {
            nParentTree = nParentPageId;

            Page parentPage = PageHome.getPage( nParentPageId );
            nParentPageId = parentPage.getParentPageId(  );
        }

        return nParentTree;
    }

    /**
     * Build the menu tree from nPageId, the number of levels defined by nDepth
     * @param item The MenunItem object
     * @param nPageId The page identifier
     * @param nDepth The page level
     */
    private void buildMenuTree( MenuItem item, int nPageId, int nDepth )
    {
        if ( nDepth > 0 )
        {
            Collection<Page> listPages = PageHome.getChildPages( nPageId );

            for ( Page page : listPages )
            {
                MenuItem mi = new MenuItem(  );
                mi.setPage( PageHome.findByPrimaryKey( page.getId(  ) ) );
                item.addChild( mi );
                buildMenuTree( mi, page.getId(  ), nDepth - 1 );
            }
        }
    }
}