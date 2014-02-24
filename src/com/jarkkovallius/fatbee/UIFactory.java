package com.jarkkovallius.fatbee;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class UIFactory {

	public static Label constructLabel(String text, BitmapFont font, Color color) {
		Label l = new Label(text, new LabelStyle(font, color));
		return l;
	}

	public static Label constructLabel(String text, BitmapFont font) {
		Label l = new Label(text, new LabelStyle(font, Color.WHITE));
		return l;
	}

}
