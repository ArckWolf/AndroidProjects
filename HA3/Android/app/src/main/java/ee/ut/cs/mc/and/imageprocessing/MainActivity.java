package ee.ut.cs.mc.and.imageprocessing;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jabistudio.androidjhlabs.filter.BoxBlurFilter;
import com.jabistudio.androidjhlabs.filter.util.AndroidUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String HEROKU_URL = "http://android-image-processing.herokuapp.com/filter"; //TODO
    private Context mContext;
    Uri currImageURI;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int PICK_FROM_GALLERY = 2;
    Bitmap thumbnail = null;
    Uri selectedImage;
    String picturePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
    }

    public void localButtonClicked(View v){
        Toast.makeText(this, "Blurring image...", Toast.LENGTH_LONG).show();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.kutsu_juku);
        new LocalTask().execute(bitmap);
    }
    public void cloudButtonClicked(View v){
        // TODO!
        Intent in = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(in, RESULT_LOAD_IMAGE);
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
            Log.e("******","START");
            try
            {
                String path = getResources().openRawResource(R.raw.kutsu_juku).toString(); //"android.resource://" + getPackageName() + "/" + R.raw.kutsu_juku;
                Log.e("******",path);

                HttpClient client = new DefaultHttpClient();

                File file = new File(picturePath);
                HttpPost post = new HttpPost(HEROKU_URL);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addPart("uploaded_file", new FileBody(file));

                HttpEntity entity = entityBuilder.build();
                post.setEntity(entity);

                HttpResponse response = client.execute(post);
                HttpEntity httpEntity = response.getEntity();

                Log.e("result", EntityUtils.toString(httpEntity));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            Log.e("******","END");

            return inputBitmap;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();
            Log.e("******selectedImage ",selectedImage.toString());

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            Log.e("******picturePath ",picturePath);

            cursor.close();
            thumbnail = (BitmapFactory.decodeFile(picturePath));

            new CloudTask().execute(thumbnail);
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