package de.androbin.remote.http.route;

import de.androbin.remote.http.message.*;
import de.androbin.remote.http.message.Message.*;
import java.util.*;
import java.util.function.*;

public final class RouteIndex implements Route {
  public final Map<String, Route> mappings;
  public Route defaultMapping;
  
  public RouteIndex() {
    mappings = new HashMap<>();
  }
  
  @ Override
  public Supplier<Response> handle( final Request request, final String[] target ) {
    if ( mappings.containsKey( target[ 0 ] ) ) {
      final Route mapping = mappings.getOrDefault( target[ 0 ], defaultMapping );
      
      if ( mapping == null ) {
        return () -> Messages.RESPONSE_ERROR_404;
      } else {
        final String[] subTarget = Arrays.copyOfRange( target, 1, target.length );
        return mapping.handle( request, subTarget );
      }
    } else if ( defaultMapping == null ) {
      return () -> Messages.RESPONSE_ERROR_404;
    } else {
      return defaultMapping.handle( request, target );
    }
  }
}