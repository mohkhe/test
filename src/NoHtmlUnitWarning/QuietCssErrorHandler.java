package NoHtmlUnitWarning;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import com.gargoylesoftware.htmlunit.DefaultCssErrorHandler;

/*
 * get rid of warnings... and provide a place to hang a break point
 */
public class QuietCssErrorHandler  extends DefaultCssErrorHandler
{

    @Override public void error( CSSParseException e ) throws CSSException 
    {
        //super.error( e ) ;
    }

    @Override public void fatalError( CSSParseException e ) throws CSSException 
    { 
      //  super.fatalError( e ) ; 
    }

    @Override public void warning( CSSParseException e ) throws CSSException 
    {
    }
}
