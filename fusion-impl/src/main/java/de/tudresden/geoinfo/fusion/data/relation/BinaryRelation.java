package de.tudresden.geoinfo.fusion.data.relation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import de.tudresden.geoinfo.fusion.data.rdf.IResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * feature relation implementation
 */
public class BinaryRelation<T extends IResource> extends Relation<T> implements IBinaryRelation<T> {

    private T domain, range;
    private Set<IRelationMeasurement> measurements;

    /**
     * constructor
     *
     * @param identifier   resource identifier
     * @param domain       relation domain
     * @param range        relation range
     * @param type         relation types
     * @param measurements relation measurements
     */
    public BinaryRelation(@Nullable IIdentifier identifier, @NotNull T domain, @NotNull T range, @NotNull IBinaryRelationType type, @Nullable Set<IRelationMeasurement> measurements, @Nullable IMetadata metadata) {
        super(identifier, type, getMembers(domain, range, type), metadata);
        this.domain = domain;
        this.range = range;
        this.measurements = measurements;
    }

    /**
     * get binary relation member objects
     *
     * @param domain domain object
     * @param range  range object
     * @param type   relation type
     */
    @NotNull
    protected static <T extends IResource> Map<IRole, Set<T>> getMembers(@NotNull T domain, @NotNull T range, @NotNull IBinaryRelationType type) {
        Map<IRole, Set<T>> members = new HashMap<>();
        members.put(type.getRoleOfDomain(), Sets.newHashSet(Collections.singletonList(domain)));
        members.put(type.getRoleOfRange(), Sets.newHashSet(Collections.singletonList(range)));
        return members;
    }

    @NotNull
    @Override
    public T getDomain() {
        return this.domain;
    }

    @NotNull
    @Override
    public T getRange() {
        return this.range;
    }

    @NotNull
    @Override
    public IBinaryRelationType getRelationType() {
        return (IBinaryRelationType) super.getRelationType();
    }

    @NotNull
    @Override
    public Set<IRelationMeasurement> getMeasurements() {
        return this.measurements;
    }

    @Override
    public void addMeasurement(@NotNull IRelationMeasurement measurement) {
        this.measurements.add(measurement);
    }

}
