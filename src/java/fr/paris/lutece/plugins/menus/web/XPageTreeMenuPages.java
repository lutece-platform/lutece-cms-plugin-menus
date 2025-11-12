package fr.paris.lutece.plugins.menus.web;

import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;

import fr.paris.lutece.portal.util.mvc.commons.annotations.ResponseBody;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.paris.lutece.plugins.menus.business.MenuItem;
import fr.paris.lutece.plugins.menus.business.PageInfo;
import fr.paris.lutece.plugins.menus.service.MainTreeMenuAllPagesService;
import fr.paris.lutece.plugins.menus.service.MenusService;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;

/**
 * Xpage dedicated to tree menu all pages json Endpoint
 **/
@RequestScoped
@Named( "menus.xpage.treemenupages" )
@Controller( xpageName = "treemenupages", pageTitleI18nKey = "menus.xpage.treemenupages.pageTitle", pagePathI18nKey = "menus.xpage.treemenupages.pagePath" )
public class XPageTreeMenuPages extends MVCApplication
{
	// Path constants
	protected static final String PLUGIN_PATH = "menus/";

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

	//Action
	private static final String ACTION_MENU_TREE = "menutree";
	
	// Status constants
	private static final String STATUS_OK = "OK";
	private static final String STATUS_KO = "KO";

	private String _strPageFullLink;

	@Inject
	private MenusService _menusService;

	@Inject
	private MainTreeMenuAllPagesService _mainTreeMenuAllPagesService;

	
	/**
	 * Generate result of json endpoint
	 *
	 * @param request The Http request
	 * @return tree menu all pages in json format 
	 */
	@ResponseBody
	@Action( value = ACTION_MENU_TREE )
	public ObjectNode getMenuTree( HttpServletRequest request )
	{
		String strStatus = STATUS_OK;
		String strTreeOfMenuPages = StringUtils.EMPTY;

		setPageFullLink(
				AppPathService.getBaseUrl( request ) + _menusService.getSitePath( 0 ) + "?page_id=" );

		try
		{
			MenuItem rootMenuItem = _mainTreeMenuAllPagesService.getFullTreeMenuItems( );
			if( rootMenuItem != null )
			{
				strTreeOfMenuPages = formatTreeMenuItems( rootMenuItem );
			}
		}
		catch( Exception exception )
		{
			strStatus = STATUS_KO;
			AppLogService.error( exception.getMessage( ), exception );
		}

		return createResponseObject( strStatus, strTreeOfMenuPages );
	}

	
	/**
	 * Generate response object for json endpoint
	 *
	 * @param strStatus resonse status ("ok" or "ko")
	 * @param strResponse Menu to return in json
	 * 
	 * @return tree menu all pages in json format 
	 */
	private ObjectNode createResponseObject( String strStatus, String strResponse )
	{
		ObjectMapper mapper = new ObjectMapper( );
		ObjectNode jsonResponse = mapper.createObjectNode( );

		try
		{
			jsonResponse.put( KEY_MENUS_STATUS_RESPONSE, strStatus );

			if( StringUtils.isNotEmpty( strResponse ) )
			{
				JsonNode resultNode = mapper.readTree( strResponse );
				jsonResponse.set( KEY_MENUS_RESPONSE_RESULT, resultNode );
			}
		}
		catch( Exception e )
		{
			AppLogService.error( e.getMessage( ), e );
		}

		return jsonResponse;
	}

	/**
	 * Return the Json tree of menu pages
	 * 
	 * @param rootMenuItem
	 *                     the root MenuItem
	 * @return the Json tree of menu pages
	 */
	private String formatTreeMenuItems( MenuItem rootMenuItem )
	{
		ObjectMapper mapper = new ObjectMapper( );
		ObjectNode jsonResponse = mapper.createObjectNode( );

		ArrayNode jsonAllMenusItems = formatListMenuItems( rootMenuItem );

		try
		{
			jsonResponse.set( KEY_ROOT_MENU_PAGES, jsonAllMenusItems );
		}
		catch( Exception e )
		{
			AppLogService.error( e.getMessage( ), e );
		}

		return jsonResponse.toString( );
	}

	/**
	 * Return the Json list of menu items
	 * 
	 * @param currentMenuItem
	 *                        the current MenuItem
	 * @return the Json list of the current MenuItem
	 */
	private ArrayNode formatListMenuItems( MenuItem currentMenuItem )
	{
		ObjectMapper mapper = new ObjectMapper( );
		ArrayNode jsonMenusList = mapper.createArrayNode( );

		for( MenuItem childMenuItem : currentMenuItem.getChilds( ) )
		{
			ObjectNode jsonMenus = mapper.createObjectNode( );

			add( jsonMenus, childMenuItem.getPage( ) );

			ArrayNode jsonChildMenusList = formatListMenuItems( childMenuItem );

			try
			{
				jsonMenus.set( KEY_MENU_PAGES, jsonChildMenusList );

				jsonMenusList.add( jsonMenus );
			}
			catch( Exception e )
			{
				AppLogService.error( e.getMessage( ), e );
			}
		}

		return jsonMenusList;
	}

	/**
	 * Add the data from a Menus object to a JsonObject
	 * 
	 * @param jsonMenus
	 *                  the Json to include the data
	 * @param pageInfo
	 *                  the information of the page
	 */
	private void add( ObjectNode jsonMenus, PageInfo pageInfo )
	{
		if( jsonMenus != null && pageInfo != null )
		{
			try
			{
				jsonMenus.put( KEY_PAGE_ID, pageInfo.getId( ) );
				jsonMenus.put( KEY_PAGE_PARENT_ID, pageInfo.getParentPageId( ) );
				jsonMenus.put( KEY_PAGE_NAME, pageInfo.getName( ) );
				jsonMenus.put( KEY_PAGE_DESC, pageInfo.getDescription( ) );
				jsonMenus.put( KEY_PAGE_FULL_LINK, getPageFullLink( ) + pageInfo.getId( ) );
			}
			catch( Exception e )
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
	 *                        The page full link
	 */
	public void setPageFullLink( String strPageFullLink )
	{
		_strPageFullLink = strPageFullLink;
	}
}