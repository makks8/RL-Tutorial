package rltut;

import java.awt.Color;

import asciiPanel.AsciiPanel;

public enum Tile {
	FLOOR((char) 250, AsciiPanel.yellow),
	WALL((char) 177, AsciiPanel.yellow),
	BOUNDS('x', AsciiPanel.brightBlack),
	STAIRS_DOWN('>', AsciiPanel.white),
	STAIRS_UP('<', AsciiPanel.white);

	private char glyph;

	private Color color;

	Tile(char glyph, Color color) {
		this.glyph = glyph;
		this.color = color;
	}

	public Color color() {
		return color;
	}

	public char glyph() {
		return glyph;
	}

	public boolean isDiggable() {
		return this == Tile.WALL;
	}

	public boolean isGround() {
		return this != WALL && this != BOUNDS;
	}
}
