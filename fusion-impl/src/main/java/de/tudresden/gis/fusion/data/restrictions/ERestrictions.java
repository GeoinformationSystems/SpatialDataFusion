package de.tudresden.gis.fusion.data.restrictions;

import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.feature.EGeometryType;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.operation.io.IDataRestriction;

public enum ERestrictions {
	
	//geometry restrictions
	GEOMETRY_POINT(new GeometryTypeRestriction(EGeometryType.GML3_0D_POINT)),
	GEOMETRY_LINE(new GeometryTypeRestriction(new EGeometryType[]{EGeometryType.GML3_1D_LINESTRING,EGeometryType.GML3_1D_CURVE})),
	GEOMETRY_POLYGON(new GeometryTypeRestriction(new EGeometryType[]{EGeometryType.GML3_2D_POLYGON,EGeometryType.GML3_2D_SURFACE})),
	GEOMETRY_NoPOINT(new GeometryTypeRestriction(new EGeometryType[]{EGeometryType.GML3_1D_LINESTRING,EGeometryType.GML3_1D_CURVE,EGeometryType.GML3_2D_POLYGON,EGeometryType.GML3_2D_SURFACE})),
	
	//Java binding restrictions
	BINDING_INTEGER(new JavaBindingRestriction(IntegerLiteral.class)),
	BINDING_LONG(new JavaBindingRestriction(LongLiteral.class)),
	BINDING_DECIMAL(new JavaBindingRestriction(DecimalLiteral.class)),
	BINDING_BOOLEAN(new JavaBindingRestriction(BooleanLiteral.class)),
	BINDING_STRING(new JavaBindingRestriction(StringLiteral.class)),
	BINDING_IFEATUReCOLLECTION(new JavaBindingRestriction(IFeatureCollection.class)),
	BINDING_IFEATUReRELATIOnCOLLECTION(new JavaBindingRestriction(IFeatureRelationCollection.class)),
	
	//mandatory restriction
	MANDATORY(new MandatoryIORestriction(true));
	
	private IDataRestriction restriction;
	private ERestrictions(IDataRestriction restriction){
		this.restriction = restriction;
	}
	public IDataRestriction getRestriction() {
		return restriction;
	}

}
