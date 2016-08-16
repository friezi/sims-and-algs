/**
 * 
 */
package de.zintel.control;

/**
 * @author Friedemann
 *
 */
public interface IKeyAction {

	String textID();

	String text();

	String getValue();

	boolean withAction();

	void plus();

	void minus();

	boolean toggleComponent();

}
