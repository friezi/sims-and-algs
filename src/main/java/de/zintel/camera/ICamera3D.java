package de.zintel.camera;

import de.zintel.math.Axis3D;
import de.zintel.math.Vector3D;
import de.zintel.math.transform.CoordinateTransformation3D;
import de.zintel.utils.Pair;

/**
 * An abstract camera. Attention: transformations are always relative to the
 * camera's coordinate system, e. g. axis is always axis of the camera
 * 
 * @author friedemann.zintel
 *
 */
public interface ICamera3D<T extends ICamera3D<T>> {

	Vector3D getViewpoint();

	CoordinateTransformation3D getTransformationToCamera();

	void rotate(Axis3D axis, double angle);

	void translate(Vector3D vector);

	/**
	 * projects a world point to camera-screen
	 * 
	 * @param worldpoint
	 * @return
	 */
	Vector3D projectWorld(Vector3D worldpoint);

	/**
	 * projects a camera point to camera screen
	 * 
	 * @param camerapoint
	 * @return null if point couldn't be projected
	 */
	Vector3D projectCamera(Vector3D camerapoint);

	/**
	 * projects a camera point to camera screen
	 * 
	 * @param camerapoint
	 * @param showbehind
	 *            tells if points behind camera should be returned
	 * @return null, if showbehind==false and point is behind screen
	 */
	Vector3D projectCamera(Vector3D camerapoint, boolean showbehind);

	/**
	 * transforms a world point to camera point
	 * 
	 * @param worldpoint
	 * @return
	 */
	Vector3D toCamera(Vector3D worldpoint);

	/**
	 * transforms a camera point to world point
	 * 
	 * @param camerapoint
	 * @return
	 */
	Vector3D toWorld(Vector3D camerapoint);

	boolean inScreenRange(final Vector3D projectedpoint);

	void reset();

	T setCurvature(double value);

	double getCurvature();

	String getId();

	/**
	 * checks if a given camera point is behind it's screen
	 * 
	 * @param camerapoint
	 * @return
	 */
	boolean behindScreen(Vector3D camerapoint);

	/**
	 * calculates the intersection of the (prolonged) line given by camerap1 and
	 * camerap2 (in camera coords)
	 * 
	 * @param camerap1
	 * @param camerap2
	 * @return
	 */
	Vector3D intersect(Vector3D camerap1, Vector3D camerap2);

	/**
	 * projects a world line to screen including clipping
	 * 
	 * @param p1
	 * @param p2
	 * @return clipped screen line, null if completely hidden
	 */
	default Pair<Vector3D, Vector3D> worldLineToScreen(Vector3D p1, Vector3D p2) {
		return cameraLineToScreen(toCamera(p1), toCamera(p2));
	}

	/**
	 * projects a camera line to screen including clipping
	 * 
	 * @param p1
	 * @param p2
	 * @return clipped screen line, null if completely hidden
	 */
	default Pair<Vector3D, Vector3D> cameraLineToScreen(Vector3D cp1, Vector3D cp2) {

		final boolean hp1 = behindScreen(cp1);
		final boolean hp2 = behindScreen(cp2);
		Pair<Vector3D, Vector3D> line = null;
		if (hp1 && hp2) {
			line = null;
		} else if (!hp1 && !hp2) {
			line = new Pair<>(projectCamera(cp1, true), projectCamera(cp2, true));
		} else if (hp1) {
			line = new Pair<>(intersect(cp1, cp2), projectCamera(cp2, true));
		} else if (hp2) {
			line = new Pair<>(projectCamera(cp1, true), intersect(cp1, cp2));
		}

		return line;
	}
}