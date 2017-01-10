package de.tudresden.geoinfo.fusion.data.relation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.Subject;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Objects;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import de.tudresden.geoinfo.fusion.metadata.IMetadataForData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * feature relation implementation
 */
public class Relation<T extends IResource> extends Subject implements IRelation<T> {

	private static IResource PREDICATE_TYPE = Predicates.TYPE.getResource();
	private static IResource TYPE_RELATION = Objects.RELATION.getResource();
	private static IResource RELATION_TYPE = Predicates.RELATION_TYPE.getResource();

	/**
	 * constructor
	 * @param identifier resource identifier
	 * @param members relation members with role
	 * @param type relation types
	 * @param metadata relation metadata
	 */
	public Relation(IIdentifier identifier, Map<IRole,Set<T>> members, IRelationType type, IMetadataForData metadata){
		super(identifier, members, metadata);
		put(PREDICATE_TYPE, TYPE_RELATION);
		put(RELATION_TYPE, type);
        validate();
	}

    /**
     * validate the relation (must have at least one resource for each role)
     */
    private void validate() {
	    for(IRole role : getRelationType().getRoles()){
	        if(!resolve().containsKey(role))
	            throw new IllegalArgumentException("relation does not contain a resource for role " + role.getIdentifier());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<IRole,Set<T>> resolve(){
	    return (Map<IRole,Set<T>>) super.resolve();
    }

    @Override
    public Set<T> getMembers() {
        Set<T> members = new HashSet<>();
        for(Set<T> member : resolve().values()){
            members.addAll(member);
        }
        return members;
    }

	@Override
	public Set<T> getMember(IRole role) {
		return resolve().get(role);
	}

    /**
     * set relation member objects
     * @param domain domain object
     * @param range range object
     * @param type relation type
     */
	protected void setMembers(T domain, T range, IBinaryRelationType type){
        Map<IRole,Set<IResource>> members = new HashMap<>();
        members.put(type.getRoleOfDomain(), Sets.newHashSet(domain));
        members.put(type.getRoleOfRange(), Sets.newHashSet(range));
        setSubject(members);
    }

	@Override
	public IRelationType getRelationType() {
		return (IRelationType) getObject(RELATION_TYPE);
	}

    /**
     * set relation type (overrides existing type)
     * @param type relation type
     */
    public void setType(IRelationType type){
        put(RELATION_TYPE, type);
    }
	
}
