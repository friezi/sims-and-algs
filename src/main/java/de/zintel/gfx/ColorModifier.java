package de.zintel.gfx;

import java.awt.Color;

public interface ColorModifier<T> {
	Color getColor(T elem);
}