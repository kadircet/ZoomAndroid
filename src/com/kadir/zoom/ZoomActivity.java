package com.kadir.zoom;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ZoomActivity extends Activity implements OnTouchListener
{
	final String TAG = "zoom";

	Matrix matrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ImageView view = (ImageView) findViewById(R.id.imageView1);
		view.setOnTouchListener(this);
	}
	
	PointF lastpos = new PointF();
	PointF mid = new PointF();
	float distance = 0.0f;
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		ImageView view = (ImageView)v;
		
		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			lastpos.set(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			switch(mode)
			{
			case DRAG:
				matrix.postTranslate(event.getX() - lastpos.x, event.getY() - lastpos.y);
				lastpos.set(event.getX(), event.getY());
				break;
			case ZOOM:
				float scale = spacing(event) / distance;
				mid = findMid(event);
				matrix.postScale(scale, scale, mid.x, mid.y);
				distance = spacing(event);
				break;
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			distance = spacing(event);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = DRAG;
			break;
		}
		
		view.setImageMatrix(matrix);
		return true;
	}
	
	private float spacing(MotionEvent e)
	{
		float x = e.getX(0) - e.getX(1);
		float y = e.getY(0) - e.getY(1);
		return FloatMath.sqrt(x*x + y*y);
	}
	
	private PointF findMid(MotionEvent e)
	{
		return new PointF((e.getX(0) + e.getX(1))/2, (e.getY(0) + e.getY(1))/2);
	}

	/*private void dumpEvent(MotionEvent event)
	{
		String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
				"POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_" ).append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP)
		{
			sb.append("(pid " ).append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")" );
		}
		sb.append("[" );
		for (int i = 0; i < event.getPointerCount(); i++)
		{
			sb.append("#" ).append(i);
			sb.append("(pid " ).append(event.getPointerId(i));
			sb.append(")=" ).append((int) event.getX(i));
			sb.append("," ).append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";" );
		}
		sb.append("]" );
		Log.d(TAG, sb.toString());
	}*/
}