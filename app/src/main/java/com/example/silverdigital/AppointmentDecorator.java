package com.example.silverdigital;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Collection;
import java.util.HashSet;

public class AppointmentDecorator implements DayViewDecorator {

    private final HashSet<CalendarDay> dates;

    public AppointmentDecorator(Collection<CalendarDay> dates) {
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // Cambiar el color de fondo o el estilo de la fecha
        view.addSpan(new ForegroundColorSpan(Color.RED)); // Cambiar el color del texto
        // view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.circle_highlight)); // Opcional: agregar fondo personalizado
    }
}