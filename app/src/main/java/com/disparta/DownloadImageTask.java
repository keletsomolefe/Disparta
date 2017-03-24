package com.disparta;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

	ImageView bmImage;
	int Flagss;
	CircleImage imgCirlce;
	
	
	public DownloadImageTask(ImageView bmImage, int Flagss) {
		this.bmImage = bmImage;
		this.Flagss = Flagss;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected Bitmap doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		
		String urldisplay = arg0[0];
		Bitmap mIcon11 = null;
		
		// First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        URLConnection in = null;
		try{
			
			in = new URL(urldisplay).openConnection();
			in.setDoInput(true);
			in.connect();
			
			InputStream in2 = in.getInputStream();
				//in2 = in.getInputStream();
			
			final Rect rect = new Rect(0, 0, 250, 250);
	        options.inJustDecodeBounds = true;
	        options.inPurgeable =true;
	        options.inInputShareable = true;
	        options.inScaled = true;
	        options.inPreferQualityOverSpeed = false;
	        BitmapFactory.decodeStream(in2, null, options);
	       // in2.close();
	        
	        // Calculate inSampleSize
	    	options.inSampleSize = 3;//calculateInSampleSize(options, 90, 90);
			if (Flagss == 2) {
				options.inSampleSize = calculateInSampleSize(options, 300, 300);
			}
			else if(Flagss == 3) {
				options.inSampleSize = calculateInSampleSize(options, 150, 150);
			}else if (Flagss == 5) {
				options.inSampleSize = calculateInSampleSize(options, 160, 160);
			}
	        
	        in = new URL(urldisplay).openConnection();
			in.setDoInput(true);
			in.connect();
			
			InputStream in3 = in.getInputStream();
			
	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
	        mIcon11 = BitmapFactory.decodeStream(in3, null, options);
	       // in.setConnectTimeout(900);

			System.out.println("THIS SHIT  : "+in3.toString());
		}
		catch(Exception e) {

			System.out.println(e.getMessage());
		}
		
		return mIcon11;
	}
	
	 public static int calculateInSampleSize(BitmapFactory.Options options,
	            int reqWidth, int reqHeight) {
	        // BEGIN_INCLUDE (calculate_sample_size)
	        // Raw height and width of image
	        final int height = options.outHeight;
	        final int width = options.outWidth;
	        int inSampleSize = 1;

	        if (height > reqHeight || width > reqWidth) {

	            final int halfHeight = height / 2;
	            final int halfWidth = width / 2;

	            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	            // height and width larger than the requested height and width.
	            while ((halfHeight / inSampleSize) > reqHeight
	                    && (halfWidth / inSampleSize) > reqWidth) {
	                inSampleSize *= 2;
	            }

	            // This offers some additional logic in case the image has a strange
	            // aspect ratio. For example, a panorama may have a much larger
	            // width than height. In these cases the total pixels might still
	            // end up being too large to fit comfortably in memory, so we should
	            // be more aggressive with sample down the image (=larger inSampleSize).

	            long totalPixels = width * height / inSampleSize;

	            // Anything more than 2x the requested pixels we'll sample down further
	            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

	            while (totalPixels > totalReqPixelsCap) {
	                inSampleSize *= 2;
	                totalPixels /= 2;
	            }
	        }
	        return inSampleSize;
	        // END_INCLUDE (calculate_sample_size)
	    }
	
	 public Bitmap getCircleBitmap(final Bitmap bitmap) {
		
		final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(output);
		
		final int color = Color.RED;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF, paint);
		
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		
		canvas.drawBitmap(bitmap, rect,  rect, paint);
		
		//bitmap.recycle();
		
		return output;	
	}
	
	/*@Override
	protected void onSuccess(Bitmap result) {
	
	Bitmap finalS = getCircleBitmap(result);
	
	if ( Flagss == 1 ) {
		//Bitmap img =imgCirlce.getCircleBitmap(result);
		bmImage.setAdjustViewBounds(true);
		bmImage.setImageBitmap(finalS);
	}else if ( Flagss == 2 ){
		bmImage.setImageBitmap(result);
	}
}*/

	@Override
	protected void onPostExecute(Bitmap result) {
		Bitmap finalS = null ; 
		try {
			finalS = getCircleBitmap(result);
		}
		catch(Exception e ) {
			//e.printStackTrace();
			//finalS = result;
		}
		//finalS = result;
		if ( Flagss == 1 || Flagss == 3) {
			//Bitmap img =imgCirlce.getCircleBitmap(result);
			bmImage.setAdjustViewBounds(true);
			bmImage.setImageBitmap(finalS);
		}else if ( Flagss == 2 ){
			bmImage.setImageBitmap(result);
		}
		else if ( Flagss == 5 ){
			bmImage.setAdjustViewBounds(true);
			bmImage.setImageBitmap(finalS);
		}
	}

}
