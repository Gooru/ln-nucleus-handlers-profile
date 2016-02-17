package org.gooru.nucleus.profiles.processors;

import org.gooru.nucleus.profiles.processors.responses.MessageResponse;

public interface Processor {
  MessageResponse process();
}
