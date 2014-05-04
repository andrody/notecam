package photo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
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
    //Nome do arquivo da foto
    private String photoName;

    //Construtor
    public PictureTaker(Activity activity) {
        this.activity = activity;
    }

    //Use esse método para tirar uma foto e salvá-la na pasta FolderName
    public void TakePicture(String folderName, String photoName)
    {
        //guarda o nome do arquivo
        //(garante que tenha 3 caracteres)
        if (photoName.length() == 1)
            this.photoName = "00" + photoName;
        else if (photoName.length() == 2)
            this.photoName = "0" + photoName;
        else
            this.photoName = photoName;
        //Guarda o nome da pasta
        this.folderName = folderName;

        dispatchTakePictureIntent();
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        final String imagesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        final String appname = "Notecam";
        final String photoFolder = imagesFolder + "/" + appname + "/" + folderName;
        File storageDir = new File(photoFolder);
        if (!storageDir.isDirectory())
        {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                photoName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "Erro ao criar arquivo...");
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void OnActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Toast.makeText(activity, "Result code = " + resultCode + ". Você tirou foto", Toast.LENGTH_LONG).show();
    }
}
