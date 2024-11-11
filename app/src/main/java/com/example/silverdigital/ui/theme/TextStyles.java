package com.example.silverdigital.ui.theme;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

public class TextStyles {

    // Estilo para bodyLarge
    public static CharacterStyle bodyLarge() {
        return new CharacterStyle() {
            @Override
            public void updateDrawState(TextPaint tp) {
                tp.setTypeface(Typeface.DEFAULT);
                tp.setTextSize(16 * tp.density); // tamaño de texto en sp
                tp.setLetterSpacing(0.05f);
                tp.setFakeBoldText(false); // simula FontWeight.Normal
            }
        };
    }

    // Estilo para titleLarge
    public static CharacterStyle titleLarge() {
        return new CharacterStyle() {
            @Override
            public void updateDrawState(TextPaint tp) {
                tp.setTypeface(Typeface.DEFAULT);
                tp.setTextSize(22 * tp.density); // tamaño de texto en sp
                tp.setLetterSpacing(0);
                tp.setFakeBoldText(false);
            }
        };
    }

    // Estilo para labelSmall
    public static CharacterStyle labelSmall() {
        return new CharacterStyle() {
            @Override
            public void updateDrawState(TextPaint tp) {
                tp.setTypeface(Typeface.DEFAULT);
                tp.setTextSize(11 * tp.density); // tamaño de texto en sp
                tp.setLetterSpacing(0.05f);
                tp.setFakeBoldText(true); // simula FontWeight.Medium
            }
        };
    }
}