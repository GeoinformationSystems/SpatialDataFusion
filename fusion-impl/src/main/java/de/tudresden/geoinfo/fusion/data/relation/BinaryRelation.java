package de.tudresden.geoinfo.fusion.data.relation;

import com.google.common.collect.Sets;
import de.tudresden.geoinfo.fusion.data.IIdentifier;
import de.tudresden.geoinfo.fusion.data.IMetadata;
import de.tudresden.geoinfo.fusion.data.rdf.IRDFResource;
import de.tudresden.geoinfo.fusion.data.rdf.vocabularies.Predicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * feature relation implementation
 */
public class BinaryRelation extends Relation implements IBinaryRelation {

    private IRDFResource domain, range;
    private Set<IRelationMeasurement> measurements;

    /**
     * constructor
     *
     * @param identifier identifier
     * @param domain       relation domain
     * @param range        relation range
     * @param type         relation types
     * @param measurements relation measurements
     */
    public BinaryRelation(@NotNull IIdentifier identifier, @NotNull IRDFResource domain, @NotNull IRDFResource range, @NotNull IBinaryRelationType type, @Nullable Set<IRelationMeasurement> measurements, @Nullable IMetadata metadata) {
        super(identifier, type, getMembers(domain, range, type), metadata);
        this.domain = domain;
        this.range = range;
        if(measurements != null)
            this.addMeasurements(measurements);
    }

    /**
     * get binary relation member objects
     *
     * @param domain domain object
     * @param range  range object
     * @param type   relation type
     */
    @NotNull
    private static Map<IRole,Set<IRDFResource>> getMembers(@NotNull IRDFResource domain, @NotNull IRDFResource range, @NotNull IBinaryRelationType type) {
        Map<IRole,Set<IRDFResource>> members = new HashMap<>();
        members.put(type.getRoleOfDomain(), Sets.newHashSet(Collections.singletonList(domain)));
        members.put(type.getRoleOfRange(), Sets.newHashSet(Collections.singletonList(range)));
        return members;
    }

    @NotNull
    @Override
    public IRDFResource getDomain() {
        return this.domain;
    }

    @NotNull
    @Override
    public IRDFResource getRange() {
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

    private void addMeasurements(@NotNull Set<IRelationMeasurement> measurements) {
        for(IRelationMeasurement measurement : measurements){
            this.addMeasurement(measurement);
        }
    }

    @Override
    public void addMeasurement(@NotNull IRelationMeasurement measurement) {
        if(this.measurements == null)
            this.measurements = new HashSet<>();
        this.measurements.add(measurement);
        this.addObject(Predicates.MEASUREMENT.getResource(), measurement);
    }

}
