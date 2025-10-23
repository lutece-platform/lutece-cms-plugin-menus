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
package fr.paris.lutece.plugins.menus.service;

import java.util.Collection;

import fr.paris.lutece.plugins.menus.business.MenuItem;
import fr.paris.lutece.plugins.menus.service.cache.MainTreeMenuCacheService;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * MainTreeMenuAllPagesService
 */
public class MainTreeMenuService
{
    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Constants

    // Properties
    private static final String PROPERTY_DEPTH_MAIN_LEVEL = "menus.mainTreeMenu.depth.main";
    private static final String PROPERTY_DEPTH_TREE_LEVEL = "menus.mainTreeMenu.depth.tree";

    private static MainTreeMenuCacheService _cacheService = MainTreeMenuCacheService.getInstance( );
    private static MainTreeMenuService _instance = new MainTreeMenuService( );

    /**
     * Get the instance of the service
     * 
     * @return The instance of the service
     */
    public static MainTreeMenuService getInstance( )
    {
        return _instance;
    }

    /**
     * Return the root MenuItem
     * 
     * @return the root MenuItem
     */
    public MenuItem getMainMenuItems( )
    {
        // Define the level of tree - Use DatastoreService with fallback on AppPropertiesService
        int nDepth = Integer.parseInt( DatastoreService.getDataValue( PROPERTY_DEPTH_MAIN_LEVEL, 
                    AppPropertiesService.getProperty( PROPERTY_DEPTH_MAIN_LEVEL, "1" ) ) );
        
        String strCacheKey = _cacheService.getMainMenuCacheKey( );
        MenuItem root = (MenuItem) _cacheService.getFromCache( strCacheKey );

        if ( root == null )
        {
            root = new MenuItem( );

            // Use the new method to build menu at specific level
            buildMenuTree( root, PortalService.getRootPageId( ), nDepth );
            _cacheService.putInCache( strCacheKey, root );
        }

        return root;
    }

    /**
     * Return the TreeMenuItems from root MenuItem
     * 
     * @param nCurrentPageId
     *            The current page id
     * @param nRootParentTree
     *            The parent page of the current page
     * @return the TreeMenuItems from root MenuItem
     */
    public MenuItem getTreeMenuItems( int nCurrentPageId, int nParentCurrentPageId )
    {
        String strCacheKey = _cacheService.getMenuTreeCacheKey( nCurrentPageId );
        MenuItem root = (MenuItem) _cacheService.getFromCache( strCacheKey );

        if ( root == null )
        {
            root = new MenuItem( );

            // Define the level of tree - utilise DatastoreService avec fallback sur AppPropertiesService
            int nDepth = Integer.parseInt( DatastoreService.getDataValue( PROPERTY_DEPTH_TREE_LEVEL, 
                        AppPropertiesService.getProperty( PROPERTY_DEPTH_TREE_LEVEL, "0" ) ) );

            if ( nDepth > 2 )
            {
                nDepth = 2;
            }
            
            
            int nRootId = PortalService.getRootPageId( );
            
            //If page_id=0, then use root site page as root of the generated tree menu. The page id=0 doesn't exist physically as a page. But, in frontend,
            // you can access to Homepage with the url <site_path>?page_id=0. 
            if( nCurrentPageId==0 || nCurrentPageId==nRootId ) 
            {
            	//Add only child pages of the root. THe page root doesn't appear in tree menu
            	buildMenuTree( root, nRootId, nDepth );
            }
            else
            {
            	//Add currentPage in menu
        		MenuItem menuItem = new MenuItem( );
                menuItem.setPage( PageHome.findByPrimaryKey( nCurrentPageId ) );
                root.addChild( menuItem );
            	
                //Add its child pages
                MenuItem childRoot = root.getChilds( ).get( 0 );
            	buildMenuTree( childRoot, nCurrentPageId, nDepth );
            }
            
            _cacheService.putInCache( strCacheKey, root );
        }

        return root;
    }

    /**
     * Define the root tree id of a page
     * 
     * @param nPageId
     *            The page identifier
     * @return The parent page identifier or root tree
     */
    public int getRootParentTree( int nPageId )
    {
        Page page = PageHome.getPage( nPageId );
        int nParentPageId = page.getParentPageId( );

        if ( nParentPageId == 0 )
        {
            return nPageId;
        }

        int nParentTree = nParentPageId;

        int nPageRootId = PortalService.getRootPageId( );

        while ( nParentPageId != nPageRootId )
        {
            nParentTree = nParentPageId;

            Page parentPage = PageHome.getPage( nParentPageId );
            nParentPageId = parentPage.getParentPageId( );
        }

        return nParentTree;
    }

    /**
     * Build the menu tree from nPageId, the number of levels defined by nDepth
     * 
     * @param item
     *            The MenunItem object
     * @param nPageId
     *            The page identifier
     * @param nDepth
     *            The page level
     */
    private void buildMenuTree( MenuItem item, int nPageId, int nDepth )
    {
        if ( nDepth > 0 )
        {
            Collection<Page> listPages = PageHome.getChildPages( nPageId );

            for ( Page page : listPages )
            {
                MenuItem menuItem = new MenuItem( );
                menuItem.setPage( PageHome.findByPrimaryKey( page.getId( ) ) );
                item.addChild( menuItem );
                buildMenuTree( menuItem, page.getId( ), nDepth - 1 );
            }
        }
    }

    /**
     * Get the cacheService
     * 
     * @return the MainTreeMenuCacheService
     */
    public MainTreeMenuCacheService getCacheService( )
    {
        return _cacheService;
    }

}
