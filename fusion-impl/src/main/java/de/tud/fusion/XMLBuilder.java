package de.tud.fusion;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * XML builder
 */
public class XMLBuilder {

    private String nodePrefix, nodeName, text;
    private Map<String,String> attributes;
    private Collection<XMLBuilder> childNodes;

    public XMLBuilder(@Nullable String nodePrefix, @NotNull String nodeName, @Nullable Map<String,String> attributes, @Nullable String text, @Nullable Collection<XMLBuilder> childNodes){
        this.nodePrefix = nodePrefix;
        this.nodeName = nodeName;
        this.attributes = attributes != null ? attributes : new HashMap<>();
        this.text = text;
        this.childNodes = childNodes != null ? childNodes : new ArrayList<>();
    }

    public void addAttribute(@NotNull String name, @NotNull String value){
        this.attributes.put(name, value);
    }

    public void addChildNode(@NotNull XMLBuilder childNode){
        this.childNodes.add(childNode);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        this.appendNode(builder);
        return builder.toString();
    }

    private void appendNode(StringBuilder builder){
        this.appendStart(builder);
        this.appendText(builder);
        this.appendChildNodes(builder);
        this.appendClosure(builder);
    }

    private void appendNodeIdentifier(StringBuilder builder){
        builder.append(this.nodePrefix != null ? nodePrefix + ":" : "").append(this.nodeName);
    }

    private void appendStart(StringBuilder builder){
        builder.append("<");
        appendNodeIdentifier(builder);
        appendAttributes(builder);
        builder.append(">");
    }

    private void appendAttributes(StringBuilder builder){
        for(Map.Entry<String,String> attribute : this.attributes.entrySet()){
            builder.append(" ").append(attribute.getKey()).append("=\"").append(attribute.getValue()).append("\"");
        }
    }

    private void appendText(StringBuilder builder){
        builder.append(this.text != null ? this.text : "");
    }

    private void appendChildNodes(StringBuilder builder){
        for(XMLBuilder childNode : this.childNodes){
            childNode.appendNode(builder);
        }
    }

    private void appendClosure(StringBuilder builder){
        builder.append("</");
        appendNodeIdentifier(builder);
        builder.append(">");
    }

}
