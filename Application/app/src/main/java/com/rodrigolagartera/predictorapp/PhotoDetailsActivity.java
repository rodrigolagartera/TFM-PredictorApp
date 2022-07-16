package com.rodrigolagartera.predictorapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PhotoDetailsActivity extends AppCompatActivity {

    private ImageView img;
    private Spinner localization, shape, colour, unique, multiple, edges, indurated_edges, exophytic,
            ulcerated, mixed, consistency, pain, cervical_lymph_nodes;
    private EditText size, evolution_time;
    private CheckBox insertar_otra_foto;

    private Map<String,Object> clinicalDetails, currentPhoto;
    private ArrayList photoDetails;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_details);

        if (ContextCompat.checkSelfPermission(PhotoDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PhotoDetailsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }

        localization = (Spinner) findViewById(R.id.localization);
        shape = (Spinner) findViewById(R.id.shape);
        colour = (Spinner) findViewById(R.id.colour);
        size = (EditText) findViewById(R.id.size);
        unique = (Spinner) findViewById(R.id.unique);
        multiple = (Spinner) findViewById(R.id.multiple);
        edges = (Spinner) findViewById(R.id.edges);
        indurated_edges = (Spinner) findViewById(R.id.indurated_edges);
        exophytic = (Spinner) findViewById(R.id.exophytic);
        ulcerated = (Spinner) findViewById(R.id.ulcerated);
        mixed = (Spinner) findViewById(R.id.mixed);
        consistency = (Spinner) findViewById(R.id.consistency);
        evolution_time = (EditText) findViewById(R.id.evolution_time);
        pain = (Spinner) findViewById(R.id.pain);
        cervical_lymph_nodes = (Spinner) findViewById(R.id.cervical_lymph_nodes);
        img = (ImageView)findViewById(R.id.imageView);
        insertar_otra_foto = (CheckBox) findViewById(R.id.insertar_otra_foto);

        String [] options_localization = {"Localization", "Buccal mucosa", "Dorsum of the tongue",
                "Lateral side of the tongue", "Back of the tongue", "Tip of the tongue",
                "Inner side of lower lip", "Inner side of upper lip", "Outer side of lower lip",
                "Outer side of upper lip", "Vestibule fundus", "Gingiva", "Floor of the mouth",
                "Alveolar ridge", "Hard palate", "Soft palate", "Oropharynx", "Retromolar area"};
        String [] options_shape = {"Shape", "Rounded", "Regular", "Irregular", "Diffused", "Speckled",
                "Verrucous appearance", "Cauliflower like surface", "Plaque", "Desquamative gingivitis",
                "Striae"};
        String [] options_colour = {"Colour","White", "Red", "White/Red", "Similar to adjacent mucosa", "Yellowish"};
        String [] options_unique = {"Unique","Yes", "No"};
        String [] options_multiple = {"Multiple","Yes", "No"};
        String [] options_edges = {"Edges","Hard", "Soft"};
        String [] options_indurated_edges = {"Indurated edges","Yes", "No"};
        String [] options_exophytic = {"Exophytic","Yes", "No"};
        String [] options_ulcerated = {"Ulcerated","Yes", "No"};
        String [] options_mixed = {"Mixed","Yes", "No"};
        String [] options_consistency = {"Consistency","Hard", "Soft"};
        String [] options_pain = {"Pain","Yes", "No"};
        String [] options_cervical_lymph_nodes = {"Cervical lymph nodes","Yes", "No", "Do not know"};

        ArrayAdapter<String> localization_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_localization);
        ArrayAdapter<String> shape_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_shape);
        ArrayAdapter<String> colour_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_colour);
        ArrayAdapter<String> unique_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_unique);
        ArrayAdapter <String> multiple_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_multiple);
        ArrayAdapter <String> edges_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_edges);
        ArrayAdapter <String> indurated_edges_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_indurated_edges);
        ArrayAdapter<String> exophytic_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_exophytic);
        ArrayAdapter<String> ulcerated_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_ulcerated);
        ArrayAdapter<String> mixed_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_mixed);
        ArrayAdapter<String> consistency_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_consistency);
        ArrayAdapter<String> pain_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_pain);
        ArrayAdapter<String> cervical_lymph_nodes_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options_cervical_lymph_nodes);

        localization.setAdapter(localization_adapter);
        shape.setAdapter(shape_adapter);
        colour.setAdapter(colour_adapter);
        unique.setAdapter(unique_adapter);
        multiple.setAdapter(multiple_adapter);
        edges.setAdapter(edges_adapter);
        indurated_edges.setAdapter(indurated_edges_adapter);
        exophytic.setAdapter(exophytic_adapter);
        ulcerated.setAdapter(ulcerated_adapter);
        mixed.setAdapter(mixed_adapter);
        consistency.setAdapter(consistency_adapter);
        pain.setAdapter(pain_adapter);
        cervical_lymph_nodes.setAdapter(cervical_lymph_nodes_adapter);

    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    public void tomarFoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.rodrigolagartera.android.fileprovider",
                        photoFile);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            img.setImageBitmap(imageBitmap);
        }
    }

    private String convertirImgString(ImageView imageView){
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, array);
        byte[] imageByte = array.toByteArray();
        String imageString = Base64.encodeToString(imageByte, Base64.DEFAULT);

        return imageString;
    }


    //@RequiresApi(api = Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void buttonRegistrar(View view){
        Intent i = new Intent(this, PhotoDetailsActivity.class);
        Intent iMenu = new Intent(this, MenuActivity.class);
        boolean send = true;

        String valor_localization = localization.getSelectedItem().toString();
        String valor_shape = shape.getSelectedItem().toString();
        String valor_colour = colour.getSelectedItem().toString();
        String valor_size = size.getText().toString();
        Double valor_size_number;
        if (TextUtils.isEmpty(valor_size))
            valor_size_number = 0.0;
        else{
            valor_size_number = Double.parseDouble(valor_size);
            if (valor_size_number < 0.0)
                valor_size_number = 0.0;
        }
        String valor_unique = unique.getSelectedItem().toString();
        String valor_multiple = multiple.getSelectedItem().toString();
        String valor_edges = edges.getSelectedItem().toString();
        String valor_indurated_edges = indurated_edges.getSelectedItem().toString();
        String valor_exophytic = exophytic.getSelectedItem().toString();
        String valor_ulcerated = ulcerated.getSelectedItem().toString();
        String valor_mixed = mixed.getSelectedItem().toString();
        String valor_consistency = consistency.getSelectedItem().toString();
        String valor_evolution_time = evolution_time.getText().toString();
        String valor_pain = pain.getSelectedItem().toString();
        String valor_cervical_lymph_nodes = cervical_lymph_nodes.getSelectedItem().toString();
        String valor_image= convertirImgString(img);
        //byte[] imageByte = Base64.decode(valor_image, Base64.DEFAULT);
        //StorageReference storageRef = db.getReference();

        /*if (valor_localization.equals("Localization") || valor_shape.equals("Shape") || valor_unique.equals("Unique")
                || valor_colour.equals("Colour") || valor_multiple.equals("Multiple") || valor_edges.equals("Edges")
                || valor_indurated_edges.equals("Indurated edges") || valor_exophytic.equals("Exophytic")
                || valor_ulcerated.equals("Ulcerated") || valor_mixed.equals("Mixed") || valor_consistency.equals("Consistency")
                || valor_pain.equals("Pain") || valor_cervical_lymph_nodes.equals("Cervical lymph nodes"))
            send = false;*/

        if (send){
            currentPhoto = new HashMap<>();
            currentPhoto.put("localization",valor_localization);
            currentPhoto.put("shape",valor_shape);
            currentPhoto.put("colour",valor_colour);
            currentPhoto.put("size",valor_size_number);
            currentPhoto.put("unique",valor_unique);
            currentPhoto.put("multiple",valor_multiple);
            currentPhoto.put("edges",valor_edges);
            currentPhoto.put("indurated_edges",valor_indurated_edges);
            currentPhoto.put("exophytic",valor_exophytic);
            currentPhoto.put("ulcerated",valor_ulcerated);
            currentPhoto.put("mixed",valor_mixed);
            currentPhoto.put("consistency",valor_consistency);
            currentPhoto.put("evolution_time",valor_evolution_time);
            currentPhoto.put("pain",valor_pain);
            currentPhoto.put("cervical_lymph_nodes", valor_cervical_lymph_nodes);
            currentPhoto.put("img", valor_image);

            clinicalDetails = ((GlobalData) this.getApplication()).getClinicalDetails();
            photoDetails = (ArrayList) clinicalDetails.get("list_photo_details");
            photoDetails.add(currentPhoto);
            clinicalDetails.put("list_photo_details", photoDetails);
            ((GlobalData) this.getApplication()).setClinicalDetails(clinicalDetails);

            if (insertar_otra_foto.isChecked())
                startActivity(i);
            else {
                startActivity(iMenu);
            }
        }
        else
            Toast.makeText(this,"Por favor, revisa que los campos sean correctos antes de continuar", Toast.LENGTH_LONG).show();

    }
}