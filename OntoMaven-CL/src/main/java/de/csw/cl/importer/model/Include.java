package de.csw.cl.importer.model;

import java.util.Stack;

import org.jdom2.Element;

public class Include {
    public Element e;
    public Stack<String> restrictHistory;

    Include(Element e, Stack<String> restrictHistory) {
        this.e = e;
        this.restrictHistory = restrictHistory;
    }

}
