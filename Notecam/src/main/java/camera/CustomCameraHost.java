package camera;

import android.content.Context;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

import java.io.File;

import helper.Singleton;
import model.Foto;

public class CustomCameraHost extends SimpleCameraHost {


    public CustomCameraHost(Context _ctxt) {

        super(_ctxt);
    }

    @Override
    protected File getPhotoDirectory() {
        return new File(Singleton.NOTECAM_FOLDER + "/" + Singleton.getMateria_selecionada().getName() + "/" + Singleton.getMateria_selecionada().getName() +  "-" + Singleton.getTopico_selecionado().getName());
    }

    @Override
    public void saveImage(PictureTransaction xact, byte[] image) {
        super.saveImage(xact, image);

        Foto foto = new Foto(getPhotoName(), getPhotoDirectory() + "/" + getPhotoFilename(), Singleton.getTopico_selecionado());
        Singleton.escanear_foto(foto, Singleton.getTopico_selecionado());
    }

    @Override
    protected String getPhotoFilename() {
        return getPhotoName() + ".jpg";
    }

    public String getPhotoName(){
        return Singleton.getTopico_selecionado().getName() + "_" + get_proxima_foto_numero();
    }

    static public int get_proxima_foto_numero(){
        int ultima_foto_numero = 0;
        if(Singleton.getTopico_selecionado().getFotos() != null && Singleton.getTopico_selecionado().getFotos().size() > 0)
            ultima_foto_numero = Integer.parseInt(Singleton.getTopico_selecionado().getFotos().get(0).getName().split("_")[1]) + 1;
        return ultima_foto_numero;
    }

    @Override
    protected boolean scanSavedImage() {
        return false;
    }
}
