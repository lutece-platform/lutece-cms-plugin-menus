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
import fr.paris.lutece.plugins.menus.service.cache.MainTreeMenuAllPagesCacheService;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 * MainTreeMenuAllPagesService
 */
@ApplicationScoped
public class MainTreeMenuAllPagesService
{
	// ///////////////////////////////////////////////////////////////////////////////////////////
	// Constants

	// Properties
	public static final String PROPERTY_DEPTH_MAIN_LEVEL_ALLPAGES = "menus.mainTreeMenu.depth.main.allpages";
	public static final String PROPERTY_DEPTH_TREE_LEVEL_ALLPAGES = "menus.mainTreeMenu.depth.tree.allpages";

	@Inject
	private MainTreeMenuAllPagesCacheService _cacheService;

	/**
	 * Return the root MenuItem
	 * 
	 * @return the root MenuItem
	 */
	public MenuItem getMainMenuItems( )
	{
		// Define the level of tree
		int nDepth = AppPropertiesService.getPropertyInt( PROPERTY_DEPTH_MAIN_LEVEL_ALLPAGES, 0 );
		MenuItem root;

		if( _cacheService != null && _cacheService.isCacheEnable( ) )
		{
			String strCacheKey = _cacheService.getMainMenuCacheKey( );
			root = ( MenuItem ) _cacheService.get( strCacheKey );

			if( root == null )
			{
				root = new MenuItem( );

				buildMenuTree( root, PortalService.getRootPageId( ), nDepth );
				_cacheService.put( strCacheKey, root );
			}
		}
		else
		{
			root = new MenuItem( );
			buildMenuTree( root, PortalService.getRootPageId( ), nDepth );
		}

		return root;
	}

	/**
	 * Return the TreeMenuItems from root MenuItem
	 * 
	 * @param nCurrentPageId
	 *                       The current page id
	 * @return the TreeMenuItems from root MenuItem
	 */
	public MenuItem getTreeMenuItems( int nCurrentPageId )
	{

		MenuItem root;
		String strCacheKey = "";

		if( _cacheService != null && _cacheService.isCacheEnable( ) )
		{
			strCacheKey = _cacheService.getMenuTreeCacheKey( nCurrentPageId );
			root = ( MenuItem ) _cacheService.get( strCacheKey );

			if( root == null )
			{
				root = new MenuItem( );

				// Define the level of tree
				int nDepth = AppPropertiesService.getPropertyInt( PROPERTY_DEPTH_TREE_LEVEL_ALLPAGES, 3 );

				buildMenuTree( root, PortalService.getRootPageId( ), nDepth );
				_cacheService.put( strCacheKey, root );
			}
		}
		else
		{
			root = new MenuItem( );

			// Define the level of tree
			int nDepth = AppPropertiesService.getPropertyInt( PROPERTY_DEPTH_TREE_LEVEL_ALLPAGES, 3 );

			buildMenuTree( root, PortalService.getRootPageId( ), nDepth );
		}

		return root;
	}

	/**
	 * Define the root tree id of a page
	 * 
	 * @param nPageId
	 *                The page identifier
	 * @return The parent page identifier or root tree
	 */
	public int getRootParentTree( int nPageId )
	{
		Page page = PageHome.getPage( nPageId );
		int nParentPageId = page.getParentPageId( );

		if( nParentPageId == 0 )
		{
			return nPageId;
		}

		int nParentTree = nParentPageId;

		int nPageRootId = PortalService.getRootPageId( );

		while( nParentPageId != nPageRootId )
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
	 *                The MenunItem object
	 * @param nPageId
	 *                The page identifier
	 * @param nDepth
	 *                The page level
	 */
	private void buildMenuTree( MenuItem item, int nPageId, int nDepth )
	{
		if( nDepth > 0 )
		{
			Collection < Page > listPages = PageHome.getChildPages( nPageId );

			for( Page page : listPages )
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
	 * @return the MainTreeMenuAllPagesCacheService
	 */
	public MainTreeMenuAllPagesCacheService getCacheService( )
	{
		return _cacheService;
	}

	/**
	 * Return if cacheService is instancied and enable
	 * 
	 * @return true if cacheService is instancied and enable, false otherwise
	 */
	public Boolean isMainTreeCacheServiceEnable( )
	{
		return _cacheService != null && _cacheService.isCacheEnable( );
	}

	/**
	 * This method observes the initialization of the {@link ApplicationScoped}
	 * context.
	 * It ensures that this CDI beans are instantiated at the application startup.
	 *
	 * <p>
	 * This method is triggered automatically by CDI when the
	 * {@link ApplicationScoped} context is initialized,
	 * which typically occurs during the startup of the application server.
	 * </p>
	 *
	 * @param context the {@link ServletContext} that is initialized. This parameter
	 *                is observed
	 *                and injected automatically by CDI when the
	 *                {@link ApplicationScoped} context is initialized.
	 */
	public void initializedService( @Observes @Initialized( ApplicationScoped.class ) ServletContext context )
	{
		// This method is intentionally left empty to trigger CDI bean instantiation
	}

}
