package de.tudresden.gis.fusion.data.restrictions;

import de.tudresden.gis.fusion.data.IComplexData;
import de.tudresden.gis.fusion.data.ICoverage;
import de.tudresden.gis.fusion.data.IFeatureCollection;
import de.tudresden.gis.fusion.data.IFeatureRelationCollection;
import de.tudresden.gis.fusion.data.complex.BPMNModel;
import de.tudresden.gis.fusion.data.complex.OSMFeatureCollection;
import de.tudresden.gis.fusion.data.feature.EGeometryType;
import de.tudresden.gis.fusion.data.simple.BooleanLiteral;
import de.tudresden.gis.fusion.data.simple.DecimalLiteral;
import de.tudresden.gis.fusion.data.simple.IntegerLiteral;
import de.tudresden.gis.fusion.data.simple.LongLiteral;
import de.tudresden.gis.fusion.data.simple.StringLiteral;
import de.tudresden.gis.fusion.data.simple.URILiteral;
import de.tudresden.gis.fusion.operation.io.IIORestriction;

public enum ERestrictions {
	
	//geometry restrictions
	GEOMETRY_POINT(new GeometryTypeRestriction(EGeometryType.GML3_0D_POINT)),
	GEOMETRY_LINE(new GeometryTypeRestriction(new EGeometryType[]{EGeometryType.GML3_1D_LINESTRING,EGeometryType.GML3_1D_CURVE})),
	GEOMETRY_POLYGON(new GeometryTypeRestriction(new EGeometryType[]{EGeometryType.GML3_2D_POLYGON,EGeometryType.GML3_2D_SURFACE})),
	GEOMETRY_NoPOINT(new GeometryTypeRestriction(new EGeometryType[]{EGeometryType.GML3_1D_LINESTRING,EGeometryType.GML3_1D_CURVE,EGeometryType.GML3_2D_POLYGON,EGeometryType.GML3_2D_SURFACE})),
	GEOMETRY_SURFACE(new GeometryTypeRestriction(EGeometryType.GML3_SURFACE)),
	
	//Java binding restrictions
	BINDING_INTEGER(new JavaBindingRestriction(IntegerLiteral.class)),
	BINDING_LONG(new JavaBindingRestriction(LongLiteral.class)),
	BINDING_DECIMAL(new JavaBindingRestriction(DecimalLiteral.class)),
	BINDING_BOOLEAN(new JavaBindingRestriction(BooleanLiteral.class)),
	BINDING_STRING(new JavaBindingRestriction(StringLiteral.class)),
	BINDING_URIRESOURCE(new JavaBindingRestriction(URILiteral.class)),
	BINDING_IFEATUReCOLLECTION(new JavaBindingRestriction(IFeatureCollection.class)),
	BINDING_OSMFEATUReCOLLECTION(new JavaBindingRestriction(OSMFeatureCollection.class)),
	BINDING_IFEATUReRELATIOnCOLLECTION(new JavaBindingRestriction(IFeatureRelationCollection.class)),
	BINDING_ICOVERAGE(new JavaBindingRestriction(ICoverage.class)),
	BINDING_BPMN(new JavaBindingRestriction(BPMNModel.class)),
	BINDING_ICOMPLEX(new JavaBindingRestriction(IComplexData.class)),
	
	//mandatory restriction
	MANDATORY(new MandatoryIORestriction(true));
	
	private IIORestriction restriction;
	private ERestrictions(IIORestriction restriction){
		this.restriction = restriction;
	}
	public IIORestriction getRestriction() {
		return restriction;
	}

}
