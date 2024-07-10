package org.eu.smileyik.numericalrequirements.core.api.element;

public interface ElementService {
    void registerElement(Element element);

    void unregisterElement(Element element);

    Element findElementById(String id);

    void shutdown();
}
