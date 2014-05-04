package photo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;

import com.koruja.notecam.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lapada on 03/05/14.
 */
public class PictureTaker {

    //Constantes
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String PHOTO_MIME_TYPE = "image/png";
    private static final String TAG = "PictureTaker";

    //Referência para a activity
    private Activity activity;
    //Nome da pasta onde a img será salva
    private String folderName;

    //Construtor
    public PictureTaker(Activity activity) {
        this.activity = activity;
    }

    //Use esse método para tirar uma foto e salvá-la na pasta FolderName
    public void TakePicture(String folderName)
    {
        //Guarda o nome da pasta
        this.folderName = folderName;
        //salva o intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Chama a camera
        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    //Chame obrigatoriamente esse método dentro do OnActivityResult da mesma activity
    //na qual vc chamou o takePicture
    public void OnActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == activity.RESULT_OK) {
            // Determina o caminho e os metadados da foto
            final long currentTimeMillis = System.currentTimeMillis();
            final String appName = activity.getString(R.string.app_name);
            final String galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            final String albumPath = galleryPath + "/" + appName + "/" + folderName;
            final String photoPath = albumPath + "/" + currentTimeMillis + ".png";
            final ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, photoPath);
            values.put(MediaStore.Images.Media.MIME_TYPE, PHOTO_MIME_TYPE);
            values.put(Images.Media.TITLE, appName);
            values.put(Images.Media.DESCRIPTION, appName);
            values.put(Images.Media.DATE_TAKEN, currentTimeMillis);
            // Cria o diretório
            File album = new File(albumPath);
            if (!album.isDirectory() && !album.mkdirs()) {
                Log.e(TAG, "Failed to create album directory at " + albumPath);
                return;
            }
            // Pega a foto
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            //Salva a foto no arquivo
            try {
                album.createNewFile();
                FileOutputStream fo = new FileOutputStream(album);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to save image in " + albumPath);
            }

            // Salva na MediaStore
            Uri uri;
            try {
                uri = activity.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
            } catch (final Exception e) {
                Log.e(TAG, "Failed to insert photo into MediaStore");
                e.printStackTrace();
                // Since the insertion failed, delete the photo.
                File photo = new File(photoPath);
                if (!photo.delete()) {
                    Log.e(TAG, "Failed to delete non-inserted photo");
                }
                return;
            }
        }
    }
}
