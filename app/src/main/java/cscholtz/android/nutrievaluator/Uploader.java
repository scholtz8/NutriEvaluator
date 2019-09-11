package cscholtz.android.nutrievaluator;

import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class Uploader {

    private StorageReference storageReference;

    public Uploader(){
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void UploadFile(File file){

        Uri uri_file = Uri.fromFile(file);
        StorageReference stg = storageReference.child(file.getName());
        stg.putFile(uri_file);
    }

}
