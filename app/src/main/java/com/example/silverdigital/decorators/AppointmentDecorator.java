package com.example.silverdigital.decorators;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.example.silverdigital.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;
import java.util.List;

public class AppointmentDecorator implements DayViewDecorator {

    private final Drawable backgroundDrawable;
    private final HashSet<CalendarDay> dates;
    private final Context context;

    public AppointmentDecorator(Context context, List<CalendarDay> dates) {
        this.context = context;
        this.dates = new HashSet<>(dates);
        // Usa el contexto para obtener el recurso drawable
        this.backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.circle_red);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Aplica el fondo del círculo
        view.setBackgroundDrawable(backgroundDrawable);
        // Cambia el color del texto a blanco
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.white)));
    }
}