//package de.tudresden.gis.fusion.operation.measurement;
//
//import java.util.Collection;
//import java.util.HashSet;
//
//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.IntersectionMatrix;
//
//import de.tudresden.gis.fusion.data.IDataCollection;
//import de.tudresden.gis.fusion.data.RangePattern;
//import de.tudresden.gis.fusion.data.description.MeasurementDescription;
//import de.tudresden.gis.fusion.data.feature.IFeature;
//import de.tudresden.gis.fusion.data.feature.geotools.GTVectorFeature;
//import de.tudresden.gis.fusion.data.feature.geotools.GTFeatureCollection;
//import de.tudresden.gis.fusion.data.literal.BooleanLiteral;
//import de.tudresden.gis.fusion.data.literal.StringLiteral;
//import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;
//import de.tudresden.gis.fusion.data.relation.FeatureRelationCollection;
//import de.tudresden.gis.fusion.data.relation.IFeatureRelation;
//import de.tudresden.gis.fusion.data.relation.IRelationMeasurement;
//import de.tudresden.gis.fusion.data.relation.RelationMeasurement;
//import de.tudresden.gis.fusion.operation.ARelationMeasurementOperation;
//import de.tudresden.gis.fusion.operation.ProcessException;
//import de.tudresden.gis.fusion.operation.constraint.ContraintFactory;
//import de.tudresden.gis.fusion.operation.constraint.IDataConstraint;
//import de.tudresden.gis.fusion.operation.constraint.IProcessConstraint;
//import de.tudresden.gis.fusion.operation.description.IInputDescription;
//import de.tudresden.gis.fusion.operation.description.IOutputDescription;
//import de.tudresden.gis.fusion.operation.description.InputDescription;
//import de.tudresden.gis.fusion.operation.description.OutputDescription;
//
//public class TopologyRelation extends ARelationMeasurementOperation {
//
//	private final String IN_SOURCE = "IN_SOURCE";
//	private final String IN_TARGET = "IN_TARGET";
//	private final String IN_RELATIONS = "IN_RELATIONS";
//	private final String IN_DROP_RELATIONS = "IN_DROP_RELATIONS";
//
//	private final String OUT_RELATIONS = "OUT_RELATIONS";
//
//	private boolean bDropRelations;
//
//	private Collection<IInputDescription> inputDescriptions = null;
//	private Collection<IOutputDescription> outputDescriptions = null;
//
//	private MeasurementDescription de9imDescription = new MeasurementDescription(
//			RDFVocabulary.TYPE_MEAS_TOP_DE9IM.getString(),
//			"DE-9IM code",
//			"DE9-IM code for intersection between feature geometries",
//			new RangePattern("^[012TF\\*]{9}$"),
//			RDFVocabulary.UOM_UNDEFINED.getResource());
//
//	@Override
//	public void execute() throws ProcessException {
//
//		//get input
//		GTFeatureCollection inSource = (GTFeatureCollection) getInput(IN_SOURCE);
//		GTFeatureCollection inTarget = (GTFeatureCollection) getInput(IN_TARGET);
//		bDropRelations = ((BooleanLiteral) getInput(IN_DROP_RELATIONS)).resolve();
//
//		//execute
//		IDataCollection<IFeatureRelation> relations =
//				inputContainsKey(IN_RELATIONS) ?
//						relations(inSource, inTarget, (FeatureRelationCollection) getInput(IN_RELATIONS), bDropRelations) :
//						relations(inSource, inTarget);
//
//		//return
//		setOutput(OUT_RELATIONS, relations);
//
//	}
//
//	@Override
//	protected IRelationMeasurement[] getMeasurements(IFeature reference, IFeature target){
//		//get geometries
//		Geometry gReference = ((GTVectorFeature) reference).getDefaultGeometry();
//		Geometry gTarget = ((GTVectorFeature) target).getDefaultGeometry();
//		if(gReference.isEmpty() || gTarget.isEmpty())
//			return null;
//		//get overlap
//		IntersectionMatrix matrix = gReference.relate(gTarget);
//		//check for overlap
//		if(!matrix.isDisjoint()){
//			return getMeasurements(new RelationMeasurement(
//					null,
//					RDFVocabulary.PROPERTY_GEOM.getResource(),
//					RDFVocabulary.PROPERTY_GEOM.getResource(),
//					new StringLiteral(matrix.toString()),
//					de9imDescription));
//		}
//		else
//			return null;
//	}
//
//	@Override
//	public String getProcessIdentifier() {
//		return this.getClass().getSimpleName();
//	}
//
//	@Override
//	public String getProcessTitle() {
//		return "Topology relation calculation";
//	}
//
//	@Override
//	public String getTextualProcessDescription() {
//		return "Calculates feature relation based on topology relation of geometries (DE9-IM model)";
//	}
//
//	@Override
//	public Collection<IProcessConstraint> getProcessConstraints() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Collection<IInputDescription> getInputDescriptions() {
//		if(inputDescriptions == null){
//			inputDescriptions = new HashSet<IInputDescription>();
//			inputDescriptions.add(new InputDescription(
//					IN_SOURCE, IN_SOURCE, "Reference features",
//					new IDataConstraint[]{
//							ContraintFactory.getMandatoryConstraint(IN_SOURCE),
//							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
//					}));
//			inputDescriptions.add(new InputDescription(IN_TARGET, IN_TARGET, "Target features",
//					new IDataConstraint[]{
//							ContraintFactory.getMandatoryConstraint(IN_TARGET),
//							ContraintFactory.getBindingConstraint(new Class<?>[]{GTFeatureCollection.class})
//					}));
//			inputDescriptions.add(new InputDescription(IN_RELATIONS, IN_RELATIONS, "If set, relation measures are added to existing relations (reference and target inputs are ignored)",
//					new IDataConstraint[]{
//							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
//					}));
//			inputDescriptions.add(new InputDescription(IN_DROP_RELATIONS, IN_DROP_RELATIONS, "If true, relations with disjoint features are dropped",
//					new IDataConstraint[]{
//							ContraintFactory.getBindingConstraint(new Class<?>[]{BooleanLiteral.class})
//					},
//					new BooleanLiteral(false)));
//		}
//		return inputDescriptions;
//	}
//
//	@Override
//	public Collection<IOutputDescription> getOutputDescriptions() {
//		if(outputDescriptions == null){
//			outputDescriptions = new HashSet<IOutputDescription>();
//			outputDescriptions.add(new OutputDescription(
//					OUT_RELATIONS, OUT_RELATIONS, "Output relations with geometry distance relation",
//					new IDataConstraint[]{
//							ContraintFactory.getMandatoryConstraint(OUT_RELATIONS),
//							ContraintFactory.getBindingConstraint(new Class<?>[]{FeatureRelationCollection.class})
//					}));
//		}
//		return outputDescriptions;
//	}
//
//}
