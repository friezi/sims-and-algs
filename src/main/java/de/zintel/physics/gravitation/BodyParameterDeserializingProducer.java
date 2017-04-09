/**
 * 
 */
package de.zintel.physics.gravitation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import de.zintel.gfx.g2d.Field;
import de.zintel.physics.Body;
import de.zintel.physics.BodyParameters;
import de.zintel.sim.nbodies.ParameterSerializer;

/**
 * @author friedemann.zintel
 *
 */
public class BodyParameterDeserializingProducer implements IBodyProducer {

	private final BufferedReader reader;

	private final Body referenceBody = new Body(new BodyParameters(""));

	@SuppressWarnings("serial")
	private Map<String, Body> bodies = new HashMap<String, Body>() {
		{
			put(referenceBody.getId(), referenceBody);
		}
	};

	/**
	 * @throws IOException
	 * 
	 */
	public BodyParameterDeserializingProducer(final String filename) throws IOException {
		reader = new BufferedReader(
				new InputStreamReader(new GZIPInputStream(Files.newInputStream(Paths.get(filename), StandardOpenOption.READ))));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.physics.gravitation.IBodyProducer#calculate()
	 */
	@Override
	public void calculate() {

		try {
			final String line = reader.readLine();
			if (line == null) {
				return;
			}

			final Map<String, Body> newBodies = new LinkedHashMap<>();

			final String[] parameterArray = line.split(ParameterSerializer.BODY_SEPARATOR);

			for (final String encodedParameter : parameterArray) {

				final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(Base64.getDecoder().decode(encodedParameter)));
				final BodyParameters parameters = (BodyParameters) ois.readObject();
				ois.close();

				Body body = bodies.get(parameters.id);
				if (body == null) {

					body = new Body(parameters);
					body.copyProperties(referenceBody);

				} else {
					body.setParameters(parameters);
				}

				newBodies.put(parameters.id, body);

			}

			bodies = newBodies;
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zintel.physics.gravitation.IBodyProducer#getBodies()
	 */
	@Override
	public Collection<Body> getBodies() {
		return bodies.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zintel.physics.gravitation.IBodyProducer#setField(de.zintel.gfx.g2d.
	 * Field)
	 */
	@Override
	public void setField(Field field) {

	}

	@Override
	public void shutdown() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
