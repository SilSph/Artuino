package polito.artuino;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class FilterActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
    }

    private boolean sourceAlreadyRead = false;

    private ImageView imageView1;

    private Uri uriImageCropped;
    private InputStream stream = null;
    private Mat source = new Mat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        imageView1 = (ImageView) findViewById(R.id.imageView1);

        setImageView();

        handleFilter();
    }

    private void handleFilter() {

        try {
            stream = getContentResolver().openInputStream(uriImageCropped);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(!sourceAlreadyRead) {
            handleBitmap(stream);
            sourceAlreadyRead = true;
        }

        adaptiveThresholdMethod();
    }

    private void setImageView() {

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("uriImageCropped")) {
            uriImageCropped = Uri.parse(extras.getString("uriImageCropped"));
        }

        try {
            imageView1.setImageURI(uriImageCropped);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void handleBitmap(InputStream stream) {

        /* Creo bitmap a partire dall'URI */
        BitmapFactory.Options bmpImageInputFactoryOptions = new BitmapFactory.Options();
        bmpImageInputFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap originalBitmapImage = BitmapFactory.decodeStream(stream, null, bmpImageInputFactoryOptions);

        /* Converto l'immagine bitmap in una matrice */
        Utils.bitmapToMat(originalBitmapImage, source);
    }

    private void adaptiveThresholdMethod() {
        /* Creo una nuova matrice a partire dalla matrice sorgente */
        Mat gray = new Mat();
        /* Converto la matrice sorgente in scala di grigi */
        Imgproc.cvtColor(source, gray, Imgproc.COLOR_BGR2GRAY);

        /* Applico una sfocatura all'immagine in scale di grigi */
        Imgproc.medianBlur(gray, gray, 5);
        //Imgproc.GaussianBlur(gray, gray, new Size(5,5), 0);

        /* Creo la matrice che conterrà il risultato finale */
        Mat result = new Mat();
        /* Applico il threshold alla matrice in scala di grigi ottenendo un'immagine binaria */
        Imgproc.adaptiveThreshold(gray, result, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 51, 10);

        /* Creo l'immagine bitmap a partire dalla matrice dell'immagine binaria */
        Bitmap filterImage = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
        /* Converto la matrice a bitmap */
        Utils.matToBitmap(result, filterImage);
    }

    public void goBack(View view) {
        finish();
    }

    public void sendArduino() {

    }
}
