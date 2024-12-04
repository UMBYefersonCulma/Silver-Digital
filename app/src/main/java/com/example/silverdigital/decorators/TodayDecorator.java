package com.example.silverdigital.decorators;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.example.silverdigital.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class TodayDecorator implements DayViewDecorator {

    private final CalendarDay today;
    private final Drawable backgroundDrawable;
    private final Context context;

    public TodayDecorator(Context context, CalendarDay today) {
        this.context = context;
        this.today = today;
        // Usa el contexto para obtener el recurso drawable
        this.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.circle_blue);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Aplica el fondo del círculo
        view.setBackgroundDrawable(backgroundDrawable);
        // Cambia el color del texto a blanco
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.white)));
    }
}