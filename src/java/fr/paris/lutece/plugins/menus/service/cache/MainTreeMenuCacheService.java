/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.menus.service.cache;

import fr.paris.lutece.portal.service.cache.AbstractCacheableService;

/**
 * Get the instance of the cache service
 */
public final class MainTreeMenuCacheService extends AbstractCacheableService
{
    // Properties
    private static final String CACHE_NAME = "Plugin Menus - Main Tree Menu Cache";
    private static final String CACHE_KEY_MAIN = "menus.main";
    private static final String CACHE_KEY_TREE = "menus.tree";
    
    private static MainTreeMenuCacheService _instance = new MainTreeMenuCacheService( );

    /**
     * Private constructor
     */
    private MainTreeMenuCacheService( )
    {
        super( );
        initCache( );
    }

    /**
     * Get the instance of the cache service
     * 
     * @return The instance of the service
     */
    public static MainTreeMenuCacheService getInstance( )
    {
        return _instance;
    }

    /**
     * Get the cache key for a given main menu
     * 
     * @return The cache key for the main
     */
    public String getMainMenuCacheKey( )
    {
        return CACHE_KEY_MAIN;
    }

    /**
     * Get the cache key for a given menu tree
     * 
     * @param nCurrentPageId
     *            The id of the menu tree 
     * @return The cache key for the menu tree
     */
    public String getMenuTreeCacheKey( int nCurrentPageId )
    {
        return CACHE_KEY_TREE + nCurrentPageId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return CACHE_NAME;
    }
}
