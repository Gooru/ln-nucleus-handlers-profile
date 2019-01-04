package org.gooru.nucleus.handlers.profiles.bootstrap.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.gooru.nucleus.handlers.profiles.app.components.AppConfiguration;
import org.gooru.nucleus.handlers.profiles.app.components.DataSourceRegistry;
import org.gooru.nucleus.handlers.profiles.app.components.UtilityManager;

public class Initializers implements Iterable<Initializer> {

  private final Iterator<Initializer> internalIterator;

  public Initializers() {
    List<Initializer> initializers = new ArrayList<>();
    initializers.add(DataSourceRegistry.getInstance());
    initializers.add(AppConfiguration.getInstance());
    initializers.add(UtilityManager.getInstance());
    internalIterator = initializers.iterator();
  }

  @Override
  public Iterator<Initializer> iterator() {
    return new Iterator<Initializer>() {

      @Override
      public boolean hasNext() {
        return internalIterator.hasNext();
      }

      @Override
      public Initializer next() {
        return internalIterator.next();
      }

    };
  }

}
