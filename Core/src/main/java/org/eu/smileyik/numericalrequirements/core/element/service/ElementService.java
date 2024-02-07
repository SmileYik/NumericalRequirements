package org.eu.smileyik.numericalrequirements.core.element.service;

import org.eu.smileyik.numericalrequirements.core.element.Element;

public interface ElementService {
    void registerElement(Element element);

    void unregisterElement(Element element);

    Element findElementById(String id);

    void shutdown();
}
