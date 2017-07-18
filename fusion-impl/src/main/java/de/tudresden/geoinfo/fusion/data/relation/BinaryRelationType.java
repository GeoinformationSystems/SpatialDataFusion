package de.tudresden.geoinfo.fusion.data.relation;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Relation type implementation
 */
public class BinaryRelationType extends RelationType implements IBinaryRelationType {

    private IRole roleOfDomain, roleOfRange;
    private boolean symmetric, transitive, reflexive;

    /**
     * constructor
     *
     * @param identifier   type resource identifier
     * @param roleOfDomain reference role
     * @param roleOfRange  target role
     * @param symmetric    flag: relation is symmetric
     * @param transitive   flag: relation is transitive
     * @param reflexive    flag: relation is reflexive
     */
    public BinaryRelationType(@NotNull String identifier, @NotNull IRole roleOfDomain, @NotNull IRole roleOfRange, boolean symmetric, boolean transitive, boolean reflexive) {
        super(identifier, Arrays.asList(roleOfDomain, roleOfRange));
        this.roleOfDomain = roleOfDomain;
        this.roleOfRange = roleOfRange;
        this.symmetric = symmetric;
        this.transitive = transitive;
        this.reflexive = reflexive;
    }

    /**
     * constructor
     *
     * @param roleOfDomain reference role
     * @param roleOfRange  target role
     * @param symmetric    flag: relation is symmetric
     * @param transitive   flag: relation is transitive
     * @param reflexive    flag: relation is reflexive
     */
    public BinaryRelationType(@NotNull IRole roleOfDomain, @NotNull IRole roleOfRange, boolean symmetric, boolean transitive, boolean reflexive) {
        this(null, roleOfDomain, roleOfRange, symmetric, transitive, reflexive);
    }

    @NotNull
    @Override
    public IRole getRoleOfDomain() {
        return roleOfDomain;
    }

    @NotNull
    @Override
    public IRole getRoleOfRange() {
        return roleOfRange;
    }

    @Override
    public boolean isSymmetric() {
        return symmetric;
    }

    @Override
    public boolean isTransitive() {
        return transitive;
    }

    @Override
    public boolean isReflexive() {
        return reflexive;
    }

}
