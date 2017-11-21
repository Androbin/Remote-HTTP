package de.androbin.remote.http.message;

import de.androbin.util.*;
import java.util.*;
import java.util.Map.*;

public final class Headers implements Iterable<Entry<NoCaseString, String>> {
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
  
  public String get( final String key ) {
    return data.get( new NoCaseString( key ) );
  }
  
  @ Override
  public Iterator<Entry<NoCaseString, String>> iterator() {
    return data.entrySet().iterator();
  }
  
  public void put( final String key, final String value ) {
    data.put( new NoCaseString( key ), value );
  }
}