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
package fr.paris.lutece.plugins.menus.business;

import java.sql.Timestamp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.style.PageTemplateHome;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * The dynamic label of a « page » item is the name of the page
 */
public class CustomMenuItemLabelTest extends LuteceTestCase
{
    private static final String PAGE_NAME = "Saint-Germain-des-Prés";

    private Page _page;

    @AfterEach
    protected void tearDown( ) throws Exception
    {
        if ( _page != null )
        {
            PageHome.remove( _page.getId( ) );
        }
        super.tearDown( );
    }

    /**
     * A hyphen in the page name does not cut the label, the description is not part of it
     */
    @Test
    public void testDynamicLabelIsTheWholePageName( )
    {
        _page = new Page( );
        _page.setParentPageId( PortalService.getRootPageId( ) );
        _page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( ).get( 0 ).getId( ) );
        _page.setName( PAGE_NAME );
        _page.setDescription( "Quartier - rive gauche" );
        _page.setMetaKeywords( "" );
        _page.setMetaDescription( "" );
        _page.setNodeStatus( 1 );
        _page.setDateUpdate( new Timestamp( System.currentTimeMillis( ) ) );
        PageHome.create( _page );

        CustomMenuItem item = new CustomMenuItem( );
        item.setType( "page" );
        item.setSourceItemId( String.valueOf( _page.getId( ) ) );
        item.setIsLabelDynamic( true );
        item.setLabel( "" );

        assertEquals( PAGE_NAME, item.getLabel( ) );
    }

    /**
     * The label of an item whose page no longer exists is empty
     */
    @Test
    public void testDynamicLabelOfAMissingPageIsEmpty( )
    {
        CustomMenuItem item = new CustomMenuItem( );
        item.setType( "page" );
        item.setSourceItemId( "987654" );
        item.setIsLabelDynamic( true );

        assertEquals( "", item.getLabel( ) );
    }
}
