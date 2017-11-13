package de.androbin.remote.http.message;

import de.androbin.util.*;
import java.util.*;
import java.util.function.*;

public final class Headers {
  private final Map<NoCaseString, String> data;
  
  public Headers() {
    data = new HashMap<>();
  }
  
  public Headers( final Headers headers ) {
    data = Collections.unmodifiableMap( new HashMap<>( headers.data ) );
  }
  
  public boolean containsKey( final String key ) {
    return data.containsKey( new NoCaseString( key ) );
  }
  
  public void forEach( final BiConsumer<String, String> consumer ) {
    data.forEach( ( key, value ) -> consumer.accept( key.value, value ) );
  }
  
  public String get( final String key ) {
    return data.get( new NoCaseString( key ) );
  }
  
  public void put( final String key, final String value ) {
    data.put( new NoCaseString( key ), value );
  }
}