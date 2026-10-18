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

import java.util.HashSet;
import java.util.Set;
import java.util.function.IntFunction;

import fr.paris.lutece.portal.business.page.Page;

/**
 * Walks up the parent chain of a page
 */
public final class PageTreeUtils
{
	/**
	 * Private constructor
	 */
	private PageTreeUtils( )
	{
	}

	/**
	 * Find the ancestor of a page that heads its tree under the root page. The walk stops when the parent chain is broken
	 * (missing page, parent 0) or loops, so that a page whose chain does not reach the root never blocks the request.
	 * 
	 * @param nPageId
	 *                   The page identifier
	 * @param nRootPageId
	 *                   The root page identifier
	 * @param pageLoader
	 *                   Loads a page from its identifier (an empty page or null when the page does not exist)
	 * @return The parent page identifier or root tree, or the last ancestor found when the chain does not reach the root
	 */
	public static int getRootParentTree( int nPageId, int nRootPageId, IntFunction<Page> pageLoader )
	{
		Page page = pageLoader.apply( nPageId );
		int nParentPageId = ( page != null ) ? page.getParentPageId( ) : 0;

		if( nParentPageId == 0 )
		{
			return nPageId;
		}

		int nParentTree = nParentPageId;
		Set<Integer> setVisitedIds = new HashSet<>( );
		setVisitedIds.add( nPageId );

		while( nParentPageId != nRootPageId )
		{
			if( nParentPageId == 0 || !setVisitedIds.add( nParentPageId ) )
			{
				// The chain does not reach the root or loops
				break;
			}

			Page parentPage = pageLoader.apply( nParentPageId );

			if( parentPage == null || parentPage.getId( ) != nParentPageId )
			{
				// Missing parent page
				break;
			}

			nParentTree = nParentPageId;
			nParentPageId = parentPage.getParentPageId( );
		}

		return nParentTree;
	}
}
