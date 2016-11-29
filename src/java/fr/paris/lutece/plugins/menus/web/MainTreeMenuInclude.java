/*
 * Copyright (c) 2002-2015, Mairie de Paris
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

import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * MainTreeMenuInclude
 */
public class MainTreeMenuInclude extends AbstractMainTreeMenuInclude implements PageInclude
{
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Constants

    private static final String MARK_PAGE_MENU_MAIN = "page_tree_menu_main";
    private static final String MARK_PAGE_MENU_TREE = "page_tree_menu_tree";

    // Properties
    private static final String PROPERTY_CACHE_ENABLED = "menus.mainTreeMenu.cache.enabled";
    private static final String PROPERTY_DEPTH_MAIN_LEVEL = "menus.mainTreeMenu.depth.main";
    private static final String PROPERTY_DEPTH_TREE_LEVEL = "menus.mainTreeMenu.depth.tree";

    private static final String CACHE_NAME = "Plugin Menus - Main Tree Menu Cache";

    /**
     * Constructor
     */
    public MainTreeMenuInclude( )
    {
        super( );
    }

    @Override
    protected String getPropertyCacheEnabled( )
    {
        return PROPERTY_CACHE_ENABLED;
    }

    /**
     * Returns the cache name
     * @return The cache name
     */
    @Override
    public String getName(  )
    {
        return CACHE_NAME;
    }

    @Override
    protected String getMarkPageMenuMain( )
    {
        return MARK_PAGE_MENU_MAIN;
    }

    @Override
    protected String getMarkPageMenuTree( )
    {
        return MARK_PAGE_MENU_TREE;
    }

    @Override
    protected int getMainPageListDepth( )
    {
        return AppPropertiesService.getPropertyInt( PROPERTY_DEPTH_MAIN_LEVEL, 1);
    }

    @Override
    protected int getPageId( int nRootParentTree, int nCurrentPageId )
    {
        if ( nRootParentTree != PortalService.getRootPageId(  ) )
        {
            return nRootParentTree;
        }

        // If the root tree is the site root tree (1), search the list of childpages from the current page, the number of levels defined by nDepth
        else
        {
            return nCurrentPageId;
        }
    }

    @Override
    protected int getTreePageListDepth( int pageId )
    {
        int nDepth = AppPropertiesService.getPropertyInt( PROPERTY_DEPTH_TREE_LEVEL, 0);

        // if the current page isn't root (1), we need two levels of childpages, other 0
        if ( pageId != PortalService.getRootPageId(  ) )
        {
            // if the current page isn't 0, the we need two levels of childpages, other 0
            if ( pageId != 0 )
            {
                nDepth = 2;
            }
        }

        return nDepth;
    }
}
