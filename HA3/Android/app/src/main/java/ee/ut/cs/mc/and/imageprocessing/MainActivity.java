package ee.ut.cs.mc.and.imageprocessing;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jabistudio.androidjhlabs.filter.BoxBlurFilter;
import com.jabistudio.androidjhlabs.filter.util.AndroidUtils;

public class MainActivity extends AppCompatActivity {
    private static final String HEROKU_URL = null; //TODO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void localButtonClicked(View v){
        Toast.makeText(this, "Blurring image...", Toast.LENGTH_LONG).show();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.kutsu_juku);
        new LocalTask().execute(bitmap);
    }
    public void cloudButtonClicked(View v){
        // TODO!
        Toast.makeText(this, "TODO: Start execution of your CloudTask here!", Toast.LENGTH_LONG).show();
    }

    /** Our abstract AsyncTask for image processing.
     *  It will display the processing result in a UI dialog.
     *  Any implementation of this abstract class must define the
     *  processImage() method, see the @LocalTask example below
     */
    abstract class ImageProcessingAsyncTask extends AsyncTask<Bitmap, Void, Bitmap> {

        abstract Bitmap processImage(Bitmap inputImage);

        protected Bitmap doInBackground(Bitmap... bitmaps) {
            return processImage(bitmaps[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);
            showImage(bmp);
        }
    }

    /** Send file to Heroku app and display the response image in UI */
    private class CloudTask extends ImageProcessingAsyncTask {
        //TODO
        @Override
        protected Bitmap processImage(Bitmap inputBitmap) {
            //TODO send an image to the cloud, convert the response to a Bitmap, return the Bitmap
            return null;
        }
    }

    /** --- Code below this comment does not need to be changed --- */

    private class LocalTask extends ImageProcessingAsyncTask {

        @Override
        protected Bitmap processImage(Bitmap bmp) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pixels = AndroidUtils.bitmapToIntArray(bmp);

            BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
            boxBlurFilter.setRadius(10);
            boxBlurFilter.setIterations(10);
            int[] result = boxBlurFilter.filter(pixels, width, height);

            return Bitmap.createBitmap(result, width, height, Bitmap.Config.ARGB_8888);
        }
    }


    /** Opens a pop-up dialog displaying the argument Bitmap image */
    public void showImage(Bitmap bmp) {
        Dialog builder = new Dialog(this);
        ImageView imageView = new ImageView(this);

        //Scale down the bitmap to avoid bitmap memory errors, set it to the imageview.
        //This code scales it such that the width is 1280
        int nh = (int) ( bmp.getHeight() * (1280.0 / bmp.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bmp, 1280, nh, true);
        imageView.setImageBitmap(scaled);

        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }
}