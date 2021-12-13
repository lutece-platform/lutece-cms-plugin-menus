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
package fr.paris.lutece.plugins.menus.web.rs;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import fr.paris.lutece.plugins.menus.business.MenuItem;
import fr.paris.lutece.plugins.menus.business.PageInfo;
import fr.paris.lutece.plugins.menus.service.MainTreeMenuAllPagesService;
import fr.paris.lutece.plugins.menus.service.MenusService;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;

/**
 * REST service for MyFavorites resource
 */
@Path( RestConstants.BASE_PATH + TreeMenuPagesRest.PLUGIN_PATH + TreeMenuPagesRest.TREE_MENUS_PAGES_PATH )
public class TreeMenuPagesRest
{
    // Path constants
    protected static final String PLUGIN_PATH = "menus/";
    protected static final String TREE_MENUS_PAGES_PATH = "tree_menu_pages/";

    // Format constants
    private static final String KEY_MENUS_STATUS_RESPONSE = "status";
    private static final String KEY_MENUS_RESPONSE_RESULT = "result";
    private static final String KEY_ROOT_MENU_PAGES = "root_menu_pages";
    private static final String KEY_MENU_PAGES = "menu_pages";
    private static final String KEY_PAGE_PARENT_ID = "parentId";
    private static final String KEY_PAGE_ID = "id";
    private static final String KEY_PAGE_NAME = "name";
    private static final String KEY_PAGE_DESC = "description";
    private static final String KEY_PAGE_FULL_LINK = "pageFullLink";

    // Status constants
    private static final String STATUS_OK = "OK";
    private static final String STATUS_KO = "KO";

    private String _strPageFullLink;

    /**
     * Return the tree of menu pages
     * 
     * @param request
     *            httpServletRequest
     * @return the tree of menu pages
     */
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getTreeMenuPages( @Context HttpServletRequest request )
    {
        String strStatus = STATUS_OK;

        String strTreeOfMenuPages = StringUtils.EMPTY;

        setPageFullLink( AppPathService.getBaseUrl( request ) + MenusService.getInstance( ).getSitePath( 0 ) + "?page_id=" );

        try
        {
            MenuItem rootMenuItem = MainTreeMenuAllPagesService.getInstance( ).getTreeMenuItems( 0 );
            if ( rootMenuItem != null )
            {
                strTreeOfMenuPages = formatTreeMenuItems( rootMenuItem );
            }
        }
        catch( Exception exception )
        {
            // We set the status at KO if an error occurred during the processing
            strStatus = STATUS_KO;
        }

        // Format the response with the given status and the tree of menu pages
        String strResponse = formatResponse( strStatus, strTreeOfMenuPages );

        return Response.ok( strResponse, MediaType.APPLICATION_JSON ).build( );
    }

    /**
     * Return the Json response with the given status
     * 
     * @param strStatus
     *            The status of the treatment "OK" by default "KO" if an error occurred during the processing
     * @param strResponse
     *            The response to send
     * @return the Json response with the given status
     */
    private String formatResponse( String strStatus, String strResponse )
    {
        JSONObject jsonResponse = new JSONObject( );
        try
        {
            jsonResponse.accumulate( KEY_MENUS_STATUS_RESPONSE, strStatus );
        }
        catch ( JSONException e1 )
        {
            AppLogService.error( e1.getMessage( ), e1 );
        }
        try
        {
            jsonResponse.accumulate( KEY_MENUS_RESPONSE_RESULT, strResponse );
        }
        catch ( JSONException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return jsonResponse.toString( );
    }

    /**
     * Return the Json tree of menu pages
     * 
     * @param rootMenuItem
     *            the root MenuItem
     * @return the Json tree of menu pages
     */
    private String formatTreeMenuItems( MenuItem rootMenuItem )
    {
        JSONObject jsonResponse = new JSONObject( );
        JSONArray jsonAllMenusItems = formatListMenuItems( rootMenuItem );

        try
        {
            jsonResponse.accumulate( KEY_ROOT_MENU_PAGES, jsonAllMenusItems );
        }
        catch( JSONException e ) {
            AppLogService.error( e.getMessage( ), e );
        }

        return jsonResponse.toString( );
    }

    /**
     * Return the Json list of menu items
     * 
     * @param currentMenuItem
     *            the current MenuItem
     * @return the Json list of the current MenuItem
     */
    private JSONArray formatListMenuItems( MenuItem currentMenuItem )
    {
        JSONArray jsonMenusList = new JSONArray( );

        for ( MenuItem childMenuItem : currentMenuItem.getChilds( ) )
        {
            JSONObject jsonMenus = new JSONObject( );
            add( jsonMenus, childMenuItem.getPage( ) );
            JSONArray jsonChildMenusList = formatListMenuItems( childMenuItem );
            try
            {
                jsonMenus.accumulate( KEY_MENU_PAGES, jsonChildMenusList );
            }
            catch ( JSONException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
            ((List<MenuItem>) jsonMenusList).addAll( (Collection<? extends MenuItem>) jsonMenus );
        }

        return jsonMenusList;
    }

    /**
     * Add the data from a Menus object to a JsonObject
     * 
     * @param jsonMenus
     *            the Json to include the data
     * @param pageInfo
     *            the information of the page
     */
    private void add( JSONObject jsonMenus, PageInfo pageInfo )
    {
        if ( jsonMenus != null && pageInfo != null )
        {
            try
            {
                jsonMenus.accumulate( KEY_PAGE_ID, pageInfo.getId( ) );
                jsonMenus.accumulate( KEY_PAGE_PARENT_ID, pageInfo.getParentPageId( ) );
                jsonMenus.accumulate( KEY_PAGE_NAME, pageInfo.getName( ) );
                jsonMenus.accumulate( KEY_PAGE_DESC, pageInfo.getDescription( ) );
                jsonMenus.accumulate( KEY_PAGE_FULL_LINK, getPageFullLink( ) + pageInfo.getId( ) );
            }
            catch ( JSONException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
            
        }
    }

    /**
     * Returns the page full link
     *
     * @return The page full link
     */
    public String getPageFullLink( )
    {
        return _strPageFullLink;
    }

    /**
     * Sets the page full link
     *
     * @param strPageFullLink
     *            The page full link
     */
    public void setPageFullLink( String strPageFullLink )
    {
        _strPageFullLink = strPageFullLink;
    }

}
