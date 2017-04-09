package de.zintel.gfx.g2d;

import java.awt.Color;

public interface ColorModifier<T> {
	Color getColor(T elem);
}