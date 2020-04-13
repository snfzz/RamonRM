package com.ramon.ramonrm.charts;

import android.graphics.Color;

public class ChartResource {
    private static int[] colors = new int[]
            {
                    Color.parseColor("#F16B80"),
                    Color.parseColor("#F8C3CD"),
                    Color.parseColor("#F75C2F"),
                    Color.parseColor("#9A5034"),
                    Color.parseColor("#DDA52D"),
                    Color.parseColor("#838A2D"),
                    Color.parseColor("#90B44B"),
                    Color.parseColor("#00896C"),
                    Color.parseColor("#81C7D4"),
                    Color.parseColor("#005CAF"),
                    Color.parseColor("#8A6BBF"),
                    Color.parseColor("#F03C8A"),
                    Color.parseColor("#707C74"),
                    Color.parseColor("#9B90C2"),
                    Color.parseColor("#6F3381"),
            };
    public static int getColor(int index) {
        if (index < 0) return 0;
        index = index % colors.length;
        return colors[index];
    }
}
