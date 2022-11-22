package edu.wm.cs.cs301.DuohanXu.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;


public class MazePanel extends View {
    private Bitmap bitmap;
    private Paint paint;
    private Canvas canvas;
    private int color;

    public MazePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

    }

    private void p6Test(Canvas c){


        setColor(Color.BLACK);
        DrawRectangle(0, 0, 1000, 500);

        setColor(Color.GRAY);
        DrawRectangle(0, 500, 1000, 500);

        setColor(Color.GREEN);
        int[] x = new int[]{0, 300, 300,0};
        int[] y = new int[]{0, 200, 800,1000};
        DrawPolygon(x, y, 4);

        setColor(Color.YELLOW);
        int[] x2 = new int[]{700, 1000, 1000, 700};
        int[] y2 = new int[]{200, 0, 1000, 800};
        DrawPolygon(x2, y2, 4);

        setColor(Color.RED);
        DrawOval(300, 300, 400, 400);
    }

    public void setColor(int rgb) {
        paint.setColor(rgb);
        color = rgb;
    }

    @Override
    public void onDraw(Canvas canvas) {
        p6Test(canvas);
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap,0,0, null);
    }


    public void DrawOval(int x, int y, int width, int height) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float right = width + x;
        float bottom = height + y;
        canvas.drawOval((float) x, (float) y, right, bottom, paint);

    }
    public void DrawRectangle(int x, int y, int width, int height) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float right = x + width;
        float bottom = y + height;
        canvas.drawRect((float) x, (float) y, right, bottom, paint);
    }

    public void DrawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Path path = new Path();
        path.reset();
        if(xPoints != null & yPoints != null){
            path.moveTo(xPoints[0], yPoints[0]);
            for(int i = 1; i < nPoints; i++){
                path.lineTo((float)xPoints[i], (float)yPoints[i]);
            }
            path.close();
            canvas.drawPath(path, paint);
        }

    }
}
