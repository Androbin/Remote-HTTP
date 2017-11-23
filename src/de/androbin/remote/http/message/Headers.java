package de.androbin.remote.http.message;

import de.androbin.util.*;
import java.util.*;
import java.util.Map.*;

public final class Headers implements Iterable<Pair<String, String>> {
  private final Map<String, List<String>> data;
  
  public Headers() {
    data = new HashMap<>();
  }
  
  public Headers( final Headers headers ) {
    data = Collections.unmodifiableMap( new HashMap<>( headers.data ) );
  }
  
  public void add( final String key, final String value ) {
    data.computeIfAbsent( key.toLowerCase(), foo -> new ArrayList<>() ).add( value );
  }
  
  public boolean contains( final String key ) {
    return data.containsKey( key.toLowerCase() );
  }
  
  public List<String> getAll( final String key ) {
    return data.get( key.toLowerCase() );
  }
  
  public String getOne( final String key ) {
    return getAll( key ).get( 0 );
  }
  
  @ Override
  public Iterator<Pair<String, String>> iterator() {
    final Iterator<Entry<String, List<String>>> iter = data.entrySet().iterator();
    
    return new Iterator<Pair<String, String>>() {
      private Entry<String, List<String>> last;
      private int index;
      
      @ Override
      public boolean hasNext() {
        return iter.hasNext() || last != null && index < last.getValue().size();
      }
      
      @ Override
      public Pair<String, String> next() {
        if ( last == null ) {
          last = iter.next();
        }
        
        final List<String> values = last.getValue();
        final Pair<String, String> pair = new Pair<>( last.getKey(), values.get( index ) );
        
        if ( ++index == values.size() ) {
          last = iter.next();
          index = 0;
        }
        
        return pair;
      }
    };
  }
}