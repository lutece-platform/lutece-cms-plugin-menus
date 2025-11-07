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

import fr.paris.lutece.plugins.menus.business.CustomMenu;
import fr.paris.lutece.plugins.menus.business.CustomMenuHome;
import fr.paris.lutece.plugins.menus.business.MenuItem;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.xpages.XPageApplicationEntry;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

@ApplicationScoped
public class CustomMenuService
{
	public static final int MODE_SITE = 0;
	public static final int MODE_ADMIN = 1;
	public static final String MARKER_SITE_PATH = "site_path";

	@Inject
	private MainTreeMenuAllPagesService _mainTreeMenuAllPagesService;

	/**
	 * Define the site path : Portal Url when mode isn't admin mode, otherwise
	 * AdminPortalUrl
	 * 
	 * @param nMode
	 *              the mode define by the request
	 * @return site path depending on the mode
	 */
	public String getSitePath( int nMode )
	{
		String strSitePath = AppPathService.getAdminPortalUrl( );

		if( nMode != MODE_ADMIN )
		{
			strSitePath = AppPathService.getPortalUrl( );
		}

		return strSitePath;
	}

	/**
	 * Check if an item match with filter criteria
	 * 
	 * @param strItemToTest
	 *                      Item to test
	 * @return the reference list
	 */
	private boolean isSearchCriteriaValidated( String strItemToTest, String strFilterCriteria )
	{

		if( StringUtils.isBlank( strFilterCriteria ) )
		{
			return true;
		}

		String strCleanCriteria = strFilterCriteria.trim( );
		Boolean isValid = Strings.CS.equals( strCleanCriteria, strItemToTest );

		for( String criteria : strCleanCriteria.split( " " ) )
		{
			isValid |= Strings.CS.contains( strItemToTest, criteria );
		}

		return isValid;
	}

	// ////////////////////////////////////////////
	// ///////////GETTERS REFERENCE LISTS//////////
	// ////////////////////////////////////////////

	/**
	 * Get the available menus reference list
	 * 
	 * @return the reference list
	 */
	public ReferenceList getAvailableMenusReferenceList( CustomMenu currentCustomMenu, String strFilterCriteria )
	{
		ReferenceList referenceList = new ReferenceList( );
		Integer nId = currentCustomMenu != null ? currentCustomMenu.getId( ) : - 1;

		for( CustomMenu menu : CustomMenuHome.findAllWithCriteria( strFilterCriteria ) )
		{
			if( menu.getId( ) != nId )
			{
				referenceList.addItem( menu.getId( ), menu.getName( ) );
			}
		}

		return referenceList;
	}

	/**
	 * Get the available xpages reference list
	 * 
	 * @return the reference list
	 */
	public ReferenceList getAvailableXpagesReferenceList( String strFilterCriteria )
	{

		ReferenceList referenceList = new ReferenceList( );

		// Scan of the list
		for( XPageApplicationEntry entry : XPageAppService.getXPageApplicationsList( ) )
		{
			if( entry.isEnable( ) && isSearchCriteriaValidated( entry.getId( ), strFilterCriteria ) )
			{
				referenceList.addItem( entry.getId( ), entry.getId( ) );
			}
		}

		return referenceList;
	}

	/**
	 * Get the available menus reference list
	 * 
	 * @return the reference list
	 */
	public ReferenceList getAvailablePagesReferenceList( String strFilterCriteria )
	{
		MenuItem root = _mainTreeMenuAllPagesService.getFullTreeMenuItems( );

		ReferenceList referenceList = new ReferenceList( );

		if( root != null )
		{
			traverseItem( root, referenceList, strFilterCriteria );
		}

		return referenceList;
	}

	/**
	 * Recursive methode to get all pages
	 * 
	 * @param MenuItem      Each Item containing a page
	 * @param ReferenceList ReferenceList with all pages
	 */
	private void traverseItem( MenuItem item, ReferenceList referenceList, String strFilterCriteria )
	{
		if( item != null )
		{

			if( item.getPage( ) != null && ( isSearchCriteriaValidated( item.getPage( ).getName( ), strFilterCriteria )
					|| isSearchCriteriaValidated( item.getPage( ).getDescription( ), strFilterCriteria )
					|| isSearchCriteriaValidated( String.valueOf( item.getPage( ).getId( ) ), strFilterCriteria ) ) )
			{
				String name = item.getPage( ).getName( ) != null ? item.getPage( ).getName( ) : "";
				String description = item.getPage( ).getDescription( ) != null ? item.getPage( ).getDescription( ) : "";
				String value = name + " - " + description;

				referenceList.addItem( item.getPage( ).getId( ), value );
			}

			if( item.getChilds( ) != null )
			{
				for( MenuItem child : item.getChilds( ) )
				{
					traverseItem( child, referenceList, strFilterCriteria );
				}
			}
		}
	}

	public String getLabelPageById( String strSourceItemId )
	{

		ReferenceList listPages = getAvailablePagesReferenceList( "" );

		for( ReferenceItem page : listPages )
		{
			if( Strings.CS.equals( strSourceItemId, page.getCode( ) ) )
			{
				return page.getName( );
			}
		}

		return "";
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
