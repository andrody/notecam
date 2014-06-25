package helper;

import android.content.Context;
import android.os.AsyncTask;

public class CustomAsyncTask extends AsyncTask<Context, Integer, String> {
    // bla bla bla, some stuff...
    String path_gerado;

    public void updateProgress(int i) {
        publishProgress(i);
    }

    public void updatePathGerado(String path){
        this.path_gerado = path;
    }

    @Override
    protected String doInBackground(Context... contexts) {
        return null;
    }
}
