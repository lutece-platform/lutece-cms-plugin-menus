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

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.menus.business.MenuItem;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

public class MainTreeMenuServiceTest extends LuteceTestCase
{
    private static final int UNKNOWN_PAGE_ID = 999999;

    @Inject
    private MainTreeMenuService _service;

    /**
     * An unknown current page gives the tree of the root, never an item without page
     */
    @Test
    public void testUnknownCurrentPage( )
    {
        MenuItem unknown = new MenuItem( );
        _service.generateTreeMenu( unknown, UNKNOWN_PAGE_ID, PortalService.getRootPageId( ) );
        MenuItem home = new MenuItem( );
        _service.generateTreeMenu( home, 0, PortalService.getRootPageId( ) );

        assertEquals( home.getChilds( ).size( ), unknown.getChilds( ).size( ) );
        for ( MenuItem item : unknown.getChilds( ) )
        {
            assertTrue( item.getPage( ).getId( ) != 0 );
            assertNotNull( item.getPage( ).getName( ) );
        }
    }
}
