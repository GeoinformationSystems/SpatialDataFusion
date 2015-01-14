package de.tudresden.gis.fusion.misc;

import java.io.IOException;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.data.collection.SpatialIndexFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class STRTreeFeatureCollection extends SpatialIndexFeatureCollection {

	public STRTreeFeatureCollection(SimpleFeatureCollection fc) throws IOException {
		super(fc);
	}
	
	/**
	 * overrides subCollection from SpatialIndexFeatureCollection
	 */
	public SimpleFeatureCollection subCollection(Filter filter) {
		//only bbox filter supported
		try {
			if(filter instanceof BBOX){
				Envelope envelope = new WKTReader().read(((BBOX)filter).getExpression2().toString()).getEnvelopeInternal();
				return subCollection(envelope);
			}
			else
				throw new UnsupportedOperationException();
		} catch (ParseException pe){
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * return collection within bounds
	 * @param envelope bounds
	 * @return collection
	 */
	@SuppressWarnings("unchecked")
	public SimpleFeatureCollection subCollection(Envelope envelope) {
		List<SimpleFeature> resultList = (List<SimpleFeature>) this.index.query(envelope);
		return DataUtilities.collection(resultList);
	}
	
	/**
	 * return collection within bounds
	 * @param envelope bounds
	 * @param buffer buffer applied to bounds
	 * @return collection
	 */
	public SimpleFeatureCollection subCollection(Envelope envelope, double buffer) {
		envelope.expandBy(buffer);
		return subCollection(envelope);
	}
	
	/**
	 * return collection within bounds
	 * @param bbox bounds
	 * @return collection
	 */
	public SimpleFeatureCollection subCollection(BoundingBox bbox) {
		return subCollection(getJTSEnvelope(bbox));
	}
	
	/**
	 * return collection within bounds
	 * @param bbox bounds
	 * @param buffer buffer applied to bounds
	 * @return collection
	 */
	public SimpleFeatureCollection subCollection(BoundingBox bbox, double buffer) {
		return subCollection(getJTSEnvelope(bbox), buffer);
	}
	
	/**
	 * convert opengis BoundingBox to JTS Envelope
	 * @param bbox bounding box
	 * @return JTS envelope
	 */
	private Envelope getJTSEnvelope(BoundingBox bbox){
		return JTS.toGeometry(bbox).getEnvelopeInternal();
	}
	
}
