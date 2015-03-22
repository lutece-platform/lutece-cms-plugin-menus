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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.style.PageTemplateHome;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.includes.PageIncludeService;
import fr.paris.lutece.portal.service.page.IPageService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.MokeHttpServletRequest;

public abstract class AbstractMainTreeMenuIncludeTest extends LuteceTestCase
{

    @Test
    public void testFillTemplate( )
    {
        int depth = 4;
        int width = 2;
        String randPageNamePart = createPageTree( depth, width );
        try
        {
            PageInclude mainTreeMenuInclude = findMainTreeMenuInclude( getMainTreeMenuIncludeClass( ) );
            // create a stack of pages to test the menu on
            Stack<Page> parentPages = new Stack<Page>( );
            // begin with the root
            Page parentPage = PageHome.findByPrimaryKey( PortalService.getRootPageId( ) );
            parentPages.push( parentPage );
            MokeHttpServletRequest request = new MokeHttpServletRequest( );
            while ( !parentPages.isEmpty( ) )
            {
                parentPage = parentPages.pop( );
                request.addMokeParameters( Parameters.PAGE_ID, Integer.toString( parentPage.getId( ) ) );
                Map<String, Object> rootModel = new HashMap<String, Object>( );
                PageData data = new PageData( );
                mainTreeMenuInclude.fillTemplate( rootModel, data, 0, request );
                checkPageTreeMenuMain( rootModel.get( getPageTreeMenuMainMark( ) ), randPageNamePart, depth, width );
                checkPageTreeMenuTree( rootModel.get( getPageTreeMenuTreeMark( ) ), parentPage, randPageNamePart,
                        depth, width );
                // add children to test
                Collection<Page> children = PageHome.getChildPagesMinimalData( parentPage.getId( ) );
                for ( Page aPage : children )
                {
                    if ( aPage.getName( ).contains( randPageNamePart ) )
                    {
                        // only take into account pages created for the test
                        parentPages.push( aPage );
                    }
                }
            }
        } finally
        {
            // cleanup pages created for the test
            deletePageTree( randPageNamePart );
        }
    }

    protected abstract String getPageTreeMenuTreeMark( );

    protected abstract String getPageTreeMenuMainMark( );

    protected abstract Class<? extends PageInclude> getMainTreeMenuIncludeClass( );

    protected abstract void checkPageTreeMenuTree( Object object, Page parentPage, String randPageNamePart, int depth,
            int width );

    protected abstract void checkPageTreeMenuMain( Object object, String randPageNamePart, int depth, int width );

    private PageInclude findMainTreeMenuInclude( Class<? extends PageInclude> clazz )
    {
        PageInclude mainTreeMenuInclude = null;
        List<PageInclude> includes = PageIncludeService.getIncludes( );
        for ( PageInclude anInclude : includes )
        {
            if ( clazz.isInstance( anInclude ) )
            {
                mainTreeMenuInclude = anInclude;
                break;
            }
        }
        assertNotNull( "Did not find MainTreeMenuInclude", mainTreeMenuInclude );
        return mainTreeMenuInclude;
    }

    private void deletePageTree( String randPageNamePart )
    {
        Collection<Page> children = PageHome.getChildPagesMinimalData( PortalService.getRootPageId( ) );
        for ( Page aPage : children )
        {
            if ( aPage.getName( ).contains( randPageNamePart ) )
            {
                deletePageTree( aPage );
            }
        }
    }

    private void deletePageTree( Page page )
    {
        Collection<Page> children = PageHome.getChildPagesMinimalData( page.getId( ) );
        for ( Page aPage : children )
        {
            deletePageTree( aPage );
        }
        IPageService pageService = ( IPageService ) SpringContextService.getBean( "pageService" );
        pageService.removePage( page.getId( ) );
    }

    private String createPageTree( int depth, int width )
    {
        Page root = PageHome.findByPrimaryKey( PortalService.getRootPageId( ) );
        String randomPageName = "page" + new SecureRandom( ).nextLong( );
        List<String> ancestry = Collections.emptyList( );
        createPageTree( root, ancestry, randomPageName, depth, width );
        return randomPageName;
    }

    private void createPageTree( Page root, List<String> ancestry, String randomPageName, int depth, int width )
    {
        if ( depth == 0 )
        {
            return;
        }
        IPageService pageService = ( IPageService ) SpringContextService.getBean( "pageService" );
        for ( int i = 0; i < width; i++ )
        {
            Page page = new Page( );
            page.setParentPageId( root.getId( ) );
            List<String> path = new ArrayList<String>( ancestry );
            path.add( depth + "_" + i );
            String name = randomPageName + "_" + StringUtils.join( path, '|' );
            page.setName( name );
            page.setDescription( name + "_desc" );
            page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( ).get( 0 ).getId( ) );
            pageService.createPage( page );
            createPageTree( page, path, randomPageName, depth - 1, width );
        }
    }

    /**
    @Test
    public void testGetMenuContent( )
    {
        
        PageInclude mainTreeMenuInclude = findMainTreeMenuInclude( getMainTreeMenuIncludeClass( ) );
        
        // activate the cache
        assertTrue( mainTreeMenuInclude instanceof AbstractCacheableService );
        boolean cacheStatus = ( ( AbstractCacheableService ) mainTreeMenuInclude ).isCacheEnable( );
        ( ( AbstractCacheableService ) mainTreeMenuInclude ).enableCache( true );
        
        try {
            Map<String, Object> model = new HashMap<String, Object>( );
            PageData data = new PageData( );
            // determine a random page name
            String randomPageName = "page" + new SecureRandom( ).nextLong( );
            MokeHttpServletRequest request = new MokeHttpServletRequest( );
            // get the menu
            mainTreeMenuInclude.fillTemplate( model, data, 0, request );
            String mark = getTestGetMenuContentMark( );
            String menu = ( String ) model.get( mark );
            assertFalse( mark + " should not contain not yet created page with name " + randomPageName, menu.contains( randomPageName ) );
            // create the page
            Page page = new Page(  );
            page.setParentPageId( PortalService.getRootPageId(  ) );
            page.setName( randomPageName );
            page.setDescription( randomPageName + "_desc");
            page.setPageTemplateId( PageTemplateHome.getPageTemplatesList( ).get( 0 ).getId( ) );
            IPageService pageService = (IPageService) SpringContextService.getBean( "pageService" );
            pageService.createPage( page );
            // get the menu
            mainTreeMenuInclude.fillTemplate( model, data, 0, request );
            menu = ( String ) model.get( mark );
            assertTrue( mark + " should contain page with name " + randomPageName, menu.contains( randomPageName ) );
            // change the page name
            randomPageName = randomPageName + "_mod";
            page.setName( randomPageName );
            pageService.updatePage( page );
            // get the menu
            mainTreeMenuInclude.fillTemplate( model, data, 0, request );
            menu = ( String ) model.get( mark );
            assertTrue( mark + " should contain page with the modified name " + randomPageName, menu.contains( randomPageName ) );
            // remove the page
            pageService.removePage( page.getId( ) );
            // get the menu
            mainTreeMenuInclude.fillTemplate( model, data, 0, request );
            menu = ( String ) model.get( mark );
            assertFalse( mark + " should not contain page with name " + randomPageName + " anymore", menu.contains( randomPageName ) );
        } finally
        {
            // restore the cache status
            ( ( AbstractCacheableService ) mainTreeMenuInclude ).enableCache( cacheStatus );
        }
    }
    */

    protected abstract String getTestGetMenuContentMark( );

}
