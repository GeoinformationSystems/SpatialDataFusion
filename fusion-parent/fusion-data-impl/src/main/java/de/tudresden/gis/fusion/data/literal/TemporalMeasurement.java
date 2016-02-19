package de.tudresden.gis.fusion.data.literal;

import java.time.LocalDateTime;
import de.tudresden.gis.fusion.data.IMeasurement;
import de.tudresden.gis.fusion.data.description.IMeasurementDescription;
import de.tudresden.gis.fusion.data.rdf.IIdentifiableResource;
import de.tudresden.gis.fusion.data.rdf.ITypedLiteral;
import de.tudresden.gis.fusion.data.rdf.RDFVocabulary;

public class TemporalMeasurement extends AbstractMeasurement<LocalDateTime> implements ITypedLiteral {
	
	public TemporalMeasurement(LocalDateTime instant, IMeasurementDescription description){
		super(instant, description);
	}

	@Override
	public String getValue() {
		return resolve().toString();
	}

	@Override
	public int compareTo(IMeasurement measurement) {
		if(measurement instanceof TemporalMeasurement)
			return this.resolve().compareTo(((TemporalMeasurement) measurement).resolve());
		else
			throw new ClassCastException("Cannot cast to TemporalMeasurement");
	}

	@Override
	public IIdentifiableResource getType() {
		return RDFVocabulary.MEASURMENT_TIME_INSTANT.asResource();
	}

}
