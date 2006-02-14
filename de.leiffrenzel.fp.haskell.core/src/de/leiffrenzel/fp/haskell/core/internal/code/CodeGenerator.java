// Copyright (c) 2003-2005 by Leif Frenzel - see http://leiffrenzel.de
package de.leiffrenzel.fp.haskell.core.internal.code;

import de.leiffrenzel.fp.haskell.core.code.EHaskellCommentStyle;


/** <p>helping class that generates Haskell source code.</p>
  *
  * @author Leif Frenzel
  */
public class CodeGenerator {

  // TODO honor code template for generating this
  public String createModuleContent( final String[] folderNames,
                                     final String name,
                                     EHaskellCommentStyle style )
  {
    StringBuffer sb = new StringBuffer();
    sb.append( getLineDelimiter() );
    sb.append(getPrefixFor( style ));
    sb.append( "module " );
    for( int i = 0; i < folderNames.length; i++ ) {
      sb.append( folderNames[ i ] );
      sb.append( "." );
    }
    sb.append( name );
    sb.append( " where" );
    sb.append( getLineDelimiter() );
    sb.append( getSuffixFor(style) );
    return sb.toString();
  }


  private static String getSuffixFor( EHaskellCommentStyle style ) {
    if (EHaskellCommentStyle.TEX == style) {
      return "\\end{code}";
    }
    
    return "";
  }


  private static String getPrefixFor(EHaskellCommentStyle style) {
    if (EHaskellCommentStyle.LITERATE == style) {
      return "> ";
    } else if (EHaskellCommentStyle.TEX == style) {
      return "\\begin{code}\n";
    } 

    return "";
  }

  
  // helping methods
  //////////////////
  
  private static String getLineDelimiter() {
    return System.getProperty( "line.separator", "\n" );    
  }
}