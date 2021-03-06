package camera;

import android.content.Context;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.koruja.notecam.R;

import java.io.File;

import helper.Singleton;
import model.Foto;

public class CustomCameraHost extends SimpleCameraHost {


    public CustomCameraHost(Context _ctxt) {

        super(_ctxt);
    }

    @Override
    protected File getPhotoDirectory() {
        return new File(Singleton.NOTECAM_FOLDER + "/" + Singleton.getMateria_selecionada().getOriginal_name() + "/" + Singleton.getMateria_selecionada().getOriginal_name() +  "-" + Singleton.getTopico_selecionado().getOriginal_name());
    }

    @Override
    public void saveImage(PictureTransaction xact, byte[] image) {
        super.saveImage(xact, image);

        Foto foto = new Foto(getPhotoName(), getPhotoDirectory() + "/" + getPhotoFilename(), Singleton.getTopico_selecionado());
        Singleton.escanear_foto(foto, Singleton.getTopico_selecionado());


        Singleton.toast(Singleton.getMateriasActivity().getString(R.string.foto_salva));
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
        if(Singleton.getTopico_selecionado().getFotos() != null && Singleton.getTopico_selecionado().getFotos().size() > 0) {
            int size = Singleton.getTopico_selecionado().getFotos().size();
            ultima_foto_numero = Integer.parseInt(Singleton.getTopico_selecionado().getFotos().get(size - 1).getName().split("_")[1]) + 1;
        }
        return ultima_foto_numero;
    }

    @Override
    protected boolean scanSavedImage() {
        return false;
    }

    @Override
    public float maxPictureCleanupHeapUsage() {
        return 0.9f;
    }
}
