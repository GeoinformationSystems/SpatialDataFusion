package de.tudresden.geoinfo.fusion.data;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;

/**
 * RDF resource implementation
 * @author Stefan Wiemann, TU Dresden
 *
 */
public class Resource implements IResource {
	
	/**
	 * resource uri
	 */
	private IIdentifier identifier;

    /**
     * constructor
     * @param identifier resource identifier uri
     */
    public Resource(IIdentifier identifier){
        this.identifier = identifier;
    }

    /**
     * constructor
     * @param empty flag: create empty node, otherwise a random identifier is created
     */
    public Resource(boolean empty){
        this(empty ? null : new Identifier());
    }

    /**
     * empty constructor, creates empty node
     */
    public Resource(){
        this(true);
    }

	@Override
	public IIdentifier getIdentifier() {
		return identifier;
	}

	@Override
    public boolean isBlank(){
        return identifier == null;
    }

	@Override
    public boolean equals(Object resource){
	    //not equal if object is null or no Resource
        if(resource == null || resource instanceof Resource)
            return false;
        //equal, if resource URI equals this.identifier
        return((Resource) resource).getIdentifier().equals(identifier);
    }
	
}
