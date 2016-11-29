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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.portal.PortalService;

public class MainTreeMenuIncludeTest extends AbstractMainTreeMenuIncludeTest
{

    @Override
    protected String getPageTreeMenuTreeMark( )
    {
        return "page_tree_menu_tree";
    }

    @Override
    protected String getPageTreeMenuMainMark( )
    {
        return "page_tree_menu_main";
    }

    @Override
    protected Class<? extends PageInclude> getMainTreeMenuIncludeClass( )
    {
        return MainTreeMenuInclude.class;
    }

    protected void checkPageTreeMenuTree( Object object, Page parentPage, String randPageNamePart, int depth, int width )
    {
        assertTrue( getPageTreeMenuTreeMark( ) + " should be a String", object instanceof String );
        String menu = ( String ) object;
        if ( parentPage.getId( ) == PortalService.getRootPageId( ) )
        {
            assertTrue( StringUtils.isBlank( menu ) );
        } else
        {
            int indexOfPipe = parentPage.getName( ).indexOf( '|' );
            String prefix;
            if ( indexOfPipe == -1 )
            {
                prefix = parentPage.getName( );
            } else
            {
                prefix = parentPage.getName( ).substring( 0, indexOfPipe );
            }
            checkPageTreeMenuTreePages( menu, prefix, width, depth, 2 /*
                                                                       * 2 is
                                                                       * the
                                                                       * depth
                                                                       * for
                                                                       * menus
                                                                       */);
        }
    }

    private void checkPageTreeMenuTreePages( String menu, String prefix, int width, int depth, int menuDepth )
    {
        if ( menuDepth == 0 )
        {
            return;
        }
        for ( int i = 0; i < width; i++ )
        {
            String name = prefix + "|" + ( menuDepth + depth - 3 ) + "_" + i;
            assertTrue( "menu should contain page named " + name, menu.contains( name + " " ) );
            checkPageTreeMenuTreePages( menu, name, width, depth, menuDepth - 1 );
        }
    }

    protected void checkPageTreeMenuMain( Object object, String randPageNamePart, int depth, int width )
    {
        assertTrue( getPageTreeMenuMainMark( ) + " should be a String", object instanceof String );
        String menu = ( String ) object;
        // int menuDepth = AppPropertiesService.getPropertyInt(
        // "menus.mainTreeMenu.depth.main", 1);
        int menuDepth = 1; // the default template is hardcoded to a depth of 1
        for ( int i = 0; i < width; i++ )
        {
            String name = randPageNamePart + "_" + depth + "_" + i;
            assertTrue( "menu should contain page named " + name, menu.contains( name + " " ) );
            checkPageTreeMenuTreePages( menu, name, width, depth, menuDepth - 1 );
        }
    }

    @Override
    protected String getTestGetMenuContentMark( )
    {
        return getPageTreeMenuMainMark( );
    }

}
