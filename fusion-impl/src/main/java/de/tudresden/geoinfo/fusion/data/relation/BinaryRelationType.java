package de.tudresden.geoinfo.fusion.data.relation;

import de.tudresden.geoinfo.fusion.data.rdf.IIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @param identifier   type identifier
     * @param roleOfDomain reference role
     * @param roleOfRange  target role
     * @param symmetric    flag: relation is symmetric
     * @param transitive   flag: relation is transitive
     * @param reflexive    flag: relation is reflexive
     */
    public BinaryRelationType(@Nullable IIdentifier identifier, @Nullable String title, @Nullable String description, @NotNull IRole roleOfDomain, @NotNull IRole roleOfRange, boolean symmetric, boolean transitive, boolean reflexive) {
        super(identifier, title, description, Arrays.asList(roleOfDomain, roleOfRange));
        this.roleOfDomain = roleOfDomain;
        this.roleOfRange = roleOfRange;
        this.symmetric = symmetric;
        this.transitive = transitive;
        this.reflexive = reflexive;
    }

    /**
     * constructor
     *
     * @param identifier   type identifier
     * @param roleOfDomain reference role
     * @param roleOfRange  target role
     * @param symmetric    flag: relation is symmetric
     * @param transitive   flag: relation is transitive
     * @param reflexive    flag: relation is reflexive
     */
    public BinaryRelationType(@NotNull IIdentifier identifier, @NotNull IRole roleOfDomain, @NotNull IRole roleOfRange, boolean symmetric, boolean transitive, boolean reflexive) {
        this(identifier, null, null, roleOfDomain, roleOfRange, symmetric, transitive, reflexive);
    }

    /**
     * constructor
     *
     * @param title        type title
     * @param roleOfDomain reference role
     * @param roleOfRange  target role
     * @param symmetric    flag: relation is symmetric
     * @param transitive   flag: relation is transitive
     * @param reflexive    flag: relation is reflexive
     */
    public BinaryRelationType(@NotNull String title, @NotNull IRole roleOfDomain, @NotNull IRole roleOfRange, boolean symmetric, boolean transitive, boolean reflexive) {
        this(null, title, null, roleOfDomain, roleOfRange, symmetric, transitive, reflexive);
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
