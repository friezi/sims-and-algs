/**
 * 
 */
package de.zintel.sim.nbodies;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;

import de.zintel.physics.Body;
import de.zintel.physics.BodyParameters;

/**
 * @author friedemann.zintel
 *
 */
public class ParameterSerializer extends BodyConsumer {

	private static final String FIELD_SEPARATOR = "#";
	public static final String BODY_SEPARATOR = ";";

	private BufferedWriter writer;

	/**
	 * @throws IOException
	 * 
	 */
	public ParameterSerializer(final String filename) throws IOException {

		writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(Paths.get(filename),
				StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING))));
	}

	@Override
	public void accept(Collection<Body> bodies) {
		try {

			int cnt = 0;
			for (final Body body : bodies) {

				final BodyParameters parameters = body.getParameters();

				if (cnt++ > 0) {
					writer.write(BODY_SEPARATOR);
				}
				try {

					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream outstream = new ObjectOutputStream(baos);

					outstream.writeObject(parameters);
					outstream.close();

					writer.write(Base64.getEncoder().encodeToString(baos.toByteArray()));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			writer.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

}
