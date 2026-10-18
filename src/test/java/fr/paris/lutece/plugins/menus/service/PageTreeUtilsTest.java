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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import fr.paris.lutece.portal.business.page.Page;

/**
 * PageTreeUtils Test
 */
public class PageTreeUtilsTest
{
	private static final int ROOT_ID = 1;

	private final Map<Integer, Page> _mapPages = new HashMap<>( );

	/**
	 * Same contract as PageHome.getPage: an empty page (id 0, parent 0) when the page does not exist
	 */
	private final IntFunction<Page> _pageLoader = nId -> _mapPages.getOrDefault( nId, new Page( ) );

	private void addPage( int nId, int nParentId )
	{
		Page page = new Page( );
		page.setId( nId );
		page.setParentPageId( nParentId );
		_mapPages.put( nId, page );
	}

	/**
	 * A valid chain returns the ancestor directly under the root
	 */
	@Test
	public void testValidChain( )
	{
		addPage( ROOT_ID, 0 );
		addPage( 5, ROOT_ID );
		addPage( 10, 5 );
		addPage( 20, 10 );

		assertEquals( 5, PageTreeUtils.getRootParentTree( 20, ROOT_ID, _pageLoader ) );
		assertEquals( 5, PageTreeUtils.getRootParentTree( 10, ROOT_ID, _pageLoader ) );
		assertEquals( ROOT_ID, PageTreeUtils.getRootParentTree( 5, ROOT_ID, _pageLoader ) );
		assertEquals( ROOT_ID, PageTreeUtils.getRootParentTree( ROOT_ID, ROOT_ID, _pageLoader ) );
	}

	/**
	 * A chain whose top parent does not exist must end
	 */
	@Test
	@Timeout( 5 )
	public void testOrphanChain( )
	{
		addPage( 10, 99 );
		addPage( 20, 10 );

		assertEquals( 10, PageTreeUtils.getRootParentTree( 20, ROOT_ID, _pageLoader ) );
		assertEquals( 99, PageTreeUtils.getRootParentTree( 10, ROOT_ID, _pageLoader ) );
		assertEquals( 404, PageTreeUtils.getRootParentTree( 404, ROOT_ID, _pageLoader ) );
	}

	/**
	 * A chain that loops must end
	 */
	@Test
	@Timeout( 5 )
	public void testCycle( )
	{
		addPage( 10, 20 );
		addPage( 20, 30 );
		addPage( 30, 10 );
		addPage( 40, 40 );

		assertEquals( 30, PageTreeUtils.getRootParentTree( 10, ROOT_ID, _pageLoader ) );
		assertEquals( 40, PageTreeUtils.getRootParentTree( 40, ROOT_ID, _pageLoader ) );
	}

	/**
	 * A loader that returns null is handled
	 */
	@Test
	public void testNullPage( )
	{
		assertEquals( 7, PageTreeUtils.getRootParentTree( 7, ROOT_ID, nId -> null ) );
	}
}
